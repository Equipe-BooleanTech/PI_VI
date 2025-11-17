package edu.fatec.petwise.core.network.dto

import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.vaccinations.domain.models.VaccinationStatus
import edu.fatec.petwise.features.vaccinations.domain.models.VaccineType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class VaccinationDto(
    val id: String,
    val petId: String,
    val veterinarianId: String,
    val vaccineType: String,
    val vaccinationDate: String,
    val nextDoseDate: String? = null,
    val totalDoses: Int,
    val manufacturer: String? = null,
    val observations: String = "",
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateVaccinationRequest(
    val petId: String,
    val veterinarianId: String,
    val vaccineType: String,
    val vaccinationDate: String,
    val nextDoseDate: String? = null,
    val totalDoses: Int,
    val manufacturer: String,
    val observations: String = "",
    val status: String = "AGENDADA"
)

@Serializable
data class UpdateVaccinationRequest(
    val petId: String,
    val veterinarianId: String,
    val vaccineType: String,
    val vaccinationDate: String,
    val nextDoseDate: String? = null,
    val totalDoses: Int,
    val manufacturer: String,
    val observations: String = "",
    val status: String = "AGENDADA"
)

@Serializable
data class VaccinationListResponse(
    val vaccinations: List<VaccinationDto>? = null,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

@Serializable
data class MarkAsAppliedRequest(
    val observations: String,
       val vaccinationDate: String
)

@Serializable
data class ScheduleNextDoseRequest(
    val nextDoseDate: String
)

fun Vaccination.toDto(): VaccinationDto = VaccinationDto(
    id = id,
    petId = petId,
    veterinarianId = veterinarianId,
    vaccineType = vaccineType.name,
    vaccinationDate = vaccinationDate,
    nextDoseDate = nextDoseDate,
    totalDoses = totalDoses,
    manufacturer = manufacturer,
    observations = observations,
    status = status.name,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun VaccinationDto.toDomain(): Vaccination = Vaccination(
    id = id,
    petId = petId,
    veterinarianId = veterinarianId,
    vaccineType = VaccineType.valueOf(vaccineType),
    vaccinationDate = vaccinationDate,
    nextDoseDate = nextDoseDate,
    totalDoses = totalDoses,
    manufacturer = manufacturer,
    observations = observations,
    status = VaccinationStatus.valueOf(status),
    createdAt = createdAt,
    updatedAt = updatedAt
)
