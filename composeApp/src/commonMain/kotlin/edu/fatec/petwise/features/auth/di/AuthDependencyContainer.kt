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

class AuthTokenStorageImpl(private val storage: KeyValueStorage) : AuthTokenStorage {
    private var token: String? = null
    private var userId: String? = null
    private var userType: String? = null
    private var fullName: String? = null
    private var tokenExpirationTime: Long = 0

    init {
        // Load from storage
        token = storage.getString("access_token")
        userId = storage.getString("user_id")
        userType = storage.getString("user_type")
        fullName = storage.getString("full_name")
        tokenExpirationTime = storage.getLong("token_expiration") ?: 0
    }

    override fun saveToken(token: String) {
        println("AuthTokenStorage: Saving token: ${token.take(10)}...")
        this.token = token
        this.tokenExpirationTime = currentTimeMs() + (60 * 60 * 1000)
        storage.putString("access_token", token)
        storage.putLong("token_expiration", tokenExpirationTime)
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
        storage.putString("user_id", userId)
    }

    override fun getUserId(): String? = userId

    override fun saveUserType(userType: String) {
        println("AuthTokenStorage: Saving userType: $userType")
        this.userType = userType
        storage.putString("user_type", userType)
    }

    override fun getUserType(): String? {
        println("AuthTokenStorage: Returning userType: $userType")
        return userType
    }

    override fun saveFullName(fullName: String) {
        println("AuthTokenStorage: Saving fullName: $fullName")
        this.fullName = fullName
        storage.putString("full_name", fullName)
    }

    override fun getFullName(): String? {
        println("AuthTokenStorage: Returning fullName: $fullName")
        return fullName
    }

    override fun clearTokens() {
        println("AuthTokenStorage: Clearing all tokens and user data")
        token = null
        userId = null
        userType = null
        fullName = null
        tokenExpirationTime = 0
        storage.remove("access_token")
        storage.remove("user_id")
        storage.remove("user_type")
        storage.remove("full_name")
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
        println("AuthTokenStorage: Saving token with ${expiresInSeconds}s expiration: ${token.take(10)}...")
        this.token = token
        this.tokenExpirationTime = currentTimeMs() + (expiresInSeconds * 1000)
        storage.putString("access_token", token)
        storage.putLong("token_expiration", tokenExpirationTime)
        storage.putLong("token_set_time", currentTimeMs())
    }
}

private fun currentTimeMs(): Long = Clock.System.now().toEpochMilliseconds()
