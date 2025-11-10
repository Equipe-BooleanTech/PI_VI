package edu.fatec.petwise.features.vaccinations.data.datasource

import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination

interface RemoteVaccinationDataSource {
    suspend fun getAllVaccinations(): List<Vaccination>
    suspend fun getVaccinationById(id: String): Vaccination?
    suspend fun getVaccinationsByPetId(petId: String): List<Vaccination>
    suspend fun createVaccination(vaccination: Vaccination): Vaccination
    suspend fun updateVaccination(vaccination: Vaccination): Vaccination
    suspend fun deleteVaccination(id: String)
    suspend fun markAsApplied(id: String, observations: String, vaccinationDate: String): Vaccination
    suspend fun scheduleNextDose(id: String, nextDoseDate: String): Vaccination
    suspend fun getUpcomingVaccinations(days: Int): List<Vaccination>
    suspend fun getOverdueVaccinations(): List<Vaccination>
}
