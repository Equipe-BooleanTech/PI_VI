package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*
import io.ktor.client.statement.*


interface AuthApiService {
    suspend fun login(request: LoginRequest): NetworkResult<LoginResponse>
    suspend fun register(request: RegisterRequest): NetworkResult<RegisterResponse>
    suspend fun refreshToken(request: RefreshTokenRequest): NetworkResult<RefreshTokenResponse>
    suspend fun forgotPassword(request: ForgotPasswordRequest): NetworkResult<ForgotPasswordResponse>
    suspend fun resetPassword(request: ResetPasswordRequest): NetworkResult<ResetPasswordResponse>
    suspend fun logout(): NetworkResult<Unit>
    suspend fun verifyEmail(token: String): NetworkResult<UserProfileDto>
    suspend fun getUserProfile(): NetworkResult<UserProfileDto>
}

class AuthApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : AuthApiService {

    override suspend fun login(request: LoginRequest): NetworkResult<LoginResponse> {
        return networkHandler.post<LoginResponse, LoginRequest>(
            urlString = ApiEndpoints.LOGIN,
            body = request
        )
    }

    override suspend fun register(request: RegisterRequest): NetworkResult<RegisterResponse> {
        return networkHandler.post<RegisterResponse, RegisterRequest>(
            urlString = ApiEndpoints.REGISTER,
            body = request
        )
    }

    override suspend fun refreshToken(request: RefreshTokenRequest): NetworkResult<RefreshTokenResponse> {
        return networkHandler.post<RefreshTokenResponse, RefreshTokenRequest>(
            urlString = ApiEndpoints.REFRESH_TOKEN,
            body = request
        )
    }

    override suspend fun forgotPassword(request: ForgotPasswordRequest): NetworkResult<ForgotPasswordResponse> {
        return networkHandler.post<ForgotPasswordResponse, ForgotPasswordRequest>(
            urlString = ApiEndpoints.FORGOT_PASSWORD,
            body = request
        )
    }

    override suspend fun resetPassword(request: ResetPasswordRequest): NetworkResult<ResetPasswordResponse> {
        return networkHandler.post<ResetPasswordResponse, ResetPasswordRequest>(
            urlString = ApiEndpoints.RESET_PASSWORD,
            body = request
        )
    }

    override suspend fun logout(): NetworkResult<Unit> {
        return networkHandler.post<Unit, Unit>(
            urlString = ApiEndpoints.LOGOUT,
            body = Unit
        )
    }

    override suspend fun verifyEmail(token: String): NetworkResult<UserProfileDto> {
        return networkHandler.get<UserProfileDto>(ApiEndpoints.VERIFY_EMAIL) {
            parameter("token", token)
        }
    }

    override suspend fun getUserProfile(): NetworkResult<UserProfileDto> {
        return networkHandler.getWithCustomDeserializer(
            urlString = ApiEndpoints.USER_PROFILE,
            deserializer = { jsonString -> UserProfileDto.fromJson(jsonString) }
        )
    }
}
