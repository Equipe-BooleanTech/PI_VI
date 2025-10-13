package edu.fatec.petwise.features.auth.di

import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.auth.data.datasource.RemoteAuthDataSource
import edu.fatec.petwise.features.auth.data.datasource.RemoteAuthDataSourceImpl
import edu.fatec.petwise.features.auth.data.repository.AuthRepositoryImpl
import edu.fatec.petwise.features.auth.data.repository.AuthTokenStorage
import edu.fatec.petwise.features.auth.domain.repository.AuthRepository
import edu.fatec.petwise.features.auth.domain.usecases.LoginUseCase
import edu.fatec.petwise.features.auth.domain.usecases.LogoutUseCase
import edu.fatec.petwise.features.auth.domain.usecases.RegisterUseCase
import edu.fatec.petwise.features.auth.domain.usecases.RequestPasswordResetUseCase
import edu.fatec.petwise.features.auth.domain.usecases.ResetPasswordUseCase
import edu.fatec.petwise.features.auth.presentation.viewmodel.AuthViewModel
import edu.fatec.petwise.features.auth.presentation.viewmodel.ForgotPasswordViewModel
import edu.fatec.petwise.features.auth.presentation.viewmodel.ResetPasswordViewModel

object AuthDependencyContainer {

    var useApi: Boolean = true // Desabilitar para usar dados de MOCK

    private val _tokenStorage: AuthTokenStorage by lazy {
        AuthTokenStorageImpl()
    }

    private val remoteDataSource: RemoteAuthDataSource? by lazy {
        if (useApi) RemoteAuthDataSourceImpl(NetworkModule.authApiService) else null
    }

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            remoteDataSource = remoteDataSource,
            tokenStorage = if (useApi) _tokenStorage else null
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

    fun provideLoginUseCase(): LoginUseCase = loginUseCase

    fun provideRegisterUseCase(): RegisterUseCase = registerUseCase

    fun provideRequestPasswordResetUseCase(): RequestPasswordResetUseCase = requestPasswordResetUseCase

    fun provideResetPasswordUseCase(): ResetPasswordUseCase = resetPasswordUseCase

    fun provideLogoutUseCase(): LogoutUseCase = logoutUseCase

    fun provideAuthViewModel(): AuthViewModel {
        return AuthViewModel(
            loginUseCase = loginUseCase,
            registerUseCase = registerUseCase,
            logoutUseCase = logoutUseCase
        )
    }

    fun provideForgotPasswordViewModel(): ForgotPasswordViewModel {
        return ForgotPasswordViewModel(requestPasswordResetUseCase = requestPasswordResetUseCase)
    }

    fun provideResetPasswordViewModel(): ResetPasswordViewModel {
        return ResetPasswordViewModel(resetPasswordUseCase = resetPasswordUseCase)
    }

    fun getTokenStorage(): AuthTokenStorage = _tokenStorage

    fun syncTokensWithNetworkModule() {
        if (useApi) {
            _tokenStorage.getToken()?.let { token ->
                NetworkModule.setAuthToken(token)
            }
        }
    }
}

class AuthTokenStorageImpl : AuthTokenStorage {
    private var token: String? = null
    private var userId: String? = null

    override fun saveToken(token: String) {
        this.token = token
    }

    override fun getToken(): String? = token

    override fun saveUserId(userId: String) {
        this.userId = userId
    }

    override fun getUserId(): String? = userId

    override fun clearTokens() {
        token = null
        userId = null
    }
}
