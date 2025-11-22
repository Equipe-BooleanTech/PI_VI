package edu.fatec.petwise.features.prescriptions.domain.models

data class Prescription(
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
