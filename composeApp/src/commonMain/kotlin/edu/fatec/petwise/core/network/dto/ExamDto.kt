package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime
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
    val examType: String,
    val examDate: String,
    val results: String? = null,
    val status: String,
    val notes: String? = null,
    val attachmentUrl: String? = null
)

@Serializable
data class UpdateExamRequest(
    val petId: String? = null,
    val examType: String? = null,
    val examDate: String? = null,
    val results: String? = null,
    val status: String? = null,
    val notes: String? = null,
    val attachmentUrl: String? = null
)

fun ExamDto.toExam(): Exam {
    val examDateTime = LocalDateTime.parse(examDate)
    return Exam(
        id = id,
        petId = petId,
        veterinaryId = veterinaryId,
        examType = examType,
        examDate = examDateTime,
        examTime = "${examDateTime.hour.toString().padStart(2, '0')}:${examDateTime.minute.toString().padStart(2, '0')}",
        results = results,
        status = status,
        notes = notes,
        attachmentUrl = attachmentUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Exam.toDto(): ExamDto {
    return ExamDto(
        id = id,
        petId = petId,
        veterinaryId = veterinaryId,
        examType = examType,
        examDate = examDate.toString(),
        results = results,
        status = status,
        notes = notes,
        attachmentUrl = attachmentUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Exam.toCreateRequest(): CreateExamRequest {
    return CreateExamRequest(
        petId = petId,
        examType = examType,
        examDate = examDate.toString(),
        results = results,
        status = status,
        notes = notes,
        attachmentUrl = attachmentUrl
    )
}

fun Exam.toUpdateRequest(): UpdateExamRequest {
    return UpdateExamRequest(
        petId = petId,
        examType = examType,
        examDate = examDate.toString(),
        results = results,
        status = status,
        notes = notes,
        attachmentUrl = attachmentUrl
    )
}
