package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*

interface PetApiService {
    suspend fun getAllPets(page: Int = 1, pageSize: Int = 20): NetworkResult<PetListResponse>
    suspend fun getPetById(id: String): NetworkResult<PetDto>
    suspend fun createPet(request: CreatePetRequest): NetworkResult<PetDto>
    suspend fun updatePet(id: String, request: UpdatePetRequest): NetworkResult<PetDto>
    suspend fun deletePet(id: String): NetworkResult<Unit>
    suspend fun toggleFavorite(id: String): NetworkResult<ToggleFavoriteResponse>
    suspend fun updateHealthStatus(id: String, request: UpdateHealthStatusRequest): NetworkResult<PetDto>
    suspend fun searchPets(query: String, page: Int = 1, pageSize: Int = 20): NetworkResult<PetListResponse>
    suspend fun getFavoritePets(page: Int = 1, pageSize: Int = 20): NetworkResult<PetListResponse>
}

class PetApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : PetApiService {

    override suspend fun getAllPets(page: Int, pageSize: Int): NetworkResult<PetListResponse> {
        return networkHandler.executeWithRetry {
            when (val result = networkHandler.get<List<PetDto>>(ApiEndpoints.PETS) {
                parameter("page", page)
                parameter("pageSize", pageSize)
            }) {
                is NetworkResult.Success -> {
                    NetworkResult.Success(
                        PetListResponse(
                            pets = result.data,
                            total = result.data.size,
                            page = page,
                            pageSize = pageSize
                        )
                    )
                }
                is NetworkResult.Error -> result
                is NetworkResult.Loading -> result
            }
        }
    }

    override suspend fun getPetById(id: String): NetworkResult<PetDto> {
        return networkHandler.executeWithRetry {
            networkHandler.get<PetDto>(ApiEndpoints.getPet(id))
        }
    }

    override suspend fun createPet(request: CreatePetRequest): NetworkResult<PetDto> {
        return networkHandler.executeWithRetry(maxAttempts = 1) {
            networkHandler.post<PetDto, CreatePetRequest>(
                urlString = ApiEndpoints.PETS,
                body = request
            )
        }
    }

    override suspend fun updatePet(id: String, request: UpdatePetRequest): NetworkResult<PetDto> {
        return networkHandler.executeWithRetry(maxAttempts = 1) {
            networkHandler.put<PetDto, UpdatePetRequest>(
                urlString = ApiEndpoints.getPet(id),
                body = request
            )
        }
    }

    override suspend fun deletePet(id: String): NetworkResult<Unit> {
        return networkHandler.executeWithRetry(maxAttempts = 1) {
            networkHandler.delete<Unit>(ApiEndpoints.getPet(id))
        }
    }

    override suspend fun toggleFavorite(id: String): NetworkResult<ToggleFavoriteResponse> {
        return networkHandler.executeWithRetry(maxAttempts = 1) {
            networkHandler.post<ToggleFavoriteResponse, Unit>(
                urlString = ApiEndpoints.toggleFavorite(id),
                body = Unit
            )
        }
    }

    override suspend fun updateHealthStatus(
        id: String,
        request: UpdateHealthStatusRequest
    ): NetworkResult<PetDto> {
        return networkHandler.executeWithRetry(maxAttempts = 1) {
            networkHandler.patch<PetDto, UpdateHealthStatusRequest>(
                urlString = ApiEndpoints.updateHealth(id),
                body = request
            )
        }
    }

    override suspend fun searchPets(query: String, page: Int, pageSize: Int): NetworkResult<PetListResponse> {
        return networkHandler.executeWithRetry {
            when (val result = networkHandler.get<List<PetDto>>(ApiEndpoints.PETS_SEARCH) {
                parameter("q", query)
                parameter("page", page)
                parameter("pageSize", pageSize)
            }) {
                is NetworkResult.Success -> {
                    NetworkResult.Success(
                        PetListResponse(
                            pets = result.data,
                            total = result.data.size,
                            page = page,
                            pageSize = pageSize
                        )
                    )
                }
                is NetworkResult.Error -> result
                is NetworkResult.Loading -> result
            }
        }
    }

    override suspend fun getFavoritePets(page: Int, pageSize: Int): NetworkResult<PetListResponse> {
        return networkHandler.executeWithRetry {
            when (val result = networkHandler.get<List<PetDto>>(ApiEndpoints.PETS_FAVORITES) {
                parameter("page", page)
                parameter("pageSize", pageSize)
            }) {
                is NetworkResult.Success -> {
                    NetworkResult.Success(
                        PetListResponse(
                            pets = result.data,
                            total = result.data.size,
                            page = page,
                            pageSize = pageSize
                        )
                    )
                }
                is NetworkResult.Error -> result
                is NetworkResult.Loading -> result
            }
        }
    }
}
