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
    val name: String? = null,
    val breed: String? = null,
    val age: Int? = null,
    val weight: Float? = null,
    val healthStatus: String? = null,
    val ownerName: String? = null,
    val ownerPhone: String? = null,
    val healthHistory: String? = null,
    val profileImageUrl: String? = null
)

@Serializable
data class PetListResponse(
    val pets: List<PetDto>,
    val total: Int,
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
    species = species.name,
    gender = gender.name,
    age = age,
    weight = weight,
    healthStatus = healthStatus.name,
    ownerName = ownerName,
    ownerPhone = ownerPhone,
    healthHistory = healthHistory,
    profileImageUrl = profileImageUrl,
    isFavorite = isFavorite,
    nextAppointment = nextAppointment,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun PetDto.toDomain(): Pet = Pet(
    id = id,
    name = name,
    breed = breed,
    species = PetSpecies.valueOf(species),
    gender = PetGender.valueOf(gender),
    age = age,
    weight = weight,
    healthStatus = HealthStatus.valueOf(healthStatus),
    ownerName = ownerName,
    ownerPhone = ownerPhone,
    healthHistory = healthHistory,
    profileImageUrl = profileImageUrl,
    isFavorite = isFavorite,
    nextAppointment = nextAppointment,
    createdAt = createdAt,
    updatedAt = updatedAt
)
