package edu.fatec.petwise.core.network.di

import edu.fatec.petwise.core.network.*
import edu.fatec.petwise.core.network.api.*
import io.ktor.client.*
import kotlinx.datetime.Clock


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
    private var _isClientClosed = false
    
    private fun getHttpClient(): HttpClient {
        val currentClient = _httpClient
        if (currentClient == null || _isClientClosed) {
            println("NetworkModule: Criando novo HttpClient (anterior fechado: $_isClientClosed, nulo: ${currentClient == null})")
            
            _isClientClosed = false
            
            val newClient = createDefaultHttpClient(createHttpClientConfig())
            _httpClient = newClient
            
            _networkRequestHandler = null
            
            println("NetworkModule: Novo HttpClient criado com sucesso")
            return newClient
        }
        return currentClient
    }

    private var _networkRequestHandler: NetworkRequestHandler? = null
    
    private fun getNetworkRequestHandler(): NetworkRequestHandler {
        val currentHandler = _networkRequestHandler
        if (currentHandler == null) {
            println("NetworkModule: Criando novo NetworkRequestHandler")
            val newHandler = NetworkRequestHandler(getHttpClient())
            _networkRequestHandler = newHandler
            return newHandler
        }
        return currentHandler
    }

    fun getDedicatedNetworkRequestHandler(): NetworkRequestHandler {
        println("NetworkModule: Criando NetworkRequestHandler e HttpClient dedicados.")
        val dedicatedConfig = createHttpClientConfig()
        val dedicatedClient = createDefaultHttpClient(dedicatedConfig)
        return NetworkRequestHandler(dedicatedClient)
    }

    val authApiService: AuthApiService
        get() = AuthApiServiceImpl(getNetworkRequestHandler())

    val petApiService: PetApiService
        get() = PetApiServiceImpl(getNetworkRequestHandler())

    val consultaApiService: ConsultaApiService
        get() = ConsultaApiServiceImpl(getNetworkRequestHandler())

    val vaccinationApiService: VaccinationApiService
        get() = VaccinationApiServiceImpl(getNetworkRequestHandler())

    val medicationApiService: MedicationApiService
        get() = MedicationApiServiceImpl(getNetworkRequestHandler())

    val profileApiService: ProfileApiService
        get() = ProfileApiServiceImpl(getNetworkRequestHandler())

    val suprimentoApiService: SuprimentoApiService
        get() = SuprimentoApiServiceImpl(getNetworkRequestHandler())

    val examApiService: ExamApiService
        get() = ExamApiServiceImpl(getNetworkRequestHandler())

    val prescriptionApiService: PrescriptionApiService
        get() = PrescriptionApiServiceImpl(getNetworkRequestHandler())

    val labApiService: LabApiService
        get() = LabApiServiceImpl(getNetworkRequestHandler())

    val foodApiService: FoodApiService
        get() = FoodApiServiceImpl(getNetworkRequestHandler())

    val hygieneApiService: HygieneApiService
        get() = HygieneApiServiceImpl(getNetworkRequestHandler())

    val toyApiService: ToyApiService
        get() = ToyApiServiceImpl(getNetworkRequestHandler())

    fun clear() {
        println("NetworkModule: Limpando recursos de rede")
        println("NetworkModule: Mantendo HttpClient ativo para evitar erros de coroutine")
        tokenManager.clearTokens()
        _networkRequestHandler = null
        println("NetworkModule: Tokens e NetworkRequestHandler limpos - HttpClient permanece ativo e reutilizável")
    }

    fun setAuthToken(token: String) {
        println("NetworkModule: Definindo token de autenticação: ${token.take(10)}...")
        tokenManager.setAccessToken(token)
        _networkRequestHandler = null
        println("NetworkModule: Token atualizado, NetworkRequestHandler limpo para recriar com novo token")
    }
    
    fun setAuthTokenWithExpiration(token: String, expiresInSeconds: Long) {
        println("NetworkModule: Definindo token de autenticação com expiração: ${token.take(10)}...")
        if (tokenManager is TokenManagerImpl) {
            tokenManager.setTokenWithExpiration(token, expiresInSeconds)
        } else {
            tokenManager.setAccessToken(token)
        }
        _networkRequestHandler = null
        println("NetworkModule: Token atualizado, NetworkRequestHandler limpo para recriar com novo token")
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
                
        println("NetworkModule: Token limpo - requisições subsequentes não terão autenticação")
        println("NetworkModule: HttpClient mantido ativo para prevenir JobCancellationException")
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
        tokenSetTime = currentTimeMs()
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
            currentTimeMs() >= tokenExpirationTime
        } else {
            false
        }
    }
    
    private fun getRemainingTokenTime(): Long {
        return if (tokenExpirationTime > 0) {
            (tokenExpirationTime - currentTimeMs()).coerceAtLeast(0)
        } else {
            -1
        }
    }
    
    fun setTokenWithExpiration(token: String, expiresInSeconds: Long) {
        println("TokenManager: Definindo token de acesso com expiração de ${expiresInSeconds}s: ${token.take(10)}...")
        accessToken = token
        tokenSetTime = currentTimeMs()
        tokenExpirationTime = tokenSetTime + (expiresInSeconds * 1000)
    }
}

private fun currentTimeMs(): Long = Clock.System.now().toEpochMilliseconds()
