package edu.fatec.petwise.core.network.dto

import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.models.PetSpecies
import edu.fatec.petwise.features.pets.domain.models.PetGender
import edu.fatec.petwise.features.pets.domain.models.HealthStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PetDto(
    @SerialName("id") val id: String,
    @SerialName("nome") val name: String,
    @SerialName("raca") val breed: String,
    @SerialName("especie") val species: String,
    @SerialName("sexo") val gender: String,
    // backend does not provide numeric age; keep as Int for domain usage if available
    @SerialName("idade") val age: Int,
    @SerialName("peso") val weight: Float,
    @SerialName("healthStatus") val healthStatus: String,
    // owner info is not part of backend CreatePetRequest, may be ignored by backend if present
    @SerialName("ownerName") val ownerName: String,
    @SerialName("ownerPhone") val ownerPhone: String,
    @SerialName("observacoes") val healthHistory: String = "",
    @SerialName("fotoUrl") val profileImageUrl: String? = null,
    @SerialName("isFavorite") val isFavorite: Boolean = false,
    @SerialName("nextAppointment") val nextAppointment: String? = null,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("updatedAt") val updatedAt: String
)

@Serializable
data class CreatePetRequest(
    @SerialName("nome") val name: String,
    @SerialName("raca") val breed: String,
    @SerialName("especie") val species: String,
    @SerialName("sexo") val gender: String,
    @SerialName("idade") val age: Int,
    @SerialName("peso") val weight: Float,
    @SerialName("healthStatus") val healthStatus: String,
    @SerialName("ownerName") val ownerName: String,
    @SerialName("ownerPhone") val ownerPhone: String,
    @SerialName("observacoes") val healthHistory: String = "",
    @SerialName("fotoUrl") val profileImageUrl: String? = null
)

@Serializable
data class UpdatePetRequest(
    @SerialName("nome") val name: String,
    @SerialName("raca") val breed: String,
    @SerialName("especie") val species: String,
    @SerialName("sexo") val gender: String,
    @SerialName("idade") val age: Int,
    @SerialName("peso") val weight: Float,
    @SerialName("healthStatus") val healthStatus: String,
    @SerialName("ownerName") val ownerName: String,
    @SerialName("ownerPhone") val ownerPhone: String,
    @SerialName("observacoes") val healthHistory: String = "",
    @SerialName("fotoUrl") val profileImageUrl: String? = null
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
