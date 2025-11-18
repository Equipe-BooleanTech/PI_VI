package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json

interface ToyApiService {
    suspend fun getAllToys(page: Int = 1, pageSize: Int = 20): NetworkResult<List<ToyDto>>
    suspend fun getToyById(id: String): NetworkResult<ToyDto>
    suspend fun getToysByCategory(category: String): NetworkResult<List<ToyDto>>
    suspend fun searchToys(query: String): NetworkResult<List<ToyDto>>
    suspend fun createToy(request: CreateToyRequest): NetworkResult<ToyDto>
    suspend fun updateToy(id: String, request: UpdateToyRequest): NetworkResult<ToyDto>
    suspend fun deleteToy(id: String): NetworkResult<Unit>
}

class ToyApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : ToyApiService {

    override suspend fun getAllToys(page: Int, pageSize: Int): NetworkResult<List<ToyDto>> {
        return networkHandler.getWithCustomDeserializer(
            urlString = ApiEndpoints.TOYS,
            deserializer = { jsonString ->
                val json = Json { ignoreUnknownKeys = true }
                try {
                    // Try to parse as direct array first
                    json.decodeFromString<List<ToyDto>>(jsonString)
                } catch (e: Exception) {
                    // Fallback to wrapped object
                    val wrapped = json.decodeFromString<ToyListResponse>(jsonString)
                    wrapped.toys ?: emptyList()
                }
            }
        ) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getToyById(id: String): NetworkResult<ToyDto> {
        return networkHandler.get<ToyDto>(ApiEndpoints.getToy(id))
    }

    override suspend fun getToysByCategory(category: String): NetworkResult<List<ToyDto>> {
        return networkHandler.get<List<ToyDto>>(ApiEndpoints.TOYS_BY_CATEGORY) {
            parameter("category", category)
        }
    }

    override suspend fun searchToys(query: String): NetworkResult<List<ToyDto>> {
        return networkHandler.get<List<ToyDto>>(ApiEndpoints.TOYS_SEARCH) {
            parameter("q", query)
        }
    }

    override suspend fun createToy(request: CreateToyRequest): NetworkResult<ToyDto> {
        return networkHandler.post<ToyDto, CreateToyRequest>(
            urlString = ApiEndpoints.TOYS,
            body = request
        )
    }

    override suspend fun updateToy(id: String, request: UpdateToyRequest): NetworkResult<ToyDto> {
        return networkHandler.put<ToyDto, UpdateToyRequest>(
            urlString = ApiEndpoints.getToy(id),
            body = request
        )
    }

    override suspend fun deleteToy(id: String): NetworkResult<Unit> {
        return networkHandler.delete<Unit>(ApiEndpoints.getToy(id))
    }
}
