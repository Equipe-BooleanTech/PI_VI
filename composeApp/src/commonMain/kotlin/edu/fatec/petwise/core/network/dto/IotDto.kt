package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IotCheckInRequest(
    @SerialName("tag_uid")
    val tagUid: String,
    @SerialName("reader_id")
    val readerId: String
)

@Serializable
data class StartPairingRequest(
    val petId: String,
    val readerId: String
)

@Serializable
data class IotCheckInResponse(
    val petId: String,
    val petName: String,
    val ownerName: String? = null,
    val species: String? = null,
    val ownerPhone: String? = null,
    val message: String
)

@Serializable
data class RfidTagReadRequest(
    val tagUid: String,
    val readerId: String,
    val timestamp: String? = null
)

@Serializable
data class RfidTagRegistrationResponse(
    val success: Boolean,
    val tagId: String? = null,
    val tagUid: String,
    val petId: String? = null,
    val message: String,
    val timestamp: String? = null
)

@Serializable
data class PairingStatusResponse(
    val isPairing: Boolean,
    val petId: String? = null,
    val message: String
)

@Serializable
data class PetTagDto(
    val id: String,
    val uid: String,
    val petId: String,
    val readerId: String? = null,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String? = null
)

@Serializable
data class LastTagReadResponse(
    val tagUid: String? = null,
    val readerId: String? = null,
    val timestamp: String? = null,
    val petId: String? = null,
    val petName: String? = null,
    val ownerName: String? = null,
    val species: String? = null,
    val message: String
)