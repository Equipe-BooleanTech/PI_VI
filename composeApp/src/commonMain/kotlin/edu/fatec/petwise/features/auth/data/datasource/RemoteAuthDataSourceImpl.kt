package edu.fatec.petwise.features.auth.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.AuthApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.core.network.NetworkException

class RemoteAuthDataSourceImpl(
    private val authApiService: AuthApiService
) : RemoteAuthDataSource {

    override suspend fun login(email: String, password: String): NetworkResult<AuthResult> {
        val request = LoginRequest(
            email = email,
            password = password,
            platform = getPlatformInfo()
        )

        return when (val result = authApiService.login(request)) {
            is NetworkResult.Success -> {
                val response = result.data
                NetworkResult.Success(
                    AuthResult(
                        userId = response.userId,
                        token = response.token,
                        expiresIn = response.expiresIn,
                        userType = response.userType,
                        fullName = response.fullName
                    )
                )
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun register(registerRequest: edu.fatec.petwise.core.network.dto.RegisterRequest): NetworkResult<AuthResult> {
        return when (val result = authApiService.register(registerRequest)) {
            is NetworkResult.Success -> {
                val response = result.data
                NetworkResult.Success(
                    AuthResult(
                        userId = response.userId,
                        token = response.token,
                        expiresIn = response.expiresIn,
                        userType = response.userType,
                        fullName = response.fullName
                    )
                )
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun requestPasswordReset(email: String): NetworkResult<String> {
        val request = ForgotPasswordRequest(email = email)

        return when (val result = authApiService.forgotPassword(request)) {
            is NetworkResult.Success -> {
                NetworkResult.Success(result.data.message)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun resetPassword(token: String, newPassword: String): NetworkResult<String> {
        val request = ResetPasswordRequest(
            resetToken = token,
            newPassword = newPassword
        )

        return when (val result = authApiService.resetPassword(request)) {
            is NetworkResult.Success -> {
                NetworkResult.Success(result.data.message)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun getUserProfile(): NetworkResult<edu.fatec.petwise.core.network.dto.UserProfileDto> {
        return authApiService.getUserProfile()
    }

    override suspend fun logout(): NetworkResult<Unit> {
        return authApiService.logout()
    }
}

private fun getPlatformInfo(): String {
    return "KMP"
}
