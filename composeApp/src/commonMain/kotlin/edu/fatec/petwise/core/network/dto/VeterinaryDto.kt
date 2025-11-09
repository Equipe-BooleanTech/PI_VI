package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable
import edu.fatec.petwise.features.veterinaries.domain.models.Veterinary
@Serializable
data class VeterinaryListResponse(
    val veterinaries: List<UserProfileDto>,
    val total: Int
)

fun UserProfileDto.toVeterinary(): Veterinary {
    return Veterinary(
        id = id,
        fullName = fullName,
        email = email,
        phone = phone,
        userType = userType,
        profileImageUrl = profileImageUrl,
        verified = verified,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}