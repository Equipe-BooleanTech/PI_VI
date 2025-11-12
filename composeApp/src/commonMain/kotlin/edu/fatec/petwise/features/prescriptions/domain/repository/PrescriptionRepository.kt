package edu.fatec.petwise.features.prescriptions.domain.repository

import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import kotlinx.coroutines.flow.Flow

interface PrescriptionRepository {
    fun getAllPrescriptions(): Flow<List<Prescription>>
    fun getPrescriptionById(id: String): Flow<Prescription?>
    fun searchPrescriptions(query: String): Flow<List<Prescription>>
    fun getPrescriptionsByPetId(petId: String): Flow<List<Prescription>>
    fun getPrescriptionsByVeterinaryId(veterinaryId: String): Flow<List<Prescription>>
    suspend fun addPrescription(prescription: Prescription): Result<Prescription>
    suspend fun updatePrescription(prescription: Prescription): Result<Prescription>
    suspend fun deletePrescription(id: String): Result<Unit>
}
