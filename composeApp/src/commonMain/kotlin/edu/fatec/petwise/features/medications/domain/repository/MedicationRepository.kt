package edu.fatec.petwise.features.medications.domain.repository

import edu.fatec.petwise.features.medications.domain.models.Medication
import edu.fatec.petwise.features.medications.domain.models.MedicationFilterOptions
import kotlinx.coroutines.flow.Flow

interface MedicationRepository {
    fun getAllMedications(): Flow<List<Medication>>

    fun getMedicationById(id: String): Flow<Medication?>

    fun getMedicationsByPetId(petId: String): Flow<List<Medication>>

    fun getMedicationsByPrescriptionId(prescriptionId: String): Flow<List<Medication>>

    fun searchMedications(query: String): Flow<List<Medication>>

    fun filterMedications(options: MedicationFilterOptions): Flow<List<Medication>>

    fun getActiveMedications(): Flow<List<Medication>>

    suspend fun addMedication(medication: Medication): Result<Medication>

    suspend fun updateMedication(medication: Medication): Result<Medication>

    suspend fun deleteMedication(id: String): Result<Unit>

    suspend fun markAsCompleted(id: String): Result<Medication>

    suspend fun pauseMedication(id: String): Result<Medication>

    suspend fun resumeMedication(id: String): Result<Medication>
}