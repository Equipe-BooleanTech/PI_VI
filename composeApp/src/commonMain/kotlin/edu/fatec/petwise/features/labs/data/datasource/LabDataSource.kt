package edu.fatec.petwise.features.labs.data.datasource

import edu.fatec.petwise.features.labs.domain.models.Lab

interface RemoteLabDataSource {
    suspend fun getAllLabs(): List<Lab>
    suspend fun getLabById(id: String): Lab?
    suspend fun createLab(lab: Lab): Lab
    suspend fun updateLab(lab: Lab): Lab
    suspend fun deleteLab(id: String)
    suspend fun searchLabs(query: String): List<Lab>
    suspend fun getLabsByVeterinaryId(veterinaryId: String): List<Lab>
}
