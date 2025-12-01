package edu.fatec.petwise.features.auth.di

import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.core.storage.KeyValueStorage
import kotlinx.datetime.Clock
import edu.fatec.petwise.features.auth.data.datasource.RemoteAuthDataSource
import edu.fatec.petwise.features.auth.data.datasource.RemoteAuthDataSourceImpl
import edu.fatec.petwise.features.auth.data.repository.AuthRepositoryImpl
import edu.fatec.petwise.features.auth.data.repository.AuthTokenStorage
import edu.fatec.petwise.features.auth.domain.repository.AuthRepository
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase
import edu.fatec.petwise.features.auth.domain.usecases.LoginUseCase
import edu.fatec.petwise.features.auth.domain.usecases.LogoutUseCase
import edu.fatec.petwise.features.auth.domain.usecases.RegisterUseCase
import edu.fatec.petwise.features.auth.domain.usecases.RequestPasswordResetUseCase
import edu.fatec.petwise.features.auth.domain.usecases.ResetPasswordUseCase
import edu.fatec.petwise.features.auth.presentation.viewmodel.AuthViewModel
import edu.fatec.petwise.features.auth.presentation.viewmodel.ForgotPasswordViewModel
import edu.fatec.petwise.features.auth.presentation.viewmodel.ResetPasswordViewModel

object AuthDependencyContainer {

    private val _tokenStorage: AuthTokenStorage by lazy {
        AuthTokenStorageImpl(KeyValueStorage)
    }

    private val remoteDataSource: RemoteAuthDataSource by lazy {
        RemoteAuthDataSourceImpl(NetworkModule.authApiService)
    }

    private var authRepository: AuthRepository? = null

    private fun getAuthRepository(): AuthRepository {
        val existing = authRepository
        if (existing != null) return existing
        val created = AuthRepositoryImpl(
            remoteDataSource = remoteDataSource,
            tokenStorage = _tokenStorage
        )
        authRepository = created
        return created
    }

    private var loginUseCase: LoginUseCase? = null
    private var registerUseCase: RegisterUseCase? = null
    private var requestPasswordResetUseCase: RequestPasswordResetUseCase? = null
    private var resetPasswordUseCase: ResetPasswordUseCase? = null
    private var logoutUseCase: LogoutUseCase? = null
    private var getUserProfileUseCase: GetUserProfileUseCase? = null
    
    private fun getLoginUseCase(): LoginUseCase {
        val existing = loginUseCase
        if (existing != null) return existing
        val created = LoginUseCase(getAuthRepository())
        loginUseCase = created
        return created
    }

    private fun getRegisterUseCase(): RegisterUseCase {
        val existing = registerUseCase
        if (existing != null) return existing
        val created = RegisterUseCase(getAuthRepository())
        registerUseCase = created
        return created
    }

    private fun getRequestPasswordResetUseCase(): RequestPasswordResetUseCase {
        val existing = requestPasswordResetUseCase
        if (existing != null) return existing
        val created = RequestPasswordResetUseCase(getAuthRepository())
        requestPasswordResetUseCase = created
        return created
    }

    private fun getResetPasswordUseCase(): ResetPasswordUseCase {
        val existing = resetPasswordUseCase
        if (existing != null) return existing
        val created = ResetPasswordUseCase(getAuthRepository())
        resetPasswordUseCase = created
        return created
    }

    private fun getLogoutUseCase(): LogoutUseCase {
        val existing = logoutUseCase
        if (existing != null) return existing
        val created = LogoutUseCase(getAuthRepository())
        logoutUseCase = created
        return created
    }

    private fun getGetUserProfileUseCase(): GetUserProfileUseCase {
        val existing = getUserProfileUseCase
        if (existing != null) return existing
        val created = GetUserProfileUseCase(getAuthRepository())
        getUserProfileUseCase = created
        return created
    }
    
    private val _authViewModel: AuthViewModel by lazy {
        AuthViewModel(
            loginUseCase = getLoginUseCase(),
            registerUseCase = getRegisterUseCase(),
            logoutUseCase = getLogoutUseCase()
        )
    }

    fun provideLoginUseCase(): LoginUseCase = getLoginUseCase()

    fun provideRegisterUseCase(): RegisterUseCase = getRegisterUseCase()

    fun provideRequestPasswordResetUseCase(): RequestPasswordResetUseCase = getRequestPasswordResetUseCase()

    fun provideResetPasswordUseCase(): ResetPasswordUseCase = getResetPasswordUseCase()

    fun provideLogoutUseCase(): LogoutUseCase = getLogoutUseCase()

    fun provideGetUserProfileUseCase(): GetUserProfileUseCase = getGetUserProfileUseCase()

    fun provideAuthViewModel(): AuthViewModel = _authViewModel

    fun provideForgotPasswordViewModel(): ForgotPasswordViewModel {
        return ForgotPasswordViewModel(requestPasswordResetUseCase = getRequestPasswordResetUseCase())
    }

    fun provideResetPasswordViewModel(): ResetPasswordViewModel {
        return ResetPasswordViewModel(resetPasswordUseCase = getResetPasswordUseCase())
    }

    fun getTokenStorage(): AuthTokenStorage = _tokenStorage

    fun syncTokensWithNetworkModule() {
        _tokenStorage.getToken()?.let { token ->
            NetworkModule.setAuthToken(token)
        }
    }

    fun reset() {
        println("AuthDependencyContainer: Resetando container de autenticação")
        authRepository = null
        loginUseCase = null
        registerUseCase = null
        requestPasswordResetUseCase = null
        resetPasswordUseCase = null
        logoutUseCase = null
        getUserProfileUseCase = null
        
    }
}

class AuthTokenStorageImpl(private val storage: KeyValueStorage) : AuthTokenStorage {
    
    
    private var token: String? = null
    private var tokenExpirationTime: Long = 0

    init {
        
        
        println("AuthTokenStorage: Inicializado - modo memory-only (sem persistência)")
    }

    override fun saveToken(token: String) {
        println("AuthTokenStorage: Saving token (memory-only): ${token.take(10)}...")
        this.token = token
        this.tokenExpirationTime = currentTimeMs() + (60 * 60 * 1000)
        
    }

    override fun getToken(): String? {
        val currentToken = token
        
        if (currentToken.isNullOrBlank()) {
            return null
        }
        if (isTokenExpired()) {
            println("AuthTokenStorage: Token expired, clearing tokens")
            clearTokens()
            return null
        }
        
        println("AuthTokenStorage: Returning token: ${currentToken.take(10)}... (expires in ${getRemainingTime()}ms)")
        return currentToken
    }

    override fun clearTokens() {
        println("AuthTokenStorage: Clearing all tokens (memory)")
        token = null
        tokenExpirationTime = 0
        
        storage.remove("access_token")
        storage.remove("token_expiration")
        storage.remove("token_set_time")
    }
    
    private fun isTokenExpired(): Boolean {
        return tokenExpirationTime > 0 && currentTimeMs() >= tokenExpirationTime
    }
    
    private fun getRemainingTime(): Long {
        return if (tokenExpirationTime > 0) {
            (tokenExpirationTime - currentTimeMs()).coerceAtLeast(0)
        } else {
            -1
        }
    }
    
    fun saveTokenWithExpiration(token: String, expiresInSeconds: Long) {
        println("AuthTokenStorage: Saving token with ${expiresInSeconds}s expiration (memory-only): ${token.take(10)}...")
        this.token = token
        this.tokenExpirationTime = currentTimeMs() + (expiresInSeconds * 1000)
        
    }
}

private fun currentTimeMs(): Long = Clock.System.now().toEpochMilliseconds()
