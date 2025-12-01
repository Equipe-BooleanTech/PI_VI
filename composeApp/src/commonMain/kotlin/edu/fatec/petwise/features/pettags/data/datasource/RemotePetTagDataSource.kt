package edu.fatec.petwise.features.pettags.data.datasource

import edu.fatec.petwise.features.pettags.domain.models.TagCheckInResult
import edu.fatec.petwise.features.pettags.domain.models.TagReadResult

interface RemotePetTagDataSource {
    suspend fun startPairing(petId: String, readerId: String): String
    suspend fun checkIn(tagUid: String, readerId: String): TagCheckInResult
    suspend fun getPetByTag(tagUid: String): TagCheckInResult
    suspend fun getLastRead(): TagReadResult
}
