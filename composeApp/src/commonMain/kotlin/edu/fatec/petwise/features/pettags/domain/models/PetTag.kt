package edu.fatec.petwise.features.pettags.domain.models

import kotlinx.serialization.Serializable

/**
 * Domain model representing a pet identification tag (NFC/RFID)
 */
@Serializable
data class PetTag(
    val id: String,
    val uid: String,
    val petId: String,
    val readerId: String? = null,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String? = null
)

/**
 * Result of a tag check-in operation
 */
@Serializable
data class TagCheckInResult(
    val petId: String,
    val petName: String,
    val ownerName: String?,
    val species: String?,
    val ownerPhone: String?,
    val message: String
)

/**
 * Result of a tag read operation
 */
@Serializable
data class TagReadResult(
    val tagUid: String?,
    val readerId: String?,
    val timestamp: String?,
    val petId: String?,
    val petName: String?,
    val ownerName: String?,
    val species: String?,
    val message: String
)

/**
 * Status of pairing mode
 */
@Serializable
data class PairingStatus(
    val isPairing: Boolean,
    val petId: String?,
    val message: String
)

/**
 * Status of a tag scanning operation
 */
enum class TagScanStatus {
    IDLE,           // Not scanning
    SCANNING,       // Actively scanning for tags
    PAIRING,        // In pairing mode waiting for tag
    TAG_FOUND,      // Tag detected
    TAG_REGISTERED, // Tag successfully registered/paired
    PET_FOUND,      // Pet info retrieved from tag
    ERROR           // Error occurred
}

/**
 * Filter options for pet tags
 */
data class PetTagFilterOptions(
    val petId: String? = null,
    val isActive: Boolean? = null,
    val searchQuery: String = ""
)
