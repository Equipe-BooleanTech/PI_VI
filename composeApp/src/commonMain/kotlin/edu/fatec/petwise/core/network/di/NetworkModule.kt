package edu.fatec.petwise.core.network.di

import edu.fatec.petwise.core.network.*
import edu.fatec.petwise.core.network.api.*
import io.ktor.client.*

object NetworkModule {

    private var httpClientConfig: PetWiseHttpClientConfig = PetWiseHttpClientConfig(
        enableLogging = NetworkConfig.enableLogging,
        requestTimeout = NetworkConfig.REQUEST_TIMEOUT,
        connectTimeout = NetworkConfig.CONNECT_TIMEOUT,
        socketTimeout = NetworkConfig.SOCKET_TIMEOUT,
        maxRetries = NetworkConfig.MAX_RETRY_ATTEMPTS,
        authTokenProvider = { tokenManager.getAccessToken() }
    )

    private val tokenManager: TokenManager by lazy {
        TokenManagerImpl()
    }

    private val httpClient: HttpClient by lazy {
        createDefaultHttpClient(httpClientConfig)
    }

    private val networkRequestHandler: NetworkRequestHandler by lazy {
        NetworkRequestHandler(httpClient)
    }

    val authApiService: AuthApiService by lazy {
        AuthApiServiceImpl(networkRequestHandler)
    }

    val petApiService: PetApiService by lazy {
        PetApiServiceImpl(networkRequestHandler)
    }

    val consultaApiService: ConsultaApiService by lazy {
        ConsultaApiServiceImpl(networkRequestHandler)
    }

    val vaccinationApiService: VaccinationApiService by lazy {
        VaccinationApiServiceImpl(networkRequestHandler)
    }

    fun updateConfig(newConfig: PetWiseHttpClientConfig) {
        httpClientConfig = newConfig
    }

    fun clear() {
        httpClient.close()
        tokenManager.clearTokens()
    }

    fun setAuthToken(token: String) {
        tokenManager.setAccessToken(token)
    }

    fun getAuthToken(): String? {
        return tokenManager.getAccessToken()
    }
}

interface TokenManager {
    fun getAccessToken(): String?
    fun setAccessToken(token: String)
    fun getRefreshToken(): String?
    fun setRefreshToken(token: String)
    fun clearTokens()
}

class TokenManagerImpl : TokenManager {
    private var accessToken: String? = null
    private var refreshToken: String? = null

    override fun getAccessToken(): String? = accessToken

    override fun setAccessToken(token: String) {
        accessToken = token
    }

    override fun getRefreshToken(): String? = refreshToken

    override fun setRefreshToken(token: String) {
        refreshToken = token
    }

    override fun clearTokens() {
        accessToken = null
        refreshToken = null
    }
}
