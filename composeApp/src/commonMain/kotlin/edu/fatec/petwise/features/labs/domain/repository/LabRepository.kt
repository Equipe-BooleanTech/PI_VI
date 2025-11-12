package edu.fatec.petwise.features.labs.domain.repository

import edu.fatec.petwise.features.labs.domain.models.Lab
import kotlinx.coroutines.flow.Flow

interface LabRepository {
    fun getAllLabs(): Flow<List<Lab>>
    fun getLabById(id: String): Flow<Lab?>
    fun searchLabs(query: String): Flow<List<Lab>>
    fun getLabsByVeterinaryId(veterinaryId: String): Flow<List<Lab>>
    suspend fun addLab(lab: Lab): Result<Lab>
    suspend fun updateLab(lab: Lab): Result<Lab>
    suspend fun deleteLab(id: String): Result<Unit>
}
