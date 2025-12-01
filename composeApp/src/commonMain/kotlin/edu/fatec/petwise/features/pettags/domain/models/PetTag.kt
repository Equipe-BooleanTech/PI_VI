package edu.fatec.petwise.features.pettags.domain.models

import kotlinx.serialization.Serializable


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


@Serializable
data class TagCheckInResult(
    val petId: String,
    val petName: String,
    val ownerName: String?,
    val species: String?,
    val ownerPhone: String?,
    val message: String
)


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


@Serializable
data class PairingStatus(
    val isPairing: Boolean,
    val petId: String?,
    val message: String
)


enum class TagScanStatus {
    IDLE,           
    SCANNING,       
    PAIRING,        
    TAG_FOUND,      
    TAG_REGISTERED, 
    PET_FOUND,      
    ERROR           
}


data class PetTagFilterOptions(
    val petId: String? = null,
    val isActive: Boolean? = null,
    val searchQuery: String = ""
)
