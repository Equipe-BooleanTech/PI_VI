package edu.fatec.petwise.features.prescriptions.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.PrescriptionApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription

class RemotePrescriptionDataSourceImpl(
    private val prescriptionApiService: PrescriptionApiService
) : RemotePrescriptionDataSource {

    override suspend fun getAllPrescriptions(): List<Prescription> {
        return when (val result = prescriptionApiService.getAllPrescriptions(1, 1000)) {
            is NetworkResult.Success -> result.data.map { it.toPrescription() }
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
