package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable
import edu.fatec.petwise.features.labs.domain.models.LabResult

@Serializable
data class LabDto(
    val id: String,
    val name: String,
    val contactInfo: String?,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class LabListResponse(
    val labs: List<LabDto>? = null,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

@Serializable
data class CreateLabRequest(
    val name: String,
    val contactInfo: String?
)

@Serializable
data class UpdateLabRequest(
    val name: String? = null,
    val contactInfo: String? = null
)

@Serializable
data class LabResultDto(
    val id: String,
    val petId: String,
    val veterinaryId: String,
    val labType: String,
    val labDate: String,
    val results: String? = null,
    val status: String,
    val notes: String? = null,
    val attachmentUrl: String? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class LabResultListResponse(
    val labResults: List<LabResultDto>? = null,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

@Serializable
data class CreateLabResultRequest(
    val labType: String,
    val labDate: String,
    val results: String? = null,
    val status: String,
    val notes: String? = null,
    val attachmentUrl: String? = null
)

@Serializable
data class UpdateLabResultRequest(
    val labType: String? = null,
    val labDate: String? = null,
    val results: String? = null,
    val status: String? = null,
    val notes: String? = null,
    val attachmentUrl: String? = null
)

fun LabResultDto.toLabResult(): LabResult {
    return LabResult(
        id = id,
        petId = petId,
        veterinaryId = veterinaryId,
        labType = labType,
        labDate = labDate,
        results = results,
        status = status,
        notes = notes,
        attachmentUrl = attachmentUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
