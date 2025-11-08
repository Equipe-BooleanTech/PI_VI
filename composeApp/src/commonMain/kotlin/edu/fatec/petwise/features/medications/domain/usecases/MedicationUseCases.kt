package edu.fatec.petwise.features.medications.domain.usecases

import edu.fatec.petwise.features.medications.domain.models.Medication
import edu.fatec.petwise.features.medications.domain.models.MedicationFilterOptions
import edu.fatec.petwise.features.medications.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow

class GetMedicationsUseCase(
    private val repository: MedicationRepository
) {
    operator fun invoke(): Flow<List<Medication>> = repository.getAllMedications()

    fun filterMedications(options: MedicationFilterOptions): Flow<List<Medication>> = 
        repository.filterMedications(options)

    fun searchMedications(query: String): Flow<List<Medication>> = 
        repository.searchMedications(query)

    fun getActiveMedications(): Flow<List<Medication>> = 
        repository.getActiveMedications()

    fun getMedicationsByPetId(petId: String): Flow<List<Medication>> = 
        repository.getMedicationsByPetId(petId)

    fun getMedicationsByVeterinarianId(veterinarianId: String): Flow<List<Medication>> = 
        repository.getMedicationsByVeterinarianId(veterinarianId)

    fun getMedicationsByPrescriptionId(prescriptionId: String): Flow<List<Medication>> = 
        repository.getMedicationsByPrescriptionId(prescriptionId)
}

class GetMedicationByIdUseCase(
    private val repository: MedicationRepository
) {
    operator fun invoke(id: String): Flow<Medication?> = repository.getMedicationById(id)
}

class AddMedicationUseCase(
    private val repository: MedicationRepository
) {
    suspend operator fun invoke(medication: Medication): Result<Medication> {
        return if (validateMedication(medication)) {
            repository.addMedication(medication)
        } else {
            Result.failure(IllegalArgumentException("Medication data is invalid"))
        }
    }

    private fun validateMedication(medication: Medication): Boolean {
        return medication.medicationName.isNotBlank() &&
               medication.dosage.isNotBlank() &&
               medication.frequency.isNotBlank() &&
               medication.durationDays > 0 &&
               medication.startDate.isNotBlank() &&
               medication.endDate.isNotBlank() &&
               medication.petId.isNotBlank() &&
               medication.veterinarianId.isNotBlank()
    }
}

class UpdateMedicationUseCase(
    private val repository: MedicationRepository
) {
    suspend operator fun invoke(medication: Medication): Result<Medication> = 
        repository.updateMedication(medication)
}

class DeleteMedicationUseCase(
    private val repository: MedicationRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteMedication(id)
}

class MarkMedicationAsCompletedUseCase(
    private val repository: MedicationRepository
) {
    suspend operator fun invoke(id: String): Result<Medication> = 
        repository.markAsCompleted(id)
}

class PauseMedicationUseCase(
    private val repository: MedicationRepository
) {
    suspend operator fun invoke(id: String): Result<Medication> = 
        repository.pauseMedication(id)
}

class ResumeMedicationUseCase(
    private val repository: MedicationRepository
) {
    suspend operator fun invoke(id: String): Result<Medication> = 
        repository.resumeMedication(id)
}

data class MedicationUseCases(
    val getMedications: GetMedicationsUseCase,
    val getMedicationById: GetMedicationByIdUseCase,
    val addMedication: AddMedicationUseCase,
    val updateMedication: UpdateMedicationUseCase,
    val deleteMedication: DeleteMedicationUseCase,
    val markAsCompleted: MarkMedicationAsCompletedUseCase,
    val pauseMedication: PauseMedicationUseCase,
    val resumeMedication: ResumeMedicationUseCase
)