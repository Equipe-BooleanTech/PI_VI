package edu.fatec.petwise.features.labs.data.datasource

import edu.fatec.petwise.features.labs.domain.models.LabResult

interface RemoteLabDataSource {
    suspend fun getAllLabResults(): List<LabResult>
    suspend fun getLabResultById(id: String): LabResult?
    suspend fun createLabResult(labResult: LabResult): LabResult
    suspend fun updateLabResult(labResult: LabResult): LabResult
    suspend fun deleteLabResult(id: String)
    suspend fun searchLabResults(query: String): List<LabResult>
}
