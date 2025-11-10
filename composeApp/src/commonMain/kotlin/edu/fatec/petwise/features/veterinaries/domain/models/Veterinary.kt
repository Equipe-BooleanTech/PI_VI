package edu.fatec.petwise.features.veterinaries.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Veterinary(
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

data class VeterinaryFilterOptions(
    val verified: Boolean? = null,
    val searchQuery: String = ""
)

data class VeterinarySearchCriteria(
    val name: String = "",
    val email: String = "",
    val phone: String = ""
)