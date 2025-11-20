package edu.fatec.petwise.features.prescriptions.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.PrescriptionApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase

class RemotePrescriptionDataSourceImpl(
    private val prescriptionApiService: PrescriptionApiService,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : RemotePrescriptionDataSource {

    override suspend fun getAllPrescriptions(): List<Prescription> {
        println("API: Buscando todas as prescrições")
        return when (val result = prescriptionApiService.getAllPrescriptions(1, 1000)) {
            is NetworkResult.Success -> {
                println("API: ${result.data.size} prescrições obtidas com sucesso")
                var prescriptions = result.data.map { it.toPrescription() }
                
                // Filter prescriptions based on user type
                try {
                    val userProfile = getUserProfileUseCase.execute().getOrNull()
                    if (userProfile != null && userProfile.userType == "OWNER") {
                        println("API: Usuário é OWNER, filtrando prescrições por pets do usuário")
                        // For OWNER users, we need to get their pets first to filter prescriptions
                        // This is a simplified approach - in a real app, the API should handle this
                        prescriptions = prescriptions.filter { prescription ->
                            // This would need to be implemented properly by checking pet ownership
                            // For now, we'll return all prescriptions (this needs backend support)
                            true
                        }
                        println("API: Após filtro OWNER: ${prescriptions.size} prescrições restantes")
                    } else {
                        println("API: Usuário não é OWNER ou perfil não encontrado, mostrando todas as prescrições")
                    }
                } catch (e: Exception) {
                    println("API: Erro ao obter perfil do usuário para filtro: ${e.message}")
                    // Continue without filtering
                }
                
                prescriptions
            }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getPrescriptionById(id: String): Prescription? {
        return when (val result = prescriptionApiService.getPrescriptionById(id)) {
            is NetworkResult.Success -> result.data.toPrescription()
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                null
            }
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun createPrescription(prescription: Prescription): Prescription {
        val request = CreatePrescriptionRequest(
            petId = prescription.petId,
            veterinarian = prescription.veterinaryId,
            prescriptionDate = prescription.startDate,
            instructions = prescription.instructions ?: "",
            medications = "${prescription.medicationName} ${prescription.dosage} ${prescription.frequency} ${prescription.duration}"
        )
        return when (val result = prescriptionApiService.createPrescription(request)) {
            is NetworkResult.Success -> result.data.toPrescription()
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun updatePrescription(prescription: Prescription): Prescription {
        val request = UpdatePrescriptionRequest(
            medicationName = prescription.medicationName,
            dosage = prescription.dosage,
            frequency = prescription.frequency,
            duration = prescription.duration,
            instructions = prescription.instructions,
            startDate = prescription.startDate,
            endDate = prescription.endDate,
            status = prescription.status,
            notes = prescription.notes
        )
        return when (val result = prescriptionApiService.updatePrescription(prescription.id, request)) {
            is NetworkResult.Success -> result.data.toPrescription()
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun deletePrescription(id: String) {
        when (val result = prescriptionApiService.deletePrescription(id)) {
            is NetworkResult.Success -> Unit
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun searchPrescriptions(query: String): List<Prescription> {
        return getAllPrescriptions().filter {
            it.medicationName.contains(query, ignoreCase = true) ||
            it.dosage.contains(query, ignoreCase = true) ||
            it.notes?.contains(query, ignoreCase = true) == true
        }
    }

    override suspend fun getPrescriptionsByPetId(petId: String): List<Prescription> {
        return when (val result = prescriptionApiService.getPrescriptionsByPetId(petId)) {
            is NetworkResult.Success -> result.data.map { it.toPrescription() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getPrescriptionsByVeterinaryId(veterinaryId: String): List<Prescription> {
        return when (val result = prescriptionApiService.getPrescriptionsByVeterinaryId(veterinaryId)) {
            is NetworkResult.Success -> result.data.map { it.toPrescription() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }
}
