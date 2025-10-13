package edu.fatec.petwise.features.auth.data.repository

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.features.auth.data.datasource.RemoteAuthDataSource
import edu.fatec.petwise.features.auth.domain.repository.AuthRepository
import edu.fatec.petwise.presentation.shared.form.currentTimeMs
import kotlinx.coroutines.delay

class AuthRepositoryImpl(
    private val remoteDataSource: RemoteAuthDataSource? = null,
    private val tokenStorage: AuthTokenStorage? = null
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<String> {
        return if (remoteDataSource != null) {
            when (val result = remoteDataSource.login(email, password)) {
                is NetworkResult.Success -> {
                    tokenStorage?.saveToken(result.data.token)
                    tokenStorage?.saveUserId(result.data.userId)
                    Result.success(result.data.userId)
                }
                is NetworkResult.Error -> {
                    Result.failure(Exception(result.exception.message ?: "Erro ao fazer login"))
                }
                is NetworkResult.Loading -> {
                    Result.failure(Exception("Carregando..."))
                }
            }
        } else {
            loginLocal(email, password)
        }
    }

    private suspend fun loginLocal(email: String, password: String): Result<String> {
        delay(1000)

        return if (email.contains("@") && password.length >= 6) {
            Result.success("user_id_123")
        } else {
            Result.failure(Exception("Credenciais inválidas"))
        }
    }

    override suspend fun register(userData: Map<String, String>): Result<String> {
        return if (remoteDataSource != null) {
            when (val result = remoteDataSource.register(userData)) {
                is NetworkResult.Success -> {
                    tokenStorage?.saveToken(result.data.token)
                    tokenStorage?.saveUserId(result.data.userId)
                    Result.success(result.data.userId)
                }
                is NetworkResult.Error -> {
                    Result.failure(Exception(result.exception.message ?: "Erro ao registrar"))
                }
                is NetworkResult.Loading -> {
                    Result.failure(Exception("Carregando..."))
                }
            }
        } else {
            registerLocal(userData)
        }
    }

    private suspend fun registerLocal(userData: Map<String, String>): Result<String> {
        delay(1500)

        val email = userData["email"] ?: return Result.failure(Exception("Email obrigatório"))
        val password = userData["password"] ?: return Result.failure(Exception("Senha obrigatória"))

        if (!email.contains("@")) {
            return Result.failure(Exception("Email inválido"))
        }

        if (password.length < 8) {
            return Result.failure(Exception("Senha muito curta"))
        }

        return Result.success("user_id_${currentTimeMs()}")
    }

    override suspend fun requestPasswordReset(email: String): Result<String> {
        return if (remoteDataSource != null) {
            when (val result = remoteDataSource.requestPasswordReset(email)) {
                is NetworkResult.Success -> {
                    Result.success(result.data)
                }
                is NetworkResult.Error -> {
                    Result.failure(Exception(result.exception.message ?: "Erro ao solicitar recuperação"))
                }
                is NetworkResult.Loading -> {
                    Result.failure(Exception("Carregando..."))
                }
            }
        } else {
            requestPasswordResetLocal(email)
        }
    }

    private suspend fun requestPasswordResetLocal(email: String): Result<String> {
        delay(1500)

        if (!email.contains("@") || !email.contains(".")) {
            return Result.failure(Exception("Email inválido"))
        }

        return Result.success("Um link de recuperação foi enviado para $email")
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<String> {
        return if (remoteDataSource != null) {
            when (val result = remoteDataSource.resetPassword(token, newPassword)) {
                is NetworkResult.Success -> {
                    Result.success(result.data)
                }
                is NetworkResult.Error -> {
                    Result.failure(Exception(result.exception.message ?: "Erro ao redefinir senha"))
                }
                is NetworkResult.Loading -> {
                    Result.failure(Exception("Carregando..."))
                }
            }
        } else {
            resetPasswordLocal(token, newPassword)
        }
    }

    private suspend fun resetPasswordLocal(token: String, newPassword: String): Result<String> {
        delay(1500)

        if (token.isBlank()) {
            return Result.failure(Exception("Token inválido ou expirado"))
        }

        if (newPassword.length < 8) {
            return Result.failure(Exception("A senha deve ter pelo menos 8 caracteres"))
        }

        return Result.success("Senha redefinida com sucesso!")
    }

    override suspend fun logout(): Result<Unit> {
        return if (remoteDataSource != null) {
            when (val result = remoteDataSource.logout()) {
                is NetworkResult.Success -> {
                    tokenStorage?.clearTokens()
                    Result.success(Unit)
                }
                is NetworkResult.Error -> {
                    tokenStorage?.clearTokens()
                    Result.success(Unit)
                }
                is NetworkResult.Loading -> {
                    Result.failure(Exception("Carregando..."))
                }
            }
        } else {
            logoutLocal()
        }
    }

    private suspend fun logoutLocal(): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }
}

interface AuthTokenStorage {
    fun saveToken(token: String)
    fun getToken(): String?
    fun saveUserId(userId: String)
    fun getUserId(): String?
    fun clearTokens()
}
