package edu.fatec.petwise.features.auth.domain.repository

/**
 * Interface para repositório de autenticação
 */
interface AuthRepository {

    suspend fun login(email: String, password: String): Result<String>

    suspend fun register(userData: Map<String, String>): Result<String>

    suspend fun requestPasswordReset(email: String): Result<String>

    suspend fun resetPassword(token: String, newPassword: String): Result<String>
    
    suspend fun logout(): Result<Unit>
}
