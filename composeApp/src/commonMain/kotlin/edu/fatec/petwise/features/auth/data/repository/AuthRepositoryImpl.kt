package edu.fatec.petwise.features.auth.data.repository

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.auth.data.datasource.RemoteAuthDataSource
import edu.fatec.petwise.features.auth.domain.repository.AuthRepository
import edu.fatec.petwise.features.auth.di.AuthTokenStorageImpl

class AuthRepositoryImpl(
    private val remoteDataSource: RemoteAuthDataSource,
    private val tokenStorage: AuthTokenStorage? = null
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            println("Repositório: Iniciando login para email '$email' via API")
            when (val result = remoteDataSource.login(email, password)) {
                is NetworkResult.Success -> {
                    // Save token with expiration information
                    if (tokenStorage is AuthTokenStorageImpl) {
                        tokenStorage.saveTokenWithExpiration(result.data.token, result.data.expiresIn)
                    } else {
                        tokenStorage?.saveToken(result.data.token)
                    }
                    tokenStorage?.saveUserId(result.data.userId)
                    
                    // Set token in network module with expiration
                    NetworkModule.setAuthTokenWithExpiration(result.data.token, result.data.expiresIn)
                    
                    println("Repositório: Login realizado com sucesso - Usuário: ${result.data.userId}, Token expira em: ${result.data.expiresIn}s")
                    Result.success("Login realizado com sucesso")
                }
                is NetworkResult.Error -> {
                    println("Repositório: Erro no login - ${result.exception.message}")
                    Result.failure(Exception(result.exception.message ?: "Erro ao fazer login"))
                }
                is NetworkResult.Loading -> {
                    println("Repositório: Login em andamento...")
                    Result.failure(Exception("Login em andamento"))
                }
            }
        } catch (e: Exception) {
            println("Repositório: Falha inesperada no login - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun register(registerRequest: edu.fatec.petwise.core.network.dto.RegisterRequest): Result<String> {
        return try {
            println("Repositório: Iniciando registro para email '${registerRequest.email}' via API")
            when (val result = remoteDataSource.register(registerRequest)) {
                is NetworkResult.Success -> {
                    tokenStorage?.saveToken(result.data.token)
                    tokenStorage?.saveUserId(result.data.userId)
                    NetworkModule.setAuthToken(result.data.token)
                    println("Repositório: Registro realizado com sucesso - Usuário: ${result.data.userId}")
                    Result.success(result.data.userId)
                }
                is NetworkResult.Error -> {
                    println("Repositório: Erro no registro - ${result.exception.message}")
                    Result.failure(Exception(result.exception.message ?: "Erro ao registrar usuário"))
                }
                is NetworkResult.Loading -> {
                    println("Repositório: Registro em andamento...")
                    Result.failure(Exception("Registro em andamento"))
                }
            }
        } catch (e: Exception) {
            println("Repositório: Falha inesperada no registro - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun requestPasswordReset(email: String): Result<String> {
        return try {
            println("Repositório: Solicitando recuperação de senha para email '$email' via API")
            when (val result = remoteDataSource.requestPasswordReset(email)) {
                is NetworkResult.Success -> {
                    println("Repositório: Solicitação de recuperação enviada com sucesso")
                    Result.success(result.data)
                }
                is NetworkResult.Error -> {
                    println("Repositório: Erro na recuperação de senha - ${result.exception.message}")
                    Result.failure(Exception(result.exception.message ?: "Erro ao solicitar recuperação de senha"))
                }
                is NetworkResult.Loading -> {
                    println("Repositório: Solicitação de recuperação em andamento...")
                    Result.failure(Exception("Solicitação em andamento"))
                }
            }
        } catch (e: Exception) {
            println("Repositório: Falha inesperada na recuperação de senha - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<String> {
        return try {
            println("Repositório: Redefinindo senha com token fornecido via API")
            when (val result = remoteDataSource.resetPassword(token, newPassword)) {
                is NetworkResult.Success -> {
                    println("Repositório: Senha redefinida com sucesso")
                    Result.success(result.data)
                }
                is NetworkResult.Error -> {
                    println("Repositório: Erro na redefinição de senha - ${result.exception.message}")
                    Result.failure(Exception(result.exception.message ?: "Erro ao redefinir senha"))
                }
                is NetworkResult.Loading -> {
                    println("Repositório: Redefinição de senha em andamento...")
                    Result.failure(Exception("Redefinição em andamento"))
                }
            }
        } catch (e: Exception) {
            println("Repositório: Falha inesperada na redefinição de senha - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(): Result<edu.fatec.petwise.core.network.dto.UserProfileDto> {
        return try {
            println("Repositório: Buscando perfil do usuário via API")
            when (val result = remoteDataSource.getUserProfile()) {
                is NetworkResult.Success -> {
                    println("Repositório: Perfil do usuário obtido com sucesso")
                    Result.success(result.data)
                }
                is NetworkResult.Error -> {
                    when (result.exception) {
                        is edu.fatec.petwise.core.network.NetworkException.Unauthorized -> {
                            println("Repositório: Token inválido ao buscar perfil - limpando tokens localmente")
                            tokenStorage?.clearTokens()
                            NetworkModule.clearAuthToken()
                            Result.failure(Exception("Token expirado - faça login novamente"))
                        }
                        is kotlinx.coroutines.CancellationException -> {
                            println("Repositório: Requisição de perfil cancelada - mantendo sessão ativa")
                            Result.failure(Exception("Busca de perfil cancelada - sessão mantida"))
                        }
                        else -> {
                            println("Repositório: Erro ao buscar perfil - ${result.exception.message}")
                            Result.failure(Exception(result.exception.message ?: "Erro ao buscar perfil do usuário"))
                        }
                    }
                }
                is NetworkResult.Loading -> {
                    println("Repositório: Busca de perfil em andamento...")
                    Result.failure(Exception("Busca em andamento"))
                }
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            println("Repositório: Busca de perfil cancelada - mantendo estado de autenticação")
            Result.failure(Exception("Operação cancelada - sessão mantida"))
        } catch (e: Exception) {
            println("Repositório: Falha inesperada ao buscar perfil - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            println("Repositório: Realizando logout local (limpeza de tokens e cancelamento de operações)")
            
            tokenStorage?.clearTokens()
            NetworkModule.clear()
            println("Repositório: Logout local realizado com sucesso, tokens limpos e operações canceladas")
            Result.success(Unit)
        } catch (e: Exception) {
            tokenStorage?.clearTokens()
            NetworkModule.clear()
            println("Repositório: Logout local realizado mesmo com erro - ${e.message}")
            Result.success(Unit)
        }
    }
}

interface AuthTokenStorage {
    fun saveToken(token: String)
    fun getToken(): String?
    fun saveUserId(userId: String)
    fun getUserId(): String?
    fun clearTokens()
}
