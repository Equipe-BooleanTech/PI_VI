package edu.fatec.petwise.features.medications.data.datasource

import edu.fatec.petwise.features.medications.domain.models.Medication

interface MedicationDataSource {
    suspend fun getAllMedications(): List<Medication>
    suspend fun getMedicationById(id: String): Medication?
    suspend fun getMedicationsByPetId(petId: String): List<Medication>
    suspend fun getMedicationsByPrescriptionId(prescriptionId: String): List<Medication>
    suspend fun createMedication(medication: Medication): Medication
    suspend fun updateMedication(medication: Medication): Medication
    suspend fun deleteMedication(id: String)
    suspend fun markAsCompleted(id: String): Medication
    suspend fun pauseMedication(id: String): Medication
    suspend fun resumeMedication(id: String): Medication
    suspend fun searchMedications(query: String): List<Medication>
}