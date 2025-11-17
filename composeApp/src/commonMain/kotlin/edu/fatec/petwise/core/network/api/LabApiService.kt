package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json

interface LabApiService {
    suspend fun getAllLabs(page: Int = 1, pageSize: Int = 20): NetworkResult<List<LabDto>>
    suspend fun getLabById(id: String): NetworkResult<LabDto>
    suspend fun createLab(request: CreateLabRequest): NetworkResult<LabDto>
    suspend fun updateLab(id: String, request: UpdateLabRequest): NetworkResult<LabDto>
    suspend fun deleteLab(id: String): NetworkResult<Unit>
}

class LabApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : LabApiService {

    override suspend fun getAllLabs(page: Int, pageSize: Int): NetworkResult<List<LabDto>> {
        return networkHandler.getWithCustomDeserializer(
            urlString = ApiEndpoints.LABS,
            deserializer = { jsonString ->
                val json = Json { ignoreUnknownKeys = true }
                try {
                    // Try to parse as direct array first
                    json.decodeFromString<List<LabDto>>(jsonString)
                } catch (e: Exception) {
                    // Fallback to wrapped object
                    val wrapped = json.decodeFromString<LabListResponse>(jsonString)
                    wrapped.labs ?: emptyList()
                }
            }
        ) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getLabById(id: String): NetworkResult<LabDto> {
        return networkHandler.get<LabDto>(ApiEndpoints.getLab(id))
    }

    override suspend fun createLab(request: CreateLabRequest): NetworkResult<LabDto> {
        return networkHandler.post<LabDto, CreateLabRequest>(
            urlString = ApiEndpoints.LABS,
            body = request
        )
    }

    override suspend fun updateLab(id: String, request: UpdateLabRequest): NetworkResult<LabDto> {
        return networkHandler.put<LabDto, UpdateLabRequest>(
            urlString = ApiEndpoints.getLab(id),
            body = request
        )
    }

    override suspend fun deleteLab(id: String): NetworkResult<Unit> {
        return networkHandler.delete<Unit>(ApiEndpoints.getLab(id))
    }
}
