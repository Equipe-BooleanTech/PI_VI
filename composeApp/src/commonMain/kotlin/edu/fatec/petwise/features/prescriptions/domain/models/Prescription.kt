package edu.fatec.petwise.features.prescriptions.domain.models

data class Prescription(
    val id: String,
    val petId: String,
    val veterinaryId: String,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val duration: String,
    val startDate: String,
    val endDate: String?,
    val instructions: String?,
    val notes: String?,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)
