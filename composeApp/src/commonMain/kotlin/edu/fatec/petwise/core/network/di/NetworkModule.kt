package edu.fatec.petwise.core.network.di

import edu.fatec.petwise.core.network.*
import edu.fatec.petwise.core.network.api.*
import io.ktor.client.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

object NetworkModule {

    private val tokenManager: TokenManager = TokenManagerImpl()
    
    private var networkSupervisorJob = SupervisorJob()
    
    private var networkScope = CoroutineScope(
        networkSupervisorJob + Dispatchers.Default + CoroutineName("NetworkModule")
    )

    private fun createHttpClientConfig(): PetWiseHttpClientConfig {
        return PetWiseHttpClientConfig(
            enableLogging = NetworkConfig.enableLogging,
            requestTimeout = NetworkConfig.REQUEST_TIMEOUT,
            connectTimeout = NetworkConfig.CONNECT_TIMEOUT,
            socketTimeout = NetworkConfig.SOCKET_TIMEOUT,
            maxRetries = NetworkConfig.MAX_RETRY_ATTEMPTS,
            authTokenProvider = { tokenManager.getAccessToken() },
            coroutineScope = networkScope
        )
    }

    private var _httpClient: HttpClient? = null
    
    private fun getHttpClient(): HttpClient {
        if (_httpClient == null) {
            _httpClient = createDefaultHttpClient(createHttpClientConfig())
        }
        return _httpClient!!
    }

    private fun recreateHttpClient() {
        println("NetworkModule: Fechando cliente HTTP existente")
        _httpClient?.close()
        _httpClient = null
        _networkRequestHandler = null
        
        println("NetworkModule: Criando novo cliente HTTP com token atualizado")
        _httpClient = createDefaultHttpClient(createHttpClientConfig())
        _networkRequestHandler = NetworkRequestHandler(getHttpClient())
        println("NetworkModule: Novo cliente HTTP criado")
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
        println("NetworkModule: Cancelando todas as operações de rede em andamento")
        networkSupervisorJob.cancel()
        networkSupervisorJob = SupervisorJob()
        networkScope = CoroutineScope(
            networkSupervisorJob + Dispatchers.Default + CoroutineName("NetworkModule")
        )
        
        _httpClient?.close()
        _httpClient = null
        _networkRequestHandler = null
        tokenManager.clearTokens()
        
        println("NetworkModule: Todas as operações canceladas e recursos limpos")
    }

    fun setAuthToken(token: String) {
        println("NetworkModule: Setting auth token: ${token.take(10)}...")
        tokenManager.setAccessToken(token)
        recreateHttpClient()
    }
    
    fun setAuthTokenWithExpiration(token: String, expiresInSeconds: Long) {
        println("NetworkModule: Setting auth token with expiration: ${token.take(10)}...")
        if (tokenManager is TokenManagerImpl) {
            tokenManager.setTokenWithExpiration(token, expiresInSeconds)
        } else {
            tokenManager.setAccessToken(token)
        }
        recreateHttpClient()
    }

    fun getAuthToken(): String? {
        val token = tokenManager.getAccessToken()
        println("NetworkModule: Getting auth token: ${token?.take(10)}...")
        return token
    }
    
    fun isTokenValid(): Boolean {
        return tokenManager.getAccessToken() != null
    }
    
    fun cancelAllOperations() {
        println("NetworkModule: Forçando cancelamento de todas as operações de rede")
        networkSupervisorJob.cancel("Logout do usuário - cancelando todas as operações")
        networkSupervisorJob = SupervisorJob()
        networkScope = CoroutineScope(
            networkSupervisorJob + Dispatchers.Default + CoroutineName("NetworkModule")
        )
        println("NetworkModule: Scope de rede recriado")
    }
    
    fun clearAuthToken() {
        println("NetworkModule: Clearing auth token")
        tokenManager.clearTokens()
        
        println("NetworkModule: Cancelando operações em andamento antes de recriar cliente")
        networkSupervisorJob.cancel()
        networkSupervisorJob = SupervisorJob()
        networkScope = CoroutineScope(
            networkSupervisorJob + Dispatchers.Default + CoroutineName("NetworkModule")
        )
        
        recreateHttpClient()
        println("NetworkModule: Cliente HTTP recriado com novo scope")
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
            println("TokenManager: Token expired, clearing tokens")
            clearTokens()
            return null
        }
        
        println("TokenManager: Returning access token: ${token?.take(10)}... (expires in ${getRemainingTokenTime()}ms)")
        return token
    }

    override fun setAccessToken(token: String) {
        println("TokenManager: Setting access token: ${token.take(10)}...")
        accessToken = token
        tokenSetTime = System.currentTimeMillis()
        // Default to 1 hour expiration if not specified
        tokenExpirationTime = tokenSetTime + (60 * 60 * 1000) 
    }

    override fun getRefreshToken(): String? = refreshToken

    override fun setRefreshToken(token: String) {
        println("TokenManager: Setting refresh token: ${token.take(10)}...")
        refreshToken = token
    }

    override fun clearTokens() {
        println("TokenManager: Clearing all tokens")
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
        println("TokenManager: Setting access token with ${expiresInSeconds}s expiration: ${token.take(10)}...")
        accessToken = token
        tokenSetTime = System.currentTimeMillis()
        tokenExpirationTime = tokenSetTime + (expiresInSeconds * 1000)
    }
}
