package edu.fatec.petwise.features.pets.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Pet(
    val id: String,
    val name: String,
    val breed: String,
    val species: PetSpecies,
    val gender: PetGender,
    val age: Int,
    val weight: Float,
    val healthStatus: HealthStatus,
    val ownerId: String,
    val ownerName: String,
    val ownerPhone: String,
    val healthHistory: String = "",
    val profileImageUrl: String? = null,
    val isFavorite: Boolean = false,
    val nextAppointment: String? = null,
    val createdAt: String,
    val updatedAt: String
)

enum class PetSpecies(val displayName: String) {
    DOG("Cão"),
    CAT("Gato"),
    BIRD("Ave"),
    RABBIT("Coelho"),
    OTHER("Outro")
}

enum class PetGender(val displayName: String) {
    MALE("Macho"),
    FEMALE("Fêmea")
}

enum class HealthStatus(val displayName: String, val color: String) {
    EXCELLENT("Excelente", "#00b942"),
    GOOD("Bom", "#4CAF50"),
    REGULAR("Regular", "#FFC107"),
    ATTENTION("Atenção", "#FF9800"),
    CRITICAL("Crítico", "#F44336")
}

data class PetFilterOptions(
    val species: PetSpecies? = null,
    val healthStatus: HealthStatus? = null,
    val favoritesOnly: Boolean = false,
    val searchQuery: String = ""
)