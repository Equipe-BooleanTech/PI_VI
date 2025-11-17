package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable
import edu.fatec.petwise.features.exams.domain.models.Exam

@Serializable
data class ExamDto(
    val id: String,
    val petId: String,
    val veterinaryId: String,
    val examType: String,
    val examDate: String,
    val results: String? = null,
    val status: String,
    val notes: String? = null,
    val attachmentUrl: String? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class ExamListResponse(
    val exams: List<ExamDto>? = null,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

@Serializable
data class CreateExamRequest(
    val petId: String,
    val veterinaryId: String,
    val examType: String,
    val examDate: String,
    val results: String? = null,
    val status: String = "PENDING",
    val notes: String? = null,
    val attachmentUrl: String? = null
)

@Serializable
data class UpdateExamRequest(
    val examType: String? = null,
    val examDate: String? = null,
    val results: String? = null,
    val status: String? = null,
    val notes: String? = null,
    val attachmentUrl: String? = null
)

fun ExamDto.toExam(): Exam {
    return Exam(
        id = id,
        petId = petId,
        veterinaryId = veterinaryId,
        examType = examType,
        examDate = examDate,
        results = results,
        status = status,
        notes = notes,
        attachmentUrl = attachmentUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
