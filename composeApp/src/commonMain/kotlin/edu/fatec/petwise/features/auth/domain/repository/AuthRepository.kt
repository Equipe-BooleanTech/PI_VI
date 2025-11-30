package edu.fatec.petwise.features.auth.domain.repository


interface AuthRepository {

    suspend fun login(email: String, password: String): Result<String>

    suspend fun register(registerRequest: edu.fatec.petwise.core.network.dto.RegisterRequest): Result<String>

    suspend fun requestPasswordReset(email: String): Result<String>

    suspend fun resetPassword(token: String, newPassword: String): Result<String>
    
    suspend fun getUserProfile(): Result<edu.fatec.petwise.core.network.dto.UserProfileDto>
    
    suspend fun logout(): Result<Unit>
}
