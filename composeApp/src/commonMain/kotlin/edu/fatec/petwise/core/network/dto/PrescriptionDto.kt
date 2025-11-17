package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription

@Serializable
data class PrescriptionDto(
    val id: String,
    val petId: String,
    val veterinaryId: String,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val duration: String,
    val startDate: String,
    val endDate: String? = null,
    val instructions: String? = null,
    val notes: String? = null,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class PrescriptionListResponse(
    val prescriptions: List<PrescriptionDto>? = null,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

@Serializable
data class CreatePrescriptionRequest(
    val petId: String,
    val veterinaryId: String,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val duration: String,
    val startDate: String,
    val endDate: String? = null,
    val instructions: String? = null,
    val notes: String? = null,
    val status: String = "ACTIVE"
)

@Serializable
data class UpdatePrescriptionRequest(
    val medicationName: String? = null,
    val dosage: String? = null,
    val frequency: String? = null,
    val duration: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val instructions: String? = null,
    val notes: String? = null,
    val status: String? = null
)

fun PrescriptionDto.toPrescription(): Prescription {
    return Prescription(
        id = id,
        petId = petId,
        veterinaryId = veterinaryId,
        medicationName = medicationName,
        dosage = dosage,
        frequency = frequency,
        duration = duration,
        startDate = startDate,
        endDate = endDate,
        instructions = instructions,
        notes = notes,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
