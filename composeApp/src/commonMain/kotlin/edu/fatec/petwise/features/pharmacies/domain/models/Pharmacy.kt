package edu.fatec.petwise.features.pharmacies.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Pharmacy(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String? = null,
    val userType: String,
    val profileImageUrl: String? = null,
    val verified: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)

data class PharmacyFilterOptions(
    val verified: Boolean? = null,
    val searchQuery: String = ""
)

data class PharmacySearchCriteria(
    val name: String = "",
    val email: String = "",
    val phone: String = ""
)
