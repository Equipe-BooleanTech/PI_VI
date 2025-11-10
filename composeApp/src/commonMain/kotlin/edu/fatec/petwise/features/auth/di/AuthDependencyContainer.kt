package edu.fatec.petwise.features.auth.di

import edu.fatec.petwise.core.network.di.NetworkModule
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
        AuthTokenStorageImpl()
    }

    private val remoteDataSource: RemoteAuthDataSource by lazy {
        RemoteAuthDataSourceImpl(NetworkModule.authApiService)
    }

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            remoteDataSource = remoteDataSource,
            tokenStorage = _tokenStorage
        )
    }

    private val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(authRepository)
    }

    private val registerUseCase: RegisterUseCase by lazy {
        RegisterUseCase(authRepository)
    }

    private val requestPasswordResetUseCase: RequestPasswordResetUseCase by lazy {
        RequestPasswordResetUseCase(authRepository)
    }

    private val resetPasswordUseCase: ResetPasswordUseCase by lazy {
        ResetPasswordUseCase(authRepository)
    }

    private val logoutUseCase: LogoutUseCase by lazy {
        LogoutUseCase(authRepository)
    }

    private val getUserProfileUseCase: GetUserProfileUseCase by lazy {
        GetUserProfileUseCase(authRepository)
    }
    
    private val _authViewModel: AuthViewModel by lazy {
        AuthViewModel(
            loginUseCase = loginUseCase,
            registerUseCase = registerUseCase,
            logoutUseCase = logoutUseCase
        )
    }

    fun provideLoginUseCase(): LoginUseCase = loginUseCase

    fun provideRegisterUseCase(): RegisterUseCase = registerUseCase

    fun provideRequestPasswordResetUseCase(): RequestPasswordResetUseCase = requestPasswordResetUseCase

    fun provideResetPasswordUseCase(): ResetPasswordUseCase = resetPasswordUseCase

    fun provideLogoutUseCase(): LogoutUseCase = logoutUseCase

    fun provideGetUserProfileUseCase(): GetUserProfileUseCase = getUserProfileUseCase

    fun provideAuthViewModel(): AuthViewModel = _authViewModel

    fun provideForgotPasswordViewModel(): ForgotPasswordViewModel {
        return ForgotPasswordViewModel(requestPasswordResetUseCase = requestPasswordResetUseCase)
    }

    fun provideResetPasswordViewModel(): ResetPasswordViewModel {
        return ResetPasswordViewModel(resetPasswordUseCase = resetPasswordUseCase)
    }

    fun getTokenStorage(): AuthTokenStorage = _tokenStorage

    fun syncTokensWithNetworkModule() {
        _tokenStorage.getToken()?.let { token ->
            NetworkModule.setAuthToken(token)
        }
    }
}

class AuthTokenStorageImpl : AuthTokenStorage {
    private var token: String? = null
    private var userId: String? = null
    private var tokenExpirationTime: Long = 0

    override fun saveToken(token: String) {
        println("AuthTokenStorage: Saving token: ${token.take(10)}...")
        this.token = token
        this.tokenExpirationTime = currentTimeMs() + (60 * 60 * 1000)
    }

    override fun getToken(): String? {
        val currentToken = token
        if (currentToken != null && isTokenExpired()) {
            println("AuthTokenStorage: Token expired, clearing tokens")
            clearTokens()
            return null
        }
        
        println("AuthTokenStorage: Returning token: ${currentToken?.take(10)}... (expires in ${getRemainingTime()}ms)")
        return currentToken
    }

    override fun saveUserId(userId: String) {
        println("AuthTokenStorage: Saving userId: $userId")
        this.userId = userId
    }

    override fun getUserId(): String? = userId

    override fun clearTokens() {
        println("AuthTokenStorage: Clearing all tokens and user data")
        token = null
        userId = null
        tokenExpirationTime = 0
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
        println("AuthTokenStorage: Saving token with ${expiresInSeconds}s expiration: ${token.take(10)}...")
        this.token = token
        this.tokenExpirationTime = currentTimeMs() + (expiresInSeconds * 1000)
    }
}

private fun currentTimeMs(): Long = Clock.System.now().toEpochMilliseconds()
