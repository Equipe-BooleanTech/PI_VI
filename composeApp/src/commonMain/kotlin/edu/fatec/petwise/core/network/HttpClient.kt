package edu.fatec.petwise.core.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun createHttpClient(config: PetWiseHttpClientConfig): HttpClient

data class PetWiseHttpClientConfig(
    val enableLogging: Boolean = NetworkConfig.enableLogging,
    val requestTimeout: Long = NetworkConfig.REQUEST_TIMEOUT,
    val connectTimeout: Long = NetworkConfig.CONNECT_TIMEOUT,
    val socketTimeout: Long = NetworkConfig.SOCKET_TIMEOUT,
    val maxRetries: Int = NetworkConfig.MAX_RETRY_ATTEMPTS,
    val authTokenProvider: (() -> String?)? = null
)

fun createDefaultHttpClient(
    config: PetWiseHttpClientConfig = PetWiseHttpClientConfig()
): HttpClient {
    return createHttpClient(config).config {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                explicitNulls = false
                encodeDefaults = true
            })
        }

        if (config.enableLogging) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("Ktor: $message")
                    }
                }
                level = LogLevel.INFO
                filter { request ->
                    request.url.host.contains("petwise")
                }
                sanitizeHeader { header -> header == HttpHeaders.Authorization.toString() }
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = config.requestTimeout
            connectTimeoutMillis = config.connectTimeout
            socketTimeoutMillis = config.socketTimeout
        }

        install(DefaultRequest) {
            url(NetworkConfig.API_URL)
            headers {
                append(io.ktor.http.HttpHeaders.ContentType, PetWiseContentTypes.JSON)
                append(io.ktor.http.HttpHeaders.Accept, PetWiseContentTypes.JSON)
                append(io.ktor.http.HttpHeaders.UserAgent, getUserAgent())
                append(PetWiseHttpHeaders.PLATFORM, getPlatformName())
            }
        }

        config.authTokenProvider?.let { tokenProvider ->
            install(Auth) {
                bearer {
                    loadTokens {
                        tokenProvider()?.let { token ->
                            BearerTokens(accessToken = token, refreshToken = "")
                        }
                    }
                    refreshTokens {
                        tokenProvider()?.let { token ->
                            BearerTokens(accessToken = token, refreshToken = "")
                        }
                    }
                    sendWithoutRequest { request ->
                        request.url.host.contains("petwise")
                    }
                }
            }
        }

        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = config.maxRetries)
            retryOnException(maxRetries = config.maxRetries, retryOnTimeout = true)
            exponentialDelay(
                base = 2.0,
                maxDelayMs = NetworkConfig.MAX_RETRY_DELAY
            )
            modifyRequest { request ->
                request.headers.append(PetWiseHttpHeaders.REQUEST_ID, generateRequestId())
            }
        }
    }
}

expect fun getUserAgent(): String

expect fun getPlatformName(): String

expect fun generateRequestId(): String
