package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription

@Serializable
data class PrescriptionDto(
    val id: String? = null,
    val petId: String,
    val userId: String,
    val veterinaryId: String,
    val medicalRecordId: String? = null,
    val prescriptionDate: String,
    val instructions: String,
    val diagnosis: String? = null,
    val validUntil: String? = null,
    val status: String,
    val medications: String,
    val observations: String,
    val active: Boolean,
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
    val veterinarian: String,
    val medicalRecordId: String? = null,
    val prescriptionDate: String,
    val instructions: String,
    val diagnosis: String? = null,
    val validUntil: String? = null,
    val medications: String? = null,
    val observations: String? = null
)

@Serializable
data class UpdatePrescriptionRequest(
    val instructions: String? = null,
    val diagnosis: String? = null,
    val validUntil: String? = null,
    val status: String? = null,
    val medications: String? = null,
    val observations: String? = null,
    val active: Boolean? = null
)

fun PrescriptionDto.toPrescription(): Prescription {
    return Prescription(
        id = id,
        petId = petId,
        userId = userId,
        veterinaryId = veterinaryId,
        medicalRecordId = medicalRecordId,
        prescriptionDate = prescriptionDate,
        instructions = instructions,
        diagnosis = diagnosis,
        validUntil = validUntil,
        status = status,
        medications = medications,
        observations = observations,
        active = active,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
