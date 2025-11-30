package edu.fatec.petwise.core.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
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
                level = LogLevel.ALL
                filter { request ->
                    true
                }
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
            println("HttpClient: Instalando autenticação com token provider")
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = tokenProvider()
                        println("HttpClient: loadTokens chamado, token: ${token?.take(10)}...")
                        token?.let { 
                            BearerTokens(accessToken = it, refreshToken = "")
                        }
                    }
                    refreshTokens {
                        val token = tokenProvider()
                        println("HttpClient: refreshTokens chamado, token: ${token?.take(10)}...")
                        token?.let { 
                            BearerTokens(accessToken = it, refreshToken = "")
                        }
                    }
                    sendWithoutRequest { request ->
                        val isAuthEndpoint = request.url.encodedPath.contains("/auth/login") ||
                                           request.url.encodedPath.contains("/auth/register")
                        
                        val shouldSend = !isAuthEndpoint && (
                            request.url.host.contains("petwise") || 
                            request.url.host.contains("localhost") ||
                            request.url.host.contains("127.0.0.1") ||
                            request.url.host.contains("10.0.2.2") // Android emulator host
                        )
                        
                        println("HttpClient: sendWithoutRequest para ${request.url} (isAuth: $isAuthEndpoint): $shouldSend")
                        shouldSend
                    }
                }
            }
        }
    }
}

expect fun getUserAgent(): String

expect fun getPlatformName(): String

expect fun generateRequestId(): String
