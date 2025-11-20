package edu.fatec.petwise.features.labs.domain.repository

import edu.fatec.petwise.features.labs.domain.models.LabResult
import kotlinx.coroutines.flow.Flow

interface LabRepository {
    fun getAllLabResults(): Flow<List<LabResult>>
    fun getLabResultById(id: String): Flow<LabResult?>
    fun searchLabResults(query: String): Flow<List<LabResult>>
    suspend fun addLabResult(labResult: LabResult): Result<LabResult>
    suspend fun updateLabResult(labResult: LabResult): Result<LabResult>
    suspend fun deleteLabResult(id: String): Result<Unit>
}
