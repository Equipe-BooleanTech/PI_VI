package edu.fatec.petwise.features.vaccinations.domain.repository

import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.vaccinations.domain.models.VaccinationFilterOptions
import kotlinx.coroutines.flow.Flow

interface VaccinationRepository {
    fun getAllVaccinations(): Flow<List<Vaccination>>
    fun getVaccinationById(id: String): Flow<Vaccination?>
    fun getVaccinationsByPetId(petId: String): Flow<List<Vaccination>>
    fun filterVaccinations(options: VaccinationFilterOptions): Flow<List<Vaccination>>
    fun getUpcomingVaccinations(days: Int = 30): Flow<List<Vaccination>>
    fun getOverdueVaccinations(): Flow<List<Vaccination>>
    suspend fun addVaccination(vaccination: Vaccination): Result<Vaccination>
    suspend fun updateVaccination(vaccination: Vaccination): Result<Vaccination>
    suspend fun deleteVaccination(id: String): Result<Unit>
    suspend fun markAsApplied(id: String, observations: String): Result<Vaccination>
    suspend fun scheduleNextDose(id: String, nextDoseDate: String): Result<Vaccination>
}
