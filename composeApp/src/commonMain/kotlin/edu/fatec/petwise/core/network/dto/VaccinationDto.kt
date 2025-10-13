package edu.fatec.petwise.core.network.dto

import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.vaccinations.domain.models.VaccinationStatus
import edu.fatec.petwise.features.vaccinations.domain.models.VaccineType
import kotlinx.serialization.Serializable

@Serializable
data class VaccinationDto(
    val id: String,
    val petId: String,
    val petName: String,
    val vaccineName: String,
    val vaccineType: String,
    val applicationDate: String,
    val nextDoseDate: String? = null,
    val doseNumber: Int,
    val totalDoses: Int,
    val veterinarianName: String,
    val veterinarianCrmv: String,
    val clinicName: String,
    val batchNumber: String,
    val manufacturer: String,
    val observations: String = "",
    val sideEffects: String = "",
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateVaccinationRequest(
    val petId: String,
    val vaccineName: String,
    val vaccineType: String,
    val applicationDate: String,
    val nextDoseDate: String? = null,
    val doseNumber: Int,
    val totalDoses: Int,
    val veterinarianName: String,
    val veterinarianCrmv: String,
    val clinicName: String,
    val batchNumber: String,
    val manufacturer: String,
    val observations: String = "",
    val status: String = "AGENDADA"
)

@Serializable
data class UpdateVaccinationRequest(
    val vaccineName: String? = null,
    val applicationDate: String? = null,
    val nextDoseDate: String? = null,
    val veterinarianName: String? = null,
    val veterinarianCrmv: String? = null,
    val clinicName: String? = null,
    val batchNumber: String? = null,
    val manufacturer: String? = null,
    val observations: String? = null,
    val sideEffects: String? = null,
    val status: String? = null
)

@Serializable
data class VaccinationListResponse(
    val vaccinations: List<VaccinationDto>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

@Serializable
data class MarkAsAppliedRequest(
    val observations: String,
    val sideEffects: String = "",
    val applicationDate: String
)

@Serializable
data class ScheduleNextDoseRequest(
    val nextDoseDate: String
)

fun Vaccination.toDto(): VaccinationDto = VaccinationDto(
    id = id,
    petId = petId,
    petName = petName,
    vaccineName = vaccineName,
    vaccineType = vaccineType.name,
    applicationDate = applicationDate,
    nextDoseDate = nextDoseDate,
    doseNumber = doseNumber,
    totalDoses = totalDoses,
    veterinarianName = veterinarianName,
    veterinarianCrmv = veterinarianCrmv,
    clinicName = clinicName,
    batchNumber = batchNumber,
    manufacturer = manufacturer,
    observations = observations,
    sideEffects = sideEffects,
    status = status.name,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun VaccinationDto.toDomain(): Vaccination = Vaccination(
    id = id,
    petId = petId,
    petName = petName,
    vaccineName = vaccineName,
    vaccineType = VaccineType.valueOf(vaccineType),
    applicationDate = applicationDate,
    nextDoseDate = nextDoseDate,
    doseNumber = doseNumber,
    totalDoses = totalDoses,
    veterinarianName = veterinarianName,
    veterinarianCrmv = veterinarianCrmv,
    clinicName = clinicName,
    batchNumber = batchNumber,
    manufacturer = manufacturer,
    observations = observations,
    sideEffects = sideEffects,
    status = VaccinationStatus.valueOf(status),
    createdAt = createdAt,
    updatedAt = updatedAt
)
