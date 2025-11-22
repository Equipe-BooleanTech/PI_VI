package edu.fatec.petwise.features.prescriptions.domain.usecases

import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.features.prescriptions.domain.repository.PrescriptionRepository
import kotlinx.coroutines.flow.Flow

class GetPrescriptionsUseCase(
    private val repository: PrescriptionRepository
) {
    operator fun invoke(): Flow<List<Prescription>> = repository.getAllPrescriptions()

    fun searchPrescriptions(query: String): Flow<List<Prescription>> = repository.searchPrescriptions(query)

    fun getPrescriptionsByPetId(petId: String): Flow<List<Prescription>> = repository.getPrescriptionsByPetId(petId)

    fun getPrescriptionsByVeterinaryId(veterinaryId: String): Flow<List<Prescription>> = repository.getPrescriptionsByVeterinaryId(veterinaryId)
}

class GetPrescriptionByIdUseCase(
    private val repository: PrescriptionRepository
) {
    operator fun invoke(id: String): Flow<Prescription?> = repository.getPrescriptionById(id)
}

class AddPrescriptionUseCase(
    private val repository: PrescriptionRepository
) {
    suspend operator fun invoke(prescription: Prescription): Result<Prescription> {
        return if (validatePrescription(prescription)) {
            repository.addPrescription(prescription)
        } else {
            Result.failure(IllegalArgumentException("Prescription data is invalid"))
        }
    }

    private fun validatePrescription(prescription: Prescription): Boolean {
        return prescription.instructions.isNotBlank() &&
               prescription.medications.isNotBlank() &&
               prescription.petId.isNotBlank() &&
               prescription.veterinaryId.isNotBlank()
    }
}

class UpdatePrescriptionUseCase(
    private val repository: PrescriptionRepository
) {
    suspend operator fun invoke(prescription: Prescription): Result<Prescription> = repository.updatePrescription(prescription)
}

class DeletePrescriptionUseCase(
    private val repository: PrescriptionRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deletePrescription(id)
}
