package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable
import edu.fatec.petwise.features.labs.domain.models.Lab

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
    val labs: List<LabDto>,
    val total: Int = labs.size,
    val page: Int = 1,
    val pageSize: Int = 20
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

fun LabDto.toLab(): Lab {
    return Lab(
        id = id,
        name = name,
        contactInfo = contactInfo,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
