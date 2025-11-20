package edu.fatec.petwise.features.labs.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.LabApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.labs.domain.models.LabResult

class RemoteLabDataSourceImpl(
    private val labApiService: LabApiService
) : RemoteLabDataSource {

    override suspend fun getAllLabResults(): List<LabResult> {
        return when (val result = labApiService.getAllLabResults(1, 1000)) {
            is NetworkResult.Success -> result.data.map { it.toLabResult() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getLabResultById(id: String): LabResult? {
        return when (val result = labApiService.getLabResultById(id)) {
            is NetworkResult.Success -> result.data.toLabResult()
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                null
            }
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun createLabResult(labResult: LabResult): LabResult {
        val request = CreateLabResultRequest(
            labType = labResult.labType,
            labDate = labResult.labDate,
            results = labResult.results,
            status = labResult.status,
            notes = labResult.notes,
            attachmentUrl = labResult.attachmentUrl
        )
        return when (val result = labApiService.createLabResult(labResult.petId, request)) {
            is NetworkResult.Success -> result.data.toLabResult()
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun updateLabResult(labResult: LabResult): LabResult {
        val request = UpdateLabResultRequest(
            labType = labResult.labType,
            labDate = labResult.labDate,
            results = labResult.results,
            status = labResult.status,
            notes = labResult.notes,
            attachmentUrl = labResult.attachmentUrl
        )
        return when (val result = labApiService.updateLabResult(labResult.id, request)) {
            is NetworkResult.Success -> result.data.toLabResult()
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun deleteLabResult(id: String) {
        when (val result = labApiService.deleteLabResult(id)) {
            is NetworkResult.Success -> Unit
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun searchLabResults(query: String): List<LabResult> {
        return getAllLabResults().filter {
            it.labType.contains(query, ignoreCase = true) ||
            it.results?.contains(query, ignoreCase = true) == true ||
            it.notes?.contains(query, ignoreCase = true) == true
        }
    }
}
