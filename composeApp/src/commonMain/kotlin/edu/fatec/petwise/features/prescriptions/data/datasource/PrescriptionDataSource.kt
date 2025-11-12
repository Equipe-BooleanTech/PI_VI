package edu.fatec.petwise.features.prescriptions.data.datasource

import edu.fatec.petwise.features.prescriptions.domain.models.Prescription

interface RemotePrescriptionDataSource {
    suspend fun getAllPrescriptions(): List<Prescription>
    suspend fun getPrescriptionById(id: String): Prescription?
    suspend fun createPrescription(prescription: Prescription): Prescription
    suspend fun updatePrescription(prescription: Prescription): Prescription
    suspend fun deletePrescription(id: String)
    suspend fun searchPrescriptions(query: String): List<Prescription>
    suspend fun getPrescriptionsByPetId(petId: String): List<Prescription>
    suspend fun getPrescriptionsByVeterinaryId(veterinaryId: String): List<Prescription>
}
