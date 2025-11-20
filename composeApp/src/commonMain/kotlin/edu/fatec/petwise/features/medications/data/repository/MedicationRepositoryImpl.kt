package edu.fatec.petwise.features.medications.data.repository

import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.features.medications.data.datasource.MedicationDataSource
import edu.fatec.petwise.features.medications.domain.models.Medication
import edu.fatec.petwise.features.medications.domain.models.MedicationFilterOptions
import edu.fatec.petwise.features.medications.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MedicationRepositoryImpl(
    private val remoteDataSource: MedicationDataSource
) : MedicationRepository {

    override fun getAllMedications(): Flow<List<Medication>> = flow {
        try {
            println("Repositório: Buscando todos os medicamentos via API")
            val medications = remoteDataSource.getAllMedications()
            println("Repositório: ${medications.size} medicamentos carregados com sucesso da API")
            emit(medications)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar medicamentos da API - ${e.message}")
            throw e
        }
    }

    override fun getMedicationById(id: String): Flow<Medication?> = flow {
        try {
            println("Repositório: Buscando medicamento por ID '$id' via API")
            val medication = remoteDataSource.getMedicationById(id)
            if (medication != null) {
                println("Repositório: Medicamento '${medication.medicationName}' encontrado com sucesso")
            } else {
                println("Repositório: Medicamento com ID '$id' não encontrado")
            }
            emit(medication)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar medicamento por ID '$id' - ${e.message}")
            throw e
        }
    }

    override fun getMedicationsByPetId(petId: String): Flow<List<Medication>> = flow {
        try {
            println("Repositório: Buscando medicamentos do pet '$petId' via API")
            val medications = remoteDataSource.getMedicationsByPetId(petId)
            println("Repositório: ${medications.size} medicamentos encontrados para o pet")
            emit(medications)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar medicamentos do pet '$petId' - ${e.message}")
            throw e
        }
    }

    override fun getMedicationsByPrescriptionId(prescriptionId: String): Flow<List<Medication>> = flow {
        try {
            println("Repositório: Buscando medicamentos para a prescrição '$prescriptionId' via API")
            val medications = remoteDataSource.getMedicationsByPrescriptionId(prescriptionId)
            println("Repositório: ${medications.size} medicamentos encontrados para a prescrição")
            emit(medications)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar medicamentos da prescrição '$prescriptionId' - ${e.message}")
            throw e
        }
    }

    override fun searchMedications(query: String): Flow<List<Medication>> = flow {
        try {
            println("Repositório: Iniciando busca de medicamentos com consulta '$query'")
            val medications = remoteDataSource.searchMedications(query)
            println("Repositório: Busca concluída - ${medications.size} medicamentos encontrados")
            emit(medications)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar medicamentos na API - ${e.message}")
            throw e
        }
    }

    override fun filterMedications(options: MedicationFilterOptions): Flow<List<Medication>> = flow {
        try {
            println("Repositório: Aplicando filtros nos medicamentos via API")
            val medications = remoteDataSource.getAllMedications()
            val filteredMedications = medications.filter { medication ->
                val medicationNameMatch = options.medicationName?.let { 
                    medication.medicationName.contains(it, ignoreCase = true) 
                } ?: true
                val searchMatch = if (options.searchQuery.isNotBlank()) {
                    medication.medicationName.contains(options.searchQuery, ignoreCase = true) ||
                    medication.dosage.contains(options.searchQuery, ignoreCase = true) ||
                    medication.frequency.contains(options.searchQuery, ignoreCase = true) ||
                    medication.sideEffects.contains(options.searchQuery, ignoreCase = true)
                } else true

                medicationNameMatch && searchMatch
            }
            println("Repositório: Filtros aplicados - ${filteredMedications.size} medicamentos encontrados")
            emit(filteredMedications)
        } catch (e: Exception) {
            println("Repositório: Erro ao filtrar medicamentos - ${e.message}")
            throw e
        }
    }

    override fun getActiveMedications(): Flow<List<Medication>> = flow {
        try {
            println("Repositório: Buscando medicamentos ativos via API")
            val allMedications = remoteDataSource.getAllMedications()
            // Filter for active medications (not completed or cancelled)
            val activeMedications = allMedications.filter { medication ->
                // You can add logic here to determine if a medication is active
                // based on dates, status, etc.
                true // For now, return all medications
            }
            println("Repositório: ${activeMedications.size} medicamentos ativos encontrados")
            emit(activeMedications)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar medicamentos ativos - ${e.message}")
            throw e
        }
    }

    override suspend fun addMedication(medication: Medication): Result<Medication> {
        return try {
            println("Repositório: Adicionando novo medicamento '${medication.medicationName}' via API")
            val createdMedication = remoteDataSource.createMedication(medication)
            println("Repositório: Medicamento '${createdMedication.medicationName}' criado com sucesso - ID: ${createdMedication.id}")
            // TODO: Add DataRefreshManager.notifyMedicationsUpdated() when available
            Result.success(createdMedication)
        } catch (e: Exception) {
            println("Repositório: Erro ao criar medicamento '${medication.medicationName}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateMedication(medication: Medication): Result<Medication> {
        return try {
            println("Repositório: Atualizando medicamento '${medication.medicationName}' (ID: ${medication.id}) via API")
            val updatedMedication = remoteDataSource.updateMedication(medication)
            println("Repositório: Medicamento '${updatedMedication.medicationName}' atualizado com sucesso")
            // TODO: Add DataRefreshManager notifications when available
            Result.success(updatedMedication)
        } catch (e: Exception) {
            println("Repositório: Erro ao atualizar medicamento '${medication.medicationName}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteMedication(id: String): Result<Unit> {
        return try {
            println("Repositório: Excluindo medicamento com ID '$id' via API")
            remoteDataSource.deleteMedication(id)
            println("Repositório: Medicamento excluído com sucesso")
            // TODO: Add DataRefreshManager.notifyMedicationsUpdated() when available
            Result.success(Unit)
        } catch (e: Exception) {
            println("Repositório: Erro ao excluir medicamento com ID '$id' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun markAsCompleted(id: String): Result<Medication> {
        return try {
            println("Repositório: Marcando medicamento '$id' como concluído via API")
            val updatedMedication = remoteDataSource.markAsCompleted(id)
            println("Repositório: Medicamento '${updatedMedication.medicationName}' marcado como concluído")
            // TODO: Add DataRefreshManager notifications when available
            Result.success(updatedMedication)
        } catch (e: Exception) {
            println("Repositório: Erro ao marcar medicamento '$id' como concluído - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun pauseMedication(id: String): Result<Medication> {
        return try {
            println("Repositório: Pausando medicamento '$id' via API")
            val updatedMedication = remoteDataSource.pauseMedication(id)
            println("Repositório: Medicamento '${updatedMedication.medicationName}' pausado")
            // TODO: Add DataRefreshManager notifications when available
            Result.success(updatedMedication)
        } catch (e: Exception) {
            println("Repositório: Erro ao pausar medicamento '$id' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun resumeMedication(id: String): Result<Medication> {
        return try {
            println("Repositório: Retomando medicamento '$id' via API")
            val updatedMedication = remoteDataSource.resumeMedication(id)
            println("Repositório: Medicamento '${updatedMedication.medicationName}' retomado")
            // TODO: Add DataRefreshManager notifications when available
            Result.success(updatedMedication)
        } catch (e: Exception) {
            println("Repositório: Erro ao retomar medicamento '$id' - ${e.message}")
            Result.failure(e)
        }
    }
}
