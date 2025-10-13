package edu.fatec.petwise.features.vaccinations.domain.usecases

import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.vaccinations.domain.models.VaccinationFilterOptions
import edu.fatec.petwise.features.vaccinations.domain.repository.VaccinationRepository
import kotlinx.coroutines.flow.Flow

class GetVaccinationsUseCase(private val repository: VaccinationRepository) {
    operator fun invoke(): Flow<List<Vaccination>> = repository.getAllVaccinations()
}

class GetVaccinationByIdUseCase(private val repository: VaccinationRepository) {
    operator fun invoke(id: String): Flow<Vaccination?> = repository.getVaccinationById(id)
}

class GetVaccinationsByPetIdUseCase(private val repository: VaccinationRepository) {
    operator fun invoke(petId: String): Flow<List<Vaccination>> = repository.getVaccinationsByPetId(petId)
}

class FilterVaccinationsUseCase(private val repository: VaccinationRepository) {
    operator fun invoke(options: VaccinationFilterOptions): Flow<List<Vaccination>> = 
        repository.filterVaccinations(options)
}

class GetUpcomingVaccinationsUseCase(private val repository: VaccinationRepository) {
    operator fun invoke(days: Int = 30): Flow<List<Vaccination>> = 
        repository.getUpcomingVaccinations(days)
}

class GetOverdueVaccinationsUseCase(private val repository: VaccinationRepository) {
    operator fun invoke(): Flow<List<Vaccination>> = repository.getOverdueVaccinations()
}

class AddVaccinationUseCase(private val repository: VaccinationRepository) {
    suspend operator fun invoke(vaccination: Vaccination): Result<Vaccination> = 
        repository.addVaccination(vaccination)
}

class UpdateVaccinationUseCase(private val repository: VaccinationRepository) {
    suspend operator fun invoke(vaccination: Vaccination): Result<Vaccination> = 
        repository.updateVaccination(vaccination)
}

class DeleteVaccinationUseCase(private val repository: VaccinationRepository) {
    suspend operator fun invoke(id: String): Result<Unit> = 
        repository.deleteVaccination(id)
}

class MarkVaccinationAsAppliedUseCase(private val repository: VaccinationRepository) {
    suspend operator fun invoke(id: String, observations: String): Result<Vaccination> = 
        repository.markAsApplied(id, observations)
}

class ScheduleNextDoseUseCase(private val repository: VaccinationRepository) {
    suspend operator fun invoke(id: String, nextDoseDate: String): Result<Vaccination> = 
        repository.scheduleNextDose(id, nextDoseDate)
}
