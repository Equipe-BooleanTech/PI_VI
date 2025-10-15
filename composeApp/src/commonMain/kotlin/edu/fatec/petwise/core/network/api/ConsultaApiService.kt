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
    suspend fun deleteConsulta(id: String): NetworkResult<Unit>
    suspend fun updateConsultaStatus(id: String, request: UpdateConsultaStatusRequest): NetworkResult<ConsultaDto>
    suspend fun cancelConsulta(id: String, request: CancelConsultaRequest): NetworkResult<CancelConsultaResponse>
    suspend fun searchConsultas(query: String, page: Int = 1, pageSize: Int = 20): NetworkResult<ConsultaListResponse>
    suspend fun getUpcomingConsultas(page: Int = 1, pageSize: Int = 20): NetworkResult<ConsultaListResponse>
    suspend fun getConsultasByPet(petId: String, page: Int = 1, pageSize: Int = 20): NetworkResult<ConsultaListResponse>
}


class ConsultaApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : ConsultaApiService {

    private suspend fun getConsultasList(
        endpoint: String,
        page: Int,
        pageSize: Int,
        additionalParams: HttpRequestBuilder.() -> Unit = {}
    ): NetworkResult<ConsultaListResponse> {
        return networkHandler.executeWithRetry {
            val result = networkHandler.get<ConsultaListResponse>(endpoint) {
                parameter("page", page)
                parameter("pageSize", pageSize)
                additionalParams()
            }
            
            when (result) {
                is NetworkResult.Error -> {
                    val errorMessage = result.exception.message ?: ""
                    if (errorMessage.contains("Expected start of the object '{', but had '['")) {
                        NetworkResult.Success(
                            ConsultaListResponse(
                                consultas = emptyList(),
                                total = 0,
                                page = page,
                                pageSize = pageSize
                            )
                        )
                    } else {
                        result
                    }
                }
                else -> result
            }
        }
    }

    override suspend fun getAllConsultas(page: Int, pageSize: Int): NetworkResult<ConsultaListResponse> {
        return getConsultasList(ApiEndpoints.CONSULTAS, page, pageSize)
    }

    override suspend fun getConsultaById(id: String): NetworkResult<ConsultaDto> {
        return networkHandler.executeWithRetry {
            networkHandler.get<ConsultaDto>(ApiEndpoints.getConsulta(id))
        }
    }

    override suspend fun createConsulta(request: CreateConsultaRequest): NetworkResult<ConsultaDto> {
        return networkHandler.executeWithRetry(maxAttempts = 1) {
            networkHandler.post<ConsultaDto, CreateConsultaRequest>(
                urlString = ApiEndpoints.CONSULTAS,
                body = request
            )
        }
    }

    override suspend fun updateConsulta(id: String, request: UpdateConsultaRequest): NetworkResult<ConsultaDto> {
        return networkHandler.executeWithRetry(maxAttempts = 1) {
            networkHandler.put<ConsultaDto, UpdateConsultaRequest>(
                urlString = ApiEndpoints.getConsulta(id),
                body = request
            )
        }
    }

    override suspend fun deleteConsulta(id: String): NetworkResult<Unit> {
        return networkHandler.executeWithRetry(maxAttempts = 1) {
            networkHandler.delete<Unit>(ApiEndpoints.getConsulta(id))
        }
    }

    override suspend fun updateConsultaStatus(
        id: String,
        request: UpdateConsultaStatusRequest
    ): NetworkResult<ConsultaDto> {
        return networkHandler.executeWithRetry(maxAttempts = 1) {
            networkHandler.patch<ConsultaDto, UpdateConsultaStatusRequest>(
                urlString = ApiEndpoints.updateStatus(id),
                body = request
            )
        }
    }

    override suspend fun cancelConsulta(
        id: String,
        request: CancelConsultaRequest
    ): NetworkResult<CancelConsultaResponse> {
        return networkHandler.executeWithRetry(maxAttempts = 1) {
            networkHandler.post<CancelConsultaResponse, CancelConsultaRequest>(
                urlString = ApiEndpoints.cancelConsulta(id),
                body = request
            )
        }
    }

    override suspend fun searchConsultas(query: String, page: Int, pageSize: Int): NetworkResult<ConsultaListResponse> {
        return getConsultasList(ApiEndpoints.CONSULTAS_SEARCH, page, pageSize) {
            parameter("q", query)
        }
    }

    override suspend fun getUpcomingConsultas(page: Int, pageSize: Int): NetworkResult<ConsultaListResponse> {
        return getConsultasList(ApiEndpoints.CONSULTAS_UPCOMING, page, pageSize)
    }

    override suspend fun getConsultasByPet(petId: String, page: Int, pageSize: Int): NetworkResult<ConsultaListResponse> {
        return getConsultasList(ApiEndpoints.CONSULTAS, page, pageSize) {
            parameter("petId", petId)
        }
    }
}
