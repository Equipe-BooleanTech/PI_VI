package edu.fatec.petwise.features.auth.data.datasource

import edu.fatec.petwise.core.network.NetworkResult

interface RemoteAuthDataSource {
    suspend fun login(email: String, password: String): NetworkResult<AuthResult>
    suspend fun register(registerRequest: edu.fatec.petwise.core.network.dto.RegisterRequest): NetworkResult<AuthResult>
    suspend fun requestPasswordReset(email: String): NetworkResult<String>
    suspend fun resetPassword(token: String, newPassword: String): NetworkResult<String>
    suspend fun getUserProfile(): NetworkResult<edu.fatec.petwise.core.network.dto.UserProfileDto>
    suspend fun logout(): NetworkResult<Unit>
}

data class AuthResult(
    val userId: String,
    val token: String,
    val expiresIn: Long,
    val userType: String,
    val fullName: String,
)
