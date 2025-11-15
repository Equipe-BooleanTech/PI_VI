package edu.fatec.petwise.core.network.dto

import edu.fatec.petwise.features.medications.domain.models.Medication
import edu.fatec.petwise.features.medications.domain.models.MedicationFrequency
import edu.fatec.petwise.features.medications.domain.models.MedicationStatus
import kotlinx.serialization.Serializable

@Serializable
data class MedicationDto(
    val id: String,
    val userId: String,
    val petId: String,
    val prescriptionId: String? = null,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val durationDays: Int,
    val startDate: String,
    val endDate: String,
    val sideEffects: String = "",
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateMedicationRequest(
    val petId: String,
    val prescriptionId: String? = null,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val durationDays: Int,
    val startDate: String,
    val endDate: String,
    val sideEffects: String = ""
)

@Serializable
data class UpdateMedicationRequest(
    val dosage: String? = null,
    val frequency: String? = null,
    val durationDays: Int? = null,
    val endDate: String? = null,
    val sideEffects: String? = null
)

@Serializable
data class MedicationListResponse(
    val medications: List<MedicationDto>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

@Serializable
data class MedicationFilterRequest(
    val petId: String? = null,
    val status: String? = null,
    val medicationName: String? = null,
    val searchQuery: String = ""
)

@Serializable
data class UpdateMedicationStatusRequest(
    val status: String
)

@Serializable
data class CompleteMedicationRequest(
    val completionDate: String,
    val observations: String = ""
)

fun Medication.toDto(): MedicationDto = MedicationDto(
    id = id,
    userId = userId,
    petId = petId,
    prescriptionId = prescriptionId.takeIf { it.isNotBlank() },
    medicationName = medicationName,
    dosage = dosage,
    frequency = frequency,
    durationDays = durationDays,
    startDate = startDate,
    endDate = endDate,
    sideEffects = sideEffects,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun MedicationDto.toDomain(): Medication = Medication(
    id = id,
    userId = userId,
    petId = petId,
    prescriptionId = prescriptionId ?: "",
    medicationName = medicationName,
    dosage = dosage,
    frequency = frequency,
    durationDays = durationDays,
    startDate = startDate,
    endDate = endDate,
    sideEffects = sideEffects,
    createdAt = createdAt,
    updatedAt = updatedAt
)