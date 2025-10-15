package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*

interface ConsultaApiService {
    suspend fun getAllConsultas(page: Int = 1, pageSize: Int = 20): NetworkResult<ConsultaListResponse>
    suspend fun getConsultaById(id: String): NetworkResult<ConsultaDto>
    suspend fun createConsulta(request: CreateConsultaRequest): NetworkResult<ConsultaDto>
    suspend fun updateConsulta(id: String, request: UpdateConsultaRequest): NetworkResult<ConsultaDto>
    suspend fun deleteConsulta(id: String): NetworkResult<ConsultaDto>
    suspend fun updateConsultaStatus(id: String, request: UpdateConsultaStatusRequest): NetworkResult<ConsultaDto>
    suspend fun cancelConsulta(id: String, request: CancelConsultaRequest): NetworkResult<CancelConsultaResponse>
    suspend fun searchConsultas(query: String, page: Int = 1, pageSize: Int = 20): NetworkResult<ConsultaListResponse>
    suspend fun getUpcomingConsultas(page: Int = 1, pageSize: Int = 20): NetworkResult<ConsultaListResponse>
    suspend fun getConsultasByPet(petId: String, page: Int = 1, pageSize: Int = 20): NetworkResult<ConsultaListResponse>
}

class ConsultaApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : ConsultaApiService {

    override suspend fun getAllConsultas(page: Int, pageSize: Int): NetworkResult<ConsultaListResponse> {
        return when (val result = networkHandler.get<List<ConsultaDto>>(ApiEndpoints.CONSULTAS) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }) {
            is NetworkResult.Success -> NetworkResult.Success(
                ConsultaListResponse(
                    consultas = result.data,
                    total = result.data.size,
                    page = page,
                    pageSize = pageSize
                )
            )
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
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

    override suspend fun deleteConsulta(id: String): NetworkResult<ConsultaDto> {
        return networkHandler.delete<ConsultaDto>(ApiEndpoints.getConsulta(id))
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

    override suspend fun searchConsultas(query: String, page: Int, pageSize: Int): NetworkResult<ConsultaListResponse> {
        return networkHandler.get(ApiEndpoints.CONSULTAS_SEARCH) {
            parameter("q", query)
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getUpcomingConsultas(page: Int, pageSize: Int): NetworkResult<ConsultaListResponse> {
        return networkHandler.get(ApiEndpoints.CONSULTAS_UPCOMING) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getConsultasByPet(petId: String, page: Int, pageSize: Int): NetworkResult<ConsultaListResponse> {
        return networkHandler.get(ApiEndpoints.CONSULTAS) {
            parameter("petId", petId)
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }
}
