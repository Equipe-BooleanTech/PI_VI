package edu.fatec.petwise.features.labs.domain.models

data class Lab(
    val id: String,
    val veterinaryId: String,
    val labName: String,
    val testType: String,
    val testDate: String,
    val results: String?,
    val status: String,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String
)
