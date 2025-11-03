package edu.fatec.petwise.core.network.dto

import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.models.PetSpecies
import edu.fatec.petwise.features.pets.domain.models.PetGender
import edu.fatec.petwise.features.pets.domain.models.HealthStatus
import kotlinx.serialization.Serializable

@Serializable
data class PetDto(
    val id: String,
    val name: String,
    val breed: String,
    val species: String,
    val gender: String,
    val age: Int,
    val weight: Float,
    val healthStatus: String,
    val ownerName: String,
    val ownerPhone: String,
    val healthHistory: String = "",
    val profileImageUrl: String? = null,
    val isFavorite: Boolean = false,
    val nextAppointment: String? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreatePetRequest(
    val name: String,
    val breed: String,
    val species: String,
    val gender: String,
    val age: Int,
    val weight: Float,
    val healthStatus: String,
    val ownerName: String,
    val ownerPhone: String,
    val healthHistory: String = "",
    val profileImageUrl: String? = null
)

@Serializable
data class UpdatePetRequest(
    val name: String,
    val breed: String,
    val species: String,
    val gender: String,
    val age: Int,
    val weight: Float,
    val healthStatus: String,
    val ownerName: String,
    val ownerPhone: String,
    val healthHistory: String = "",
    val profileImageUrl: String? = null
)

@Serializable
data class PetListResponse(
    val pets: List<PetDto>,
    var total: Int,
    val page: Int,
    val pageSize: Int
)

@Serializable
data class ToggleFavoriteResponse(
    val petId: String,
    val isFavorite: Boolean
)

@Serializable
data class UpdateHealthStatusRequest(
    val healthStatus: String,
    val notes: String? = null
)

fun Pet.toDto(): PetDto = PetDto(
    id = id,
    name = name,
    breed = breed,
    species = species.displayName,
    gender = gender.displayName,
    age = age,
    weight = weight,
    healthStatus = healthStatus.displayName,
    ownerName = ownerName,
    ownerPhone = ownerPhone,
    healthHistory = healthHistory,
    profileImageUrl = profileImageUrl,
    isFavorite = isFavorite,
    nextAppointment = nextAppointment,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun mapSpeciesFromPortuguese(species: String): PetSpecies {
    return when (species.lowercase()) {
        "gato" -> PetSpecies.CAT
        "cão", "cachorro", "dog" -> PetSpecies.DOG
        "ave", "pássaro", "bird" -> PetSpecies.BIRD
        "coelho", "rabbit" -> PetSpecies.RABBIT
        else -> PetSpecies.OTHER
    }
}

private fun mapGenderFromPortuguese(gender: String): PetGender {
    return when (gender.lowercase()) {
        "fêmea", "femea", "female", "f" -> PetGender.FEMALE
        "macho", "male", "m" -> PetGender.MALE
        else -> PetGender.MALE
    }
}

private fun mapHealthStatusFromPortuguese(healthStatus: String): HealthStatus {
    return when (healthStatus.lowercase()) {
        "excelente", "excellent" -> HealthStatus.EXCELLENT
        "bom", "boa", "good" -> HealthStatus.GOOD
        "regular", "ok" -> HealthStatus.REGULAR
        "atenção", "atencao", "attention" -> HealthStatus.ATTENTION
        "crítico", "critico", "critical" -> HealthStatus.CRITICAL
        else -> HealthStatus.GOOD
    }
}

fun PetDto.toDomain(): Pet = Pet(
    id = id,
    name = name,
    breed = breed,
    species = mapSpeciesFromPortuguese(species),
    gender = mapGenderFromPortuguese(gender),
    age = age,
    weight = weight,
    healthStatus = mapHealthStatusFromPortuguese(healthStatus),
    ownerName = ownerName,
    ownerPhone = ownerPhone,
    healthHistory = healthHistory,
    profileImageUrl = profileImageUrl,
    isFavorite = isFavorite,
    nextAppointment = nextAppointment,
    createdAt = createdAt,
    updatedAt = updatedAt
)
