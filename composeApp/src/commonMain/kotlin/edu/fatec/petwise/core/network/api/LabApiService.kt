package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*

interface LabApiService {
    suspend fun getAllLabs(page: Int = 1, pageSize: Int = 20): NetworkResult<LabListResponse>
    suspend fun getLabById(id: String): NetworkResult<LabDto>
    suspend fun createLab(request: CreateLabRequest): NetworkResult<LabDto>
    suspend fun updateLab(id: String, request: UpdateLabRequest): NetworkResult<LabDto>
    suspend fun deleteLab(id: String): NetworkResult<Unit>
}

class LabApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : LabApiService {

    override suspend fun getAllLabs(page: Int, pageSize: Int): NetworkResult<LabListResponse> {
        return networkHandler.get<LabListResponse>(ApiEndpoints.LABS) {
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
