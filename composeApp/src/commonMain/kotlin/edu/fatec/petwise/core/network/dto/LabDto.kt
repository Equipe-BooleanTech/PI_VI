package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable
import edu.fatec.petwise.features.labs.domain.models.Lab

@Serializable
data class LabDto(
    val id: String,
    val veterinaryId: String,
    val labName: String,
    val testType: String,
    val testDate: String,
    val results: String? = null,
    val status: String,
    val notes: String? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class LabListResponse(
    val labs: List<LabDto>,
    val total: Int = labs.size,
    val page: Int = 1,
    val pageSize: Int = 20
)

@Serializable
data class CreateLabRequest(
    val veterinaryId: String,
    val labName: String,
    val testType: String,
    val testDate: String,
    val results: String? = null,
    val status: String = "PENDING",
    val notes: String? = null
)

@Serializable
data class UpdateLabRequest(
    val labName: String? = null,
    val testType: String? = null,
    val testDate: String? = null,
    val results: String? = null,
    val status: String? = null,
    val notes: String? = null
)

fun LabDto.toLab(): Lab {
    return Lab(
        id = id,
        veterinaryId = veterinaryId,
        labName = labName,
        testType = testType,
        testDate = testDate,
        results = results,
        status = status,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
