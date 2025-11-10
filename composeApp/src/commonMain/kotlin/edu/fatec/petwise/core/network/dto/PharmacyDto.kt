package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable
import edu.fatec.petwise.features.pharmacies.domain.models.Pharmacy

@Serializable
data class PharmacyListResponse(
    val pharmacies: List<UserProfileDto>,
    val total: Int
)

fun UserProfileDto.toPharmacy(): Pharmacy {
    return Pharmacy(
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
