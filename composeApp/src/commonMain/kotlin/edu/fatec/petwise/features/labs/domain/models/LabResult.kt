package edu.fatec.petwise.features.labs.domain.models

data class LabResult(
    val id: String,
    val petId: String,
    val veterinaryId: String,
    val labType: String,
    val labDate: String,
    val results: String?,
    val status: String,
    val notes: String?,
    val attachmentUrl: String?,
    val createdAt: String,
    val updatedAt: String
)
