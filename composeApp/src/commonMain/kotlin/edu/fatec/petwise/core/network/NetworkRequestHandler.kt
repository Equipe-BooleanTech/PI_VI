package edu.fatec.petwise.core.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

class NetworkRequestHandler(
    val httpClient: HttpClient
) {
    suspend inline fun <reified T> getWithCustomDeserializer(
        urlString: String,
        crossinline deserializer: (String) -> T,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): NetworkResult<T> {
        return try {
            val response = httpClient.get(urlString) {
                headers {
                    append(HttpHeaders.CacheControl, "no-cache")
                    append("Pragma", "no-cache")
                }
                block()
            }
            when (response.status.value) {
                in 200..299 -> {
                    val jsonString = response.bodyAsText()
                    val data = deserializer(jsonString)
                    NetworkResult.Success(data)
                }
                else -> handleResponse(response)
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }
    internal suspend inline fun <reified T> safeRequest(
        crossinline request: suspend HttpClient.() -> HttpResponse
    ): NetworkResult<T> {
        return try {
            val response = httpClient.request()
            handleResponse(response)
        } catch (e: CancellationException) {
            println("NetworkRequestHandler: Requisição cancelada (propagando CancellationException)")
            throw e
        } catch (e: Exception) {
            handleException(e)
        }
    }

    internal suspend inline fun <reified T> get(
        urlString: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): NetworkResult<T> = safeRequest {
        get(urlString) {
            headers {
                append(HttpHeaders.CacheControl, "no-cache")
                append("Pragma", "no-cache")
            }
            block()
        }
    }

    internal suspend inline fun <reified T, reified R> post(
        urlString: String,
        body: R,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): NetworkResult<T> = safeRequest {
        post(urlString) {
            contentType(ContentType.Application.Json)
            setBody(body)
            block()
        }
    }

    internal suspend inline fun <reified T, reified R> put(
        urlString: String,
        body: R,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): NetworkResult<T> = safeRequest {
        put(urlString) {
            contentType(ContentType.Application.Json)
            setBody(body)
            block()
        }
    }

    internal suspend inline fun <reified T, reified R> patch(
        urlString: String,
        body: R,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): NetworkResult<T> = safeRequest {
        patch(urlString) {
            contentType(ContentType.Application.Json)
            setBody(body)
            block()
        }
    }

    internal suspend inline fun <reified T> delete(
        urlString: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): NetworkResult<T> = safeRequest {
        delete(urlString) {
            block()
        }
    }

    suspend inline fun <reified T> handleResponse(response: HttpResponse): NetworkResult<T> {
        return try {
            when (response.status.value) {
                in 200..299 -> {
                    try {
                        val data: T = response.body()
                        NetworkResult.Success(data)
                    } catch (e: SerializationException) {
                        val bodyText = try { response.bodyAsText() } catch (ex: Exception) { "Unable to read body" }
                        println("Ktor: RESPONSE: ${response.status}")
                        println("METHOD: ${response.request.method}")
                        println("FROM: ${response.request.url}")
                        println("BODY Content-Type: ${response.contentType()}")
                        println("BODY START")
                        println(bodyText)
                        println("BODY END")
                        println("Erro de serialização na resposta 200: ${e.message}")
                        println("Expected type: ${T::class.simpleName}")
                        NetworkResult.Error(
                            NetworkException.SerializationError(
                                message = "Erro ao processar resposta: ${e.message ?: "Formato de dados inválido"}",
                                cause = e
                            )
                        )
                    }
                }
                401 -> {
                    val errorBody = response.bodyAsText()
                    println("NetworkRequestHandler: 401 Unauthorized - $errorBody")
                    
                    // Check if token is blacklisted or invalid - these require re-login
                    val isTokenInvalid = errorBody.contains("blacklist", ignoreCase = true) ||
                            errorBody.contains("invalid", ignoreCase = true) ||
                            errorBody.contains("expired", ignoreCase = true) ||
                            errorBody.contains("Token validation failed", ignoreCase = true)
                    
                    // Provide more context about the 401 error
                    val errorMessage = when {
                        errorBody.contains("blacklist", ignoreCase = true) -> 
                            "TOKEN_BLACKLISTED:Sua sessão foi encerrada. Faça login novamente."
                        errorBody.contains("token", ignoreCase = true) && errorBody.contains("expired", ignoreCase = true) -> 
                            "TOKEN_EXPIRED:Token expirado - faça login novamente"
                        errorBody.contains("invalid", ignoreCase = true) -> 
                            "TOKEN_INVALID:Credenciais inválidas - faça login novamente"
                        else -> 
                            "AUTH_ERROR:Erro temporário de autenticação - tente novamente"
                    }
                    
                    NetworkResult.Error(NetworkException.Unauthorized(message = errorMessage, requiresRelogin = isTokenInvalid))
                }
                403 -> {
                    println("NetworkRequestHandler: 403 Forbidden - Insufficient permissions")
                    NetworkResult.Error(NetworkException.Forbidden())
                }
                404 -> {
                    println("NetworkRequestHandler: 404 Not Found - Resource not found")
                    NetworkResult.Error(NetworkException.NotFound())
                }
                408 -> {
                    println("NetworkRequestHandler: 408 Request Timeout")
                    NetworkResult.Error(NetworkException.Timeout())
                }
                429 -> {
                    println("NetworkRequestHandler: 429 Too Many Requests - Rate limited")
                    NetworkResult.Error(
                        NetworkException.HttpError(
                            code = 429,
                            errorBody = "Too many requests - please try again later",
                            errorResponse = null
                        )
                    )
                }
                in 400..499 -> {
                    val errorResponse = try {
                        response.body<ApiErrorResponse>()
                    } catch (e: Exception) {
                        null
                    }
                    val errorBody = response.bodyAsText()
                    println("NetworkRequestHandler: Client error ${response.status.value} - $errorBody")
                    NetworkResult.Error(
                        NetworkException.HttpError(
                            code = response.status.value,
                            errorBody = errorBody,
                            errorResponse = errorResponse
                        )
                    )
                }
                in 500..599 -> {
                    println("NetworkRequestHandler: Server error ${response.status.value} - ${response.status.description}")
                    NetworkResult.Error(
                        NetworkException.ServerError(
                            message = "Erro do servidor (${response.status.value}): ${response.status.description}"
                        )
                    )
                }
                else -> {
                    NetworkResult.Error(
                        NetworkException.Unknown(
                            message = "Resposta inesperada: ${response.status.value}"
                        )
                    )
                }
            }
        } catch (e: SerializationException) {
            println("Erro de serialização geral: ${e.message}")
            NetworkResult.Error(
                NetworkException.SerializationError(
                    message = "Falha ao processar resposta: ${e.message}",
                    cause = e
                )
            )
        } catch (e: Exception) {
            println("Erro ao processar resposta: ${e.message}")
            e.printStackTrace()
            NetworkResult.Error(
                NetworkException.Unknown(
                    message = "Erro ao processar resposta: ${e.message}",
                    cause = e
                )
            )
        }
    }

    fun <T> handleException(exception: Exception): NetworkResult<T> {
        return NetworkResult.Error(
            when (exception) {
                is CancellationException -> {
                    println("Requisição de rede cancelada: ${exception.message}")
                    NetworkException.RequestCancelled(
                        message = "Operação cancelada",
                        cause = exception
                    )
                }
                is IOException -> NetworkException.NoConnectivity()
                is HttpRequestTimeoutException -> NetworkException.Timeout()
                is SerializationException -> NetworkException.SerializationError(cause = exception)
                is NetworkException -> exception
                is IllegalStateException -> {
                    if (exception.message?.contains("Parent job is Completed") == true ||
                        exception.message?.contains("Job is Completed") == true) {
                        println("HttpClient em estado inválido: ${exception.message}")
                        NetworkException.Unknown(
                            message = "Cliente HTTP em estado inválido. Por favor, tente novamente.",
                            cause = exception
                        )
                    } else {
                        NetworkException.Unknown(
                            message = exception.message ?: "Erro de estado desconhecido",
                            cause = exception
                        )
                    }
                }
                else -> NetworkException.Unknown(
                    message = exception.message ?: "Erro desconhecido",
                    cause = exception
                )
            }
        )
    }
}
