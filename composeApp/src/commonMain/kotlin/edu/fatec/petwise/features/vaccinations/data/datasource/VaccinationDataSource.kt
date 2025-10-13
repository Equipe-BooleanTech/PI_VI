package edu.fatec.petwise.features.vaccinations.data.datasource

import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import kotlinx.coroutines.flow.Flow

interface LocalVaccinationDataSource {
    fun getAllVaccinations(): Flow<List<Vaccination>>
    fun getVaccinationById(id: String): Flow<Vaccination?>
    fun getVaccinationsByPetId(petId: String): Flow<List<Vaccination>>
    suspend fun insertVaccination(vaccination: Vaccination)
    suspend fun updateVaccination(vaccination: Vaccination)
    suspend fun deleteVaccination(id: String)
    suspend fun deleteAllVaccinations()
}

interface RemoteVaccinationDataSource {
    suspend fun getAllVaccinations(): List<Vaccination>
    suspend fun getVaccinationById(id: String): Vaccination?
    suspend fun getVaccinationsByPetId(petId: String): List<Vaccination>
    suspend fun createVaccination(vaccination: Vaccination): Vaccination
    suspend fun updateVaccination(vaccination: Vaccination): Vaccination
    suspend fun deleteVaccination(id: String)
    suspend fun markAsApplied(id: String, observations: String, sideEffects: String, applicationDate: String): Vaccination
    suspend fun scheduleNextDose(id: String, nextDoseDate: String): Vaccination
    suspend fun getUpcomingVaccinations(days: Int): List<Vaccination>
    suspend fun getOverdueVaccinations(): List<Vaccination>
}
