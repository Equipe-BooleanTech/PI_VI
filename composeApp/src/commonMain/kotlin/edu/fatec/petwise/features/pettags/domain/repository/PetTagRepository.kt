package edu.fatec.petwise.features.pettags.domain.repository

import edu.fatec.petwise.features.pettags.domain.models.TagCheckInResult
import edu.fatec.petwise.features.pettags.domain.models.TagReadResult

interface PetTagRepository {
    /**
     * Start pairing mode for a pet, allowing the next scanned tag to be associated with it
     */
    suspend fun startPairing(petId: String, readerId: String): Result<String>

    /**
     * Process a check-in when a tag is scanned
     */
    suspend fun checkIn(tagUid: String, readerId: String): Result<TagCheckInResult>

    /**
     * Get pet information by tag UID
     */
    suspend fun getPetByTag(tagUid: String): Result<TagCheckInResult>

    /**
     * Get the last tag read from the reader
     */
    suspend fun getLastRead(): Result<TagReadResult>
}
