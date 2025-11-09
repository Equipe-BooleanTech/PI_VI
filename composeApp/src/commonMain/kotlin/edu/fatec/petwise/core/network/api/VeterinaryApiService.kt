package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*

interface VeterinaryApiService {
    suspend fun getAllVeterinaries(): NetworkResult<VeterinaryListResponse>
    suspend fun getVeterinaryById(id: String): NetworkResult<UserProfileDto>
    suspend fun searchVeterinaries(query: String): NetworkResult<VeterinaryListResponse>
}

class VeterinaryApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : VeterinaryApiService {

    override suspend fun getAllVeterinaries(): NetworkResult<VeterinaryListResponse> {
        return networkHandler.get<VeterinaryListResponse>(ApiEndpoints.VETERINARIES)
    }

    override suspend fun getVeterinaryById(id: String): NetworkResult<UserProfileDto> {
        return networkHandler.get<UserProfileDto>(ApiEndpoints.getVeterinary(id))
    }

    override suspend fun searchVeterinaries(query: String): NetworkResult<VeterinaryListResponse> {
        return networkHandler.get<VeterinaryListResponse>(ApiEndpoints.VETERINARIES_SEARCH) {
            parameter("query", query)
        }
    }
}