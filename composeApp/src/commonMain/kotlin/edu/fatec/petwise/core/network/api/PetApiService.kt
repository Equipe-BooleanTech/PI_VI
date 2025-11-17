package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json

interface PetApiService {
    suspend fun getAllPets(page: Int = 1, pageSize: Int = 20): NetworkResult<List<PetDto>>
    suspend fun getPetById(id: String): NetworkResult<PetDto>
    suspend fun createPet(request: CreatePetRequest): NetworkResult<PetDto>
    suspend fun updatePet(id: String, request: UpdatePetRequest): NetworkResult<PetDto>
    suspend fun deletePet(id: String): NetworkResult<Unit>
    suspend fun toggleFavorite(id: String): NetworkResult<ToggleFavoriteResponse>
    suspend fun updateHealthStatus(id: String, request: UpdateHealthStatusRequest): NetworkResult<PetDto>
    suspend fun searchPets(query: String, page: Int = 1, pageSize: Int = 20): NetworkResult<List<PetDto>>
    suspend fun getFavoritePets(page: Int = 1, pageSize: Int = 20): NetworkResult<List<PetDto>>
}

class PetApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : PetApiService {

    override suspend fun getAllPets(page: Int, pageSize: Int): NetworkResult<List<PetDto>> {
        return networkHandler.getWithCustomDeserializer(ApiEndpoints.PETS, deserializer = { jsonString ->
            val json = Json { ignoreUnknownKeys = true }
            try {
                // Try to parse as direct array first
                json.decodeFromString<List<PetDto>>(jsonString)
            } catch (e: Exception) {
                // Fallback to wrapped object
                val wrapped = json.decodeFromString<PetListResponse>(jsonString)
                wrapped.pets ?: emptyList()
            }
        }) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getPetById(id: String): NetworkResult<PetDto> {
        return networkHandler.get<PetDto>(ApiEndpoints.getPet(id))
    }

    override suspend fun createPet(request: CreatePetRequest): NetworkResult<PetDto> {
        return networkHandler.post<PetDto, CreatePetRequest>(
            urlString = ApiEndpoints.PETS,
            body = request
        )
    }

    override suspend fun updatePet(id: String, request: UpdatePetRequest): NetworkResult<PetDto> {
        return networkHandler.put<PetDto, UpdatePetRequest>(
            urlString = ApiEndpoints.getPet(id),
            body = request
        )
    }

    override suspend fun deletePet(id: String): NetworkResult<Unit> {
        return networkHandler.delete<Unit>(ApiEndpoints.getPet(id))
    }

    override suspend fun toggleFavorite(id: String): NetworkResult<ToggleFavoriteResponse> {
        return networkHandler.post<ToggleFavoriteResponse, Unit>(
            urlString = ApiEndpoints.toggleFavorite(id),
            body = Unit
        )
    }

    override suspend fun updateHealthStatus(
        id: String,
        request: UpdateHealthStatusRequest
    ): NetworkResult<PetDto> {
        return networkHandler.patch<PetDto, UpdateHealthStatusRequest>(
            urlString = ApiEndpoints.updateHealth(id),
            body = request
        )
    }

    override suspend fun searchPets(query: String, page: Int, pageSize: Int): NetworkResult<List<PetDto>> {
        return networkHandler.getWithCustomDeserializer(ApiEndpoints.PETS_SEARCH, deserializer = { jsonString ->
            val json = Json { ignoreUnknownKeys = true }
            try {
                // Try to parse as direct array first
                json.decodeFromString<List<PetDto>>(jsonString)
            } catch (e: Exception) {
                // Fallback to wrapped object
                val wrapped = json.decodeFromString<PetListResponse>(jsonString)
                wrapped.pets ?: emptyList()
            }
        }) {
            parameter("q", query)
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getFavoritePets(page: Int, pageSize: Int): NetworkResult<List<PetDto>> {
        return networkHandler.getWithCustomDeserializer(ApiEndpoints.PETS_FAVORITES, deserializer = { jsonString ->
            val json = Json { ignoreUnknownKeys = true }
            try {
                // Try to parse as direct array first
                json.decodeFromString<List<PetDto>>(jsonString)
            } catch (e: Exception) {
                // Fallback to wrapped object
                val wrapped = json.decodeFromString<PetListResponse>(jsonString)
                wrapped.pets ?: emptyList()
            }
        }) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }
}
