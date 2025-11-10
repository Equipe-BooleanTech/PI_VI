package edu.fatec.petwise.features.medications.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.MedicationApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.medications.domain.models.Medication
import edu.fatec.petwise.features.medications.domain.models.MedicationFrequency
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class RemoteMedicationDataSourceImpl(
    private val medicationApiService: MedicationApiService
) : MedicationDataSource {

    override suspend fun getAllMedications(): List<Medication> {
        println("API: Buscando todos os medicamentos")
        return when (val result = medicationApiService.getAllMedications()) {
            is NetworkResult.Success -> {
                println("API: ${result.data.medications.size} medicamentos obtidos com sucesso")
                result.data.medications.map { it.toDomain() }
            }
            is NetworkResult.Error -> {
                println("API: Erro ao buscar medicamentos - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getMedicationById(id: String): Medication? {
        println("API: Buscando medicamento por ID: $id")
        return when (val result = medicationApiService.getMedicationById(id)) {
            is NetworkResult.Success -> {
                println("API: Medicamento encontrado - ${result.data.medicationName}")
                result.data.toDomain()
            }
            is NetworkResult.Error -> {
                println("API: Erro ao buscar medicamento - ${result.exception.message}")
                null
            }
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun getMedicationsByPetId(petId: String): List<Medication> {
        println("API: Buscando medicamentos por Pet ID: $petId")
        return when (val result = medicationApiService.getMedicationsByPetId(petId)) {
            is NetworkResult.Success -> {
                println("API: ${result.data.size} medicamentos encontrados para o pet")
                result.data.map { it.toDomain() }
            }
            is NetworkResult.Error -> {
                println("API: Erro ao buscar medicamentos por pet - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getMedicationsByVeterinarianId(veterinarianId: String): List<Medication> {
        println("API: Buscando medicamentos por Veterinarian ID: $veterinarianId")
        val filter = MedicationFilterRequest(veterinarianId = veterinarianId)
        return when (val result = medicationApiService.searchMedications(filter)) {
            is NetworkResult.Success -> {
                println("API: ${result.data.medications.size} medicamentos encontrados para o veterinário")
                result.data.medications.map { it.toDomain() }
            }
            is NetworkResult.Error -> {
                println("API: Erro ao buscar medicamentos por veterinário - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getMedicationsByPrescriptionId(prescriptionId: String): List<Medication> {
        println("API: Buscando medicamentos por Prescription ID: $prescriptionId")
        return when (val result = medicationApiService.getAllMedications()) {
            is NetworkResult.Success -> {
                val filtered = result.data.medications
                    .filter { it.prescriptionId == prescriptionId }
                    .map { it.toDomain() }
                println("API: ${filtered.size} medicamentos encontrados para a prescrição")
                filtered
            }
            is NetworkResult.Error -> {
                println("API: Erro ao buscar medicamentos por prescrição - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun createMedication(medication: Medication): Medication {
        println("API: Criando novo medicamento - ${medication.medicationName}")
        
        val request = CreateMedicationRequest(
            petId = medication.petId,
            veterinarianId = medication.veterinarianId,
            prescriptionId = medication.prescriptionId,
            medicationName = medication.medicationName,
            dosage = medication.dosage,
            frequency = medication.frequency,
            durationDays = medication.durationDays,
            startDate = medication.startDate,
            endDate = medication.endDate,
            sideEffects = medication.sideEffects
        )

        return when (val result = medicationApiService.createMedication(request)) {
            is NetworkResult.Success -> {
                println("API: Medicamento criado com sucesso - ID: ${result.data.id}")
                result.data.toDomain()
            }
            is NetworkResult.Error -> {
                println("API: Erro ao criar medicamento - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun updateMedication(medication: Medication): Medication {
        println("API: Atualizando medicamento - ${medication.medicationName} (ID: ${medication.id})")
        
        val request = UpdateMedicationRequest(
            dosage = medication.dosage,
            frequency = medication.frequency,
            durationDays = medication.durationDays,
            endDate = medication.endDate,
            sideEffects = medication.sideEffects
        )

        return when (val result = medicationApiService.updateMedication(medication.id, request)) {
            is NetworkResult.Success -> {
                println("API: Medicamento atualizado com sucesso")
                result.data.toDomain()
            }
            is NetworkResult.Error -> {
                println("API: Erro ao atualizar medicamento - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun deleteMedication(id: String) {
        println("API: Excluindo medicamento com ID: $id")
        when (val result = medicationApiService.deleteMedication(id)) {
            is NetworkResult.Success -> {
                println("API: Medicamento excluído com sucesso")
            }
            is NetworkResult.Error -> {
                println("API: Erro ao excluir medicamento - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun markAsCompleted(id: String): Medication {
        println("API: Marcando medicamento como concluído - ID: $id")
        return when (val result = medicationApiService.completeMedication(id)) {
            is NetworkResult.Success -> {
                println("API: Medicamento marcado como concluído")
                result.data.toDomain()
            }
            is NetworkResult.Error -> {
                println("API: Erro ao marcar medicamento como concluído - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun pauseMedication(id: String): Medication {
        println("API: Pausando medicamento - ID: $id")
        return when (val result = medicationApiService.updateMedicationStatus(id, "PAUSED")) {
            is NetworkResult.Success -> {
                println("API: Medicamento pausado")
                result.data.toDomain()
            }
            is NetworkResult.Error -> {
                println("API: Erro ao pausar medicamento - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun resumeMedication(id: String): Medication {
        println("API: Retomando medicamento - ID: $id")
        return when (val result = medicationApiService.updateMedicationStatus(id, "ACTIVE")) {
            is NetworkResult.Success -> {
                println("API: Medicamento retomado")
                result.data.toDomain()
            }
            is NetworkResult.Error -> {
                println("API: Erro ao retomar medicamento - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun searchMedications(query: String): List<Medication> {
        println("API: Buscando medicamentos com query: '$query'")
        val filter = MedicationFilterRequest(searchQuery = query)
        return when (val result = medicationApiService.searchMedications(filter)) {
            is NetworkResult.Success -> {
                println("API: Busca concluída - ${result.data.medications.size} medicamentos encontrados")
                result.data.medications.map { it.toDomain() }
            }
            is NetworkResult.Error -> {
                println("API: Erro na busca de medicamentos - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> emptyList()
        }
    }
}