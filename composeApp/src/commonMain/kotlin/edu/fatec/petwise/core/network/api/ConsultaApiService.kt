package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json

interface ConsultaApiService {
    suspend fun getAllConsultas(page: Int = 1, pageSize: Int = 20): NetworkResult<List<ConsultaDto>>
    suspend fun getConsultaById(id: String): NetworkResult<ConsultaDto>
    suspend fun createConsulta(request: CreateConsultaRequest): NetworkResult<ConsultaDto>
    suspend fun updateConsulta(id: String, request: UpdateConsultaRequest): NetworkResult<ConsultaDto>
    suspend fun deleteConsulta(id: String): NetworkResult<MessageResponse>
    suspend fun updateConsultaStatus(id: String, request: UpdateConsultaStatusRequest): NetworkResult<ConsultaDto>
    suspend fun cancelConsulta(id: String, request: CancelConsultaRequest): NetworkResult<CancelConsultaResponse>
    suspend fun searchConsultas(query: String, page: Int = 1, pageSize: Int = 20): NetworkResult<List<ConsultaDto>>
    suspend fun getUpcomingConsultas(page: Int = 1, pageSize: Int = 20): NetworkResult<List<ConsultaDto>>
    suspend fun getConsultasByPet(petId: String, page: Int = 1, pageSize: Int = 20): NetworkResult<List<ConsultaDto>>
}

class ConsultaApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : ConsultaApiService {

    override suspend fun getAllConsultas(page: Int, pageSize: Int): NetworkResult<List<ConsultaDto>> {
        return networkHandler.getWithCustomDeserializer(ApiEndpoints.CONSULTAS, deserializer = { jsonString ->
            val json = Json { ignoreUnknownKeys = true }
            try {
                // Try to parse as direct array first
                json.decodeFromString<List<ConsultaDto>>(jsonString)
            } catch (e: Exception) {
                // Fallback to wrapped object
                val wrapped = json.decodeFromString<ConsultaListResponse>(jsonString)
                wrapped.consultas ?: emptyList()
            }
        }) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getConsultaById(id: String): NetworkResult<ConsultaDto> {
        return networkHandler.get(ApiEndpoints.getConsulta(id))
    }

    override suspend fun createConsulta(request: CreateConsultaRequest): NetworkResult<ConsultaDto> {
        return networkHandler.post(
            urlString = ApiEndpoints.CONSULTAS,
            body = request
        )
    }

    override suspend fun updateConsulta(id: String, request: UpdateConsultaRequest): NetworkResult<ConsultaDto> {
        return networkHandler.put(
            urlString = ApiEndpoints.getConsulta(id),
            body = request
        )
    }

    override suspend fun deleteConsulta(id: String): NetworkResult<MessageResponse> {
        return networkHandler.delete<MessageResponse>(ApiEndpoints.getConsulta(id))
    }

    override suspend fun updateConsultaStatus(
        id: String,
        request: UpdateConsultaStatusRequest
    ): NetworkResult<ConsultaDto> {
        return networkHandler.patch(
            urlString = ApiEndpoints.updateStatus(id),
            body = request
        )
    }

    override suspend fun cancelConsulta(
        id: String,
        request: CancelConsultaRequest
    ): NetworkResult<CancelConsultaResponse> {
        return networkHandler.post(
            urlString = ApiEndpoints.cancelConsulta(id),
            body = request
        )
    }

    override suspend fun searchConsultas(query: String, page: Int, pageSize: Int): NetworkResult<List<ConsultaDto>> {
        return networkHandler.getWithCustomDeserializer(ApiEndpoints.CONSULTAS_SEARCH, deserializer = { jsonString ->
            val json = Json { ignoreUnknownKeys = true }
            try {
                // Try to parse as direct array first
                json.decodeFromString<List<ConsultaDto>>(jsonString)
            } catch (e: Exception) {
                // Fallback to wrapped object
                val wrapped = json.decodeFromString<ConsultaListResponse>(jsonString)
                wrapped.consultas ?: emptyList()
            }
        }) {
            parameter("q", query)
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getUpcomingConsultas(page: Int, pageSize: Int): NetworkResult<List<ConsultaDto>> {
        return networkHandler.getWithCustomDeserializer(ApiEndpoints.CONSULTAS, deserializer = { jsonString ->
            val json = Json { ignoreUnknownKeys = true }
            try {
                // Try to parse as direct array first
                json.decodeFromString<List<ConsultaDto>>(jsonString)
            } catch (e: Exception) {
                // Fallback to wrapped object
                val wrapped = json.decodeFromString<ConsultaListResponse>(jsonString)
                wrapped.consultas ?: emptyList()
            }
        }) {
            parameter("status", "SCHEDULED")
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getConsultasByPet(petId: String, page: Int, pageSize: Int): NetworkResult<List<ConsultaDto>> {
        return networkHandler.getWithCustomDeserializer(ApiEndpoints.getConsultasByPet(petId), deserializer = { jsonString ->
            val json = Json { ignoreUnknownKeys = true }
            try {
                // Try to parse as direct array first
                json.decodeFromString<List<ConsultaDto>>(jsonString)
            } catch (e: Exception) {
                // Fallback to wrapped object
                val wrapped = json.decodeFromString<ConsultaListResponse>(jsonString)
                wrapped.consultas ?: emptyList()
            }
        }) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }
}
