package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable


@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val deviceId: String? = null,
    val platform: String? = null
)

@Serializable
data class LoginResponse(
    val userId: String,
    val token: String,
    val expiresIn: Long,
    val userType: String,
    val fullName: String,
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val userType: String,
    val phone: String? = null,
    val cpf: String? = null,
    val cnpj: String? = null,
    val companyName: String? = null,
    val crmv: String? = null,
    val adminCode: String? = null
)

@Serializable
data class RegisterResponse(
    val userId: String,
    val token: String,
    val email: String,
    val userType: String,
    val fullName: String,
    val expiresIn: Long = 86400
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class ForgotPasswordResponse(
    val message: String,
    val resetTokenSent: Boolean
)

@Serializable
data class ResetPasswordRequest(
    val resetToken: String,
    val newPassword: String
)

@Serializable
data class ResetPasswordResponse(
    val message: String,
    val success: Boolean
)

@Serializable
data class UserProfileDto(
    val id: String,
    val email: String,
    val fullName: String,
    val userType: String,
    val phone: String? = null,
    val profileImageUrl: String? = null,
    val verified: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)
