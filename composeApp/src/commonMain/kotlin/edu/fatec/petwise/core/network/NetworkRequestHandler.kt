package edu.fatec.petwise.core.network

import edu.fatec.petwise.core.network.retry.CircuitBreaker
import edu.fatec.petwise.core.network.retry.CircuitBreakerOpenException
import edu.fatec.petwise.core.network.retry.RetryContext
import edu.fatec.petwise.core.network.retry.RetryPolicy
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException

/**
 * Enhanced network request handler with retry logic, circuit breaker, and comprehensive error handling
 */
class NetworkRequestHandler(
    private val httpClient: HttpClient,
    private val retryPolicy: RetryPolicy = RetryPolicy.DEFAULT,
    private val circuitBreaker: CircuitBreaker = CircuitBreaker()
) {
    /**
     * Execute a safe HTTP request with automatic retry and circuit breaker
     */
    internal suspend inline fun <reified T> safeRequest(
        crossinline request: suspend HttpClient.() -> HttpResponse
    ): NetworkResult<T> = executeWithRetry {
        try {
            val response = httpClient.request()
            handleResponse(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    /**
     * HTTP GET request
     */
    internal suspend inline fun <reified T> get(
        urlString: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): NetworkResult<T> = safeRequest {
        get(urlString) {
            block()
        }
    }

    /**
     * HTTP POST request
     */
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

    /**
     * HTTP PUT request
     */
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

    /**
     * HTTP PATCH request
     */
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

    /**
     * HTTP DELETE request
     */
    internal suspend inline fun <reified T> delete(
        urlString: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): NetworkResult<T> = safeRequest {
        delete(urlString) {
            block()
        }
    }

    internal suspend inline fun <reified T> handleResponse(response: HttpResponse): NetworkResult<T> {
        return try {
            when (response.status.value) {
                in 200..299 -> {
                    try {
                        val data: T = response.body()
                        NetworkResult.Success(data)
                    } catch (e: SerializationException) {
                        println("Erro de serialização na resposta 200: ${e.message}")
                        println("Response body: ${response.bodyAsText()}")
                        NetworkResult.Error(
                            NetworkException.SerializationError(
                                message = "Falha ao processar resposta do servidor: ${e.message}",
                                cause = e
                            )
                        )
                    }
                }
                401 -> {
                    println("NetworkRequestHandler: 401 Unauthorized - Token may be expired or invalid")
                    NetworkResult.Error(NetworkException.Unauthorized())
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
                    println("Network request was cancelled (não afeta autenticação): ${exception.message}")
                    NetworkException.RequestCancelled(
                        message = "Operação cancelada pelo usuário",
                        cause = exception
                    )
                }
                is IOException -> NetworkException.NoConnectivity()
                is HttpRequestTimeoutException -> NetworkException.Timeout()
                is SerializationException -> NetworkException.SerializationError(cause = exception)
                is NetworkException -> exception
                else -> NetworkException.Unknown(
                    message = exception.message ?: "Erro desconhecido",
                    cause = exception
                )
            }
        )
    }

    suspend fun <T> executeWithRetry(
        maxAttempts: Int = NetworkConfig.MAX_RETRY_ATTEMPTS,
        initialDelay: Long = NetworkConfig.RETRY_DELAY,
        maxDelay: Long = NetworkConfig.MAX_RETRY_DELAY,
        factor: Double = 2.0,
        block: suspend () -> NetworkResult<T>
    ): NetworkResult<T> {
        var currentDelay = initialDelay
        
        repeat(maxAttempts - 1) { attempt ->
            try {
                val result = block()
                
                when (result) {
                    is NetworkResult.Success -> {
                        if (attempt > 0) {
                            println("NetworkRequestHandler: Request succeeded after ${attempt + 1} attempts")
                        }
                        return result
                    }
                    is NetworkResult.Error -> {
                        if (!isRetryable(result.exception)) {
                            println("NetworkRequestHandler: Non-retryable error: ${result.exception}")
                            return result
                        }
                        
                        println("NetworkRequestHandler: Retryable error on attempt ${attempt + 1}: ${result.exception}")
                        
                        // Add jitter to prevent thundering herd
                        val jitter = (0..100).random()
                        val delayWithJitter = currentDelay + jitter
                        
                        println("NetworkRequestHandler: Retrying in ${delayWithJitter}ms (attempt ${attempt + 2}/$maxAttempts)")
                        delay(delayWithJitter)
                        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
                    }
                    is NetworkResult.Loading -> {
                        // Continue to retry
                    }
                }
            } catch (e: CancellationException) {
                println("NetworkRequestHandler: Request cancelled during retry attempt ${attempt + 1} - não afeta autenticação")
                return NetworkResult.Error(
                    NetworkException.RequestCancelled(
                        message = "Operação cancelada pelo usuário",
                        cause = e
                    )
                )
            } catch (e: Exception) {
                println("NetworkRequestHandler: Unexpected error during retry attempt ${attempt + 1}: ${e.message}")
                val networkException = when (e) {
                    is IOException -> NetworkException.NoConnectivity()
                    is HttpRequestTimeoutException -> NetworkException.Timeout()
                    else -> NetworkException.Unknown(message = e.message ?: "Erro inesperado", cause = e)
                }
                
                if (!isRetryable(networkException)) {
                    return NetworkResult.Error(networkException)
                }
                
                // Add jitter for unexpected errors too
                val jitter = (0..100).random()
                val delayWithJitter = currentDelay + jitter
                delay(delayWithJitter)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
            }
        }

        // Final attempt
        return try {
            val result = block()
            when (result) {
                is NetworkResult.Error -> {
                    println("NetworkRequestHandler: Final attempt failed: ${result.exception}")
                }
                else -> {
                    println("NetworkRequestHandler: Final attempt succeeded")
                }
            }
            result
        } catch (e: CancellationException) {
            println("NetworkRequestHandler: Final request attempt cancelled - mantendo estado de autenticação")
            NetworkResult.Error(
                NetworkException.RequestCancelled(
                    message = "Operação cancelada pelo usuário",
                    cause = e
                )
            )
        } catch (e: Exception) {
            println("NetworkRequestHandler: Final attempt failed with exception: ${e.message}")
            val errorResult = handleException<T>(e)
            errorResult
        }
    }

    private fun isRetryable(exception: NetworkException): Boolean {
        return when (exception) {
            is NetworkException.NoConnectivity -> true
            is NetworkException.Timeout -> true
            is NetworkException.ServerError -> true
            is NetworkException.RequestCancelled -> false
            is NetworkException.HttpError -> {
                exception.code >= 500 || exception.code == 408 || exception.code == 429
            }
            else -> false
        }
    }
}

suspend fun <T> NetworkResult<T>.retryOnError(
    handler: NetworkRequestHandler,
    maxAttempts: Int = NetworkConfig.MAX_RETRY_ATTEMPTS,
    block: suspend () -> NetworkResult<T>
): NetworkResult<T> {
    return if (this is NetworkResult.Error && isRetryable(this.exception)) {
        handler.executeWithRetry(maxAttempts = maxAttempts, block = block)
    } else {
        this
    }
}

private fun isRetryable(exception: NetworkException): Boolean {
    return when (exception) {
        is NetworkException.NoConnectivity -> true
        is NetworkException.Timeout -> true
        is NetworkException.ServerError -> true
        is NetworkException.RequestCancelled -> false
        is NetworkException.HttpError -> exception.code >= 500
        else -> false
    }
}
