package edu.fatec.petwise.core.network.di

import edu.fatec.petwise.core.network.*
import edu.fatec.petwise.core.network.api.*
import io.ktor.client.*

object NetworkModule {

    private val tokenManager: TokenManager = TokenManagerImpl()

    private fun createHttpClientConfig(): PetWiseHttpClientConfig {
        return PetWiseHttpClientConfig(
            enableLogging = NetworkConfig.enableLogging,
            requestTimeout = NetworkConfig.REQUEST_TIMEOUT,
            connectTimeout = NetworkConfig.CONNECT_TIMEOUT,
            socketTimeout = NetworkConfig.SOCKET_TIMEOUT,
            maxRetries = NetworkConfig.MAX_RETRY_ATTEMPTS,
            authTokenProvider = { tokenManager.getAccessToken() }
        )
    }

    private var _httpClient: HttpClient? = null
    
    private fun getHttpClient(): HttpClient {
        if (_httpClient == null) {
            _httpClient = createDefaultHttpClient(createHttpClientConfig())
        }
        return _httpClient!!
    }

    private var _networkRequestHandler: NetworkRequestHandler? = null
    
    private fun getNetworkRequestHandler(): NetworkRequestHandler {
        if (_networkRequestHandler == null) {
            _networkRequestHandler = NetworkRequestHandler(getHttpClient())
        }
        return _networkRequestHandler!!
    }

    val authApiService: AuthApiService
        get() = AuthApiServiceImpl(getNetworkRequestHandler())

    val petApiService: PetApiService
        get() = PetApiServiceImpl(getNetworkRequestHandler())

    val consultaApiService: ConsultaApiService
        get() = ConsultaApiServiceImpl(getNetworkRequestHandler())

    val vaccinationApiService: VaccinationApiService
        get() = VaccinationApiServiceImpl(getNetworkRequestHandler())

    fun clear() {
        println("NetworkModule: Limpando recursos de rede")
        _httpClient?.close()
        _httpClient = null
        _networkRequestHandler = null
        tokenManager.clearTokens()
        println("NetworkModule: Recursos limpos")
    }

    fun setAuthToken(token: String) {
        println("NetworkModule: Definindo token de autenticação: ${token.take(10)}...")
        tokenManager.setAccessToken(token)
        println("NetworkModule: Token atualizado, cliente HTTP reutilizará automaticamente")
    }
    
    fun setAuthTokenWithExpiration(token: String, expiresInSeconds: Long) {
        println("NetworkModule: Definindo token de autenticação com expiração: ${token.take(10)}...")
        if (tokenManager is TokenManagerImpl) {
            tokenManager.setTokenWithExpiration(token, expiresInSeconds)
        } else {
            tokenManager.setAccessToken(token)
        }
        println("NetworkModule: Token atualizado, cliente HTTP reutilizará automaticamente")
    }

    fun getAuthToken(): String? {
        val token = tokenManager.getAccessToken()
        println("NetworkModule: Obtendo token de autenticação: ${token?.take(10)}...")
        return token
    }
    
    fun isTokenValid(): Boolean {
        return tokenManager.getAccessToken() != null
    }
    
    fun clearAuthToken() {
        println("NetworkModule: Limpando token de autenticação")
        tokenManager.clearTokens()
        println("NetworkModule: Token limpo, requisições subsequentes não terão autenticação")
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
    private var tokenExpirationTime: Long = 0
    private var tokenSetTime: Long = 0

    override fun getAccessToken(): String? {
        val token = accessToken
        if (token != null && isTokenExpired()) {
            println("TokenManager: Token expirado, limpando tokens")
            clearTokens()
            return null
        }
        
        println("TokenManager: Retornando token de acesso: ${token?.take(10)}... (expira em ${getRemainingTokenTime()}ms)")
        return token
    }

    override fun setAccessToken(token: String) {
        println("TokenManager: Definindo token de acesso: ${token.take(10)}...")
        accessToken = token
        tokenSetTime = System.currentTimeMillis()
        tokenExpirationTime = tokenSetTime + (60 * 60 * 1000) 
    }

    override fun getRefreshToken(): String? = refreshToken

    override fun setRefreshToken(token: String) {
        println("TokenManager: Definindo token de refresh: ${token.take(10)}...")
        refreshToken = token
    }

    override fun clearTokens() {
        println("TokenManager: Limpando todos os tokens")
        accessToken = null
        refreshToken = null
        tokenExpirationTime = 0
        tokenSetTime = 0
    }
    
    private fun isTokenExpired(): Boolean {
        return if (tokenExpirationTime > 0) {
            System.currentTimeMillis() >= tokenExpirationTime
        } else {
            false
        }
    }
    
    private fun getRemainingTokenTime(): Long {
        return if (tokenExpirationTime > 0) {
            (tokenExpirationTime - System.currentTimeMillis()).coerceAtLeast(0)
        } else {
            -1
        }
    }
    
    fun setTokenWithExpiration(token: String, expiresInSeconds: Long) {
        println("TokenManager: Definindo token de acesso com expiração de ${expiresInSeconds}s: ${token.take(10)}...")
        accessToken = token
        tokenSetTime = System.currentTimeMillis()
        tokenExpirationTime = tokenSetTime + (expiresInSeconds * 1000)
    }
}
