package edu.fatec.petwise.features.exams.domain.models

data class Exam(
    val id: String,
    val petId: String,
    val veterinaryId: String,
    val examType: String,
    val examDate: String,
    val results: String?,
    val status: String,
    val notes: String?,
    val attachmentUrl: String?,
    val createdAt: String,
    val updatedAt: String
)
