package edu.fatec.petwise.features.pettags.domain.repository

import edu.fatec.petwise.features.pettags.domain.models.TagCheckInResult
import edu.fatec.petwise.features.pettags.domain.models.TagReadResult

interface PetTagRepository {
    

    suspend fun startPairing(petId: String, readerId: String): Result<String>

    
    suspend fun checkIn(tagUid: String, readerId: String): Result<TagCheckInResult>

    
    suspend fun getPetByTag(tagUid: String): Result<TagCheckInResult>

    
    suspend fun getLastRead(): Result<TagReadResult>
}
