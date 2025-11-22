package edu.fatec.petwise.features.exams.domain.models

import kotlinx.datetime.LocalDate

data class Exam(
    val id: String,
    val petId: String,
    val veterinaryId: String,
    val examType: String,
    val examDate: kotlinx.datetime.LocalDateTime,
    val examTime: String,
    val results: String?,
    val status: String,
    val notes: String?,
    val attachmentUrl: String?,
    val createdAt: String,
    val updatedAt: String
)
