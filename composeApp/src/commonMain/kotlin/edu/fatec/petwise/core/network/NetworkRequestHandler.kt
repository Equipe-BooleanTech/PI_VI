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
import kotlinx.coroutines.delay
import kotlinx.serialization.SerializationException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

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
                401 -> NetworkResult.Error(NetworkException.Unauthorized())
                403 -> NetworkResult.Error(NetworkException.Forbidden())
                404 -> NetworkResult.Error(NetworkException.NotFound())
                in 400..499 -> {
                    val errorResponse = try {
                        response.body<ApiErrorResponse>()
                    } catch (e: Exception) {
                        null
                    }
                    NetworkResult.Error(
                        NetworkException.HttpError(
                            code = response.status.value,
                            errorBody = response.bodyAsText(),
                            errorResponse = errorResponse
                        )
                    )
                }
                in 500..599 -> {
                    NetworkResult.Error(
                        NetworkException.ServerError(
                            message = "Erro do servidor: ${response.status.description}"
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
            val result = block()
            
            when (result) {
                is NetworkResult.Success -> return result
                is NetworkResult.Error -> {
                    if (!isRetryable(result.exception)) {
                        return result
                    }
                }
                is NetworkResult.Loading -> {
                }
            }

            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }

        return block()
    }

    private fun isRetryable(exception: NetworkException): Boolean {
        return when (exception) {
            is NetworkException.NoConnectivity -> true
            is NetworkException.Timeout -> true
            is NetworkException.ServerError -> true
            is NetworkException.HttpError -> exception.code >= 500
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
        is NetworkException.HttpError -> exception.code >= 500
        else -> false
    }
}
