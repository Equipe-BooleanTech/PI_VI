package edu.fatec.petwise.features.labs.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.LabApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.labs.domain.models.Lab

class RemoteLabDataSourceImpl(
    private val labApiService: LabApiService
) : RemoteLabDataSource {

    override suspend fun getAllLabs(): List<Lab> {
        return when (val result = labApiService.getAllLabs()) {
            is NetworkResult.Success -> result.data.labs.map { it.toLab() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getLabById(id: String): Lab? {
        return when (val result = labApiService.getLabById(id)) {
            is NetworkResult.Success -> result.data.toLab()
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                null
            }
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun createLab(lab: Lab): Lab {
        val request = CreateLabRequest(
            veterinaryId = lab.veterinaryId,
            labName = lab.labName,
            testType = lab.testType,
            testDate = lab.testDate,
            results = lab.results,
            status = lab.status,
            notes = lab.notes
        )
        return when (val result = labApiService.createLab(request)) {
            is NetworkResult.Success -> result.data.toLab()
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun updateLab(lab: Lab): Lab {
        val request = UpdateLabRequest(
            labName = lab.labName,
            testType = lab.testType,
            testDate = lab.testDate,
            results = lab.results,
            status = lab.status,
            notes = lab.notes
        )
        return when (val result = labApiService.updateLab(lab.id, request)) {
            is NetworkResult.Success -> result.data.toLab()
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun deleteLab(id: String) {
        when (val result = labApiService.deleteLab(id)) {
            is NetworkResult.Success -> Unit
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun searchLabs(query: String): List<Lab> {
        return getAllLabs().filter {
            it.labName.contains(query, ignoreCase = true) ||
            it.testType.contains(query, ignoreCase = true) ||
            it.notes?.contains(query, ignoreCase = true) == true
        }
    }

    override suspend fun getLabsByVeterinaryId(veterinaryId: String): List<Lab> {
        return when (val result = labApiService.getLabsByVeterinaryId(veterinaryId)) {
            is NetworkResult.Success -> result.data.map { it.toLab() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }
}
