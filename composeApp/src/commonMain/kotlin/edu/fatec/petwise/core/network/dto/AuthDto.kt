package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive


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
    val specialization: String? = null,
    val crmv: String? = null,
    val adminCode: String? = null,
    val active: Boolean? = null
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
    val message: String
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
) {
    companion object {
        fun fromJson(json: String): UserProfileDto {
            val jsonElement = Json.parseToJsonElement(json)
            return fromJsonElement(jsonElement)
        }

        private fun fromJsonElement(jsonElement: JsonElement): UserProfileDto {
            val obj = jsonElement as JsonObject
            
            return UserProfileDto(
                id = obj["id"]?.jsonPrimitive?.contentOrNull ?: "",
                email = extractValue(obj["email"]) ?: "",
                fullName = obj["fullName"]?.jsonPrimitive?.contentOrNull ?: "",
                userType = obj["userType"]?.jsonPrimitive?.contentOrNull ?: "",
                phone = extractValue(obj["phone"]),
                profileImageUrl = obj["profileImageUrl"]?.jsonPrimitive?.contentOrNull,
                verified = obj["verified"]?.jsonPrimitive?.boolean ?: false,
                createdAt = obj["createdAt"]?.jsonPrimitive?.contentOrNull ?: "",
                updatedAt = obj["updatedAt"]?.jsonPrimitive?.contentOrNull ?: ""
            )
        }

        private fun extractValue(jsonElement: JsonElement?): String? {
            return when (jsonElement) {
                is JsonObject -> jsonElement["value"]?.jsonPrimitive?.contentOrNull
                is JsonPrimitive -> jsonElement.contentOrNull
                else -> null
            }
        }
    }
}
