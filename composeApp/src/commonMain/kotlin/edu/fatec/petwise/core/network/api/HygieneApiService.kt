package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json

interface HygieneApiService {
    suspend fun getAllHygieneProducts(page: Int = 1, pageSize: Int = 20): NetworkResult<List<HygieneDto>>
    suspend fun getHygieneProductById(id: String): NetworkResult<HygieneDto>
    suspend fun getHygieneProductsByCategory(category: String): NetworkResult<List<HygieneDto>>
    suspend fun searchHygieneProducts(query: String): NetworkResult<List<HygieneDto>>
    suspend fun createHygieneProduct(request: CreateHygieneRequest): NetworkResult<HygieneDto>
    suspend fun updateHygieneProduct(id: String, request: UpdateHygieneRequest): NetworkResult<HygieneDto>
    suspend fun deleteHygieneProduct(id: String): NetworkResult<Unit>
}

class HygieneApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : HygieneApiService {

    override suspend fun getAllHygieneProducts(page: Int, pageSize: Int): NetworkResult<List<HygieneDto>> {
        return networkHandler.getWithCustomDeserializer(
            urlString = ApiEndpoints.HYGIENE,
            deserializer = { jsonString ->
                val json = Json { ignoreUnknownKeys = true }
                try {
                    
                    json.decodeFromString<List<HygieneDto>>(jsonString)
                } catch (e: Exception) {
                    
                    val wrapped = json.decodeFromString<HygieneListResponse>(jsonString)
                    wrapped.hygieneProducts ?: emptyList()
                }
            }
        ) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getHygieneProductById(id: String): NetworkResult<HygieneDto> {
        return networkHandler.get<HygieneDto>(ApiEndpoints.getHygieneProduct(id))
    }

    override suspend fun getHygieneProductsByCategory(category: String): NetworkResult<List<HygieneDto>> {
        return networkHandler.get<List<HygieneDto>>(ApiEndpoints.HYGIENE_BY_CATEGORY) {
            parameter("category", category)
        }
    }

    override suspend fun searchHygieneProducts(query: String): NetworkResult<List<HygieneDto>> {
        return networkHandler.get<List<HygieneDto>>(ApiEndpoints.HYGIENE_SEARCH) {
            parameter("q", query)
        }
    }

    override suspend fun createHygieneProduct(request: CreateHygieneRequest): NetworkResult<HygieneDto> {
        return networkHandler.post<HygieneDto, CreateHygieneRequest>(
            urlString = ApiEndpoints.HYGIENE,
            body = request
        )
    }

    override suspend fun updateHygieneProduct(id: String, request: UpdateHygieneRequest): NetworkResult<HygieneDto> {
        return networkHandler.put<HygieneDto, UpdateHygieneRequest>(
            urlString = ApiEndpoints.getHygieneProduct(id),
            body = request
        )
    }

    override suspend fun deleteHygieneProduct(id: String): NetworkResult<Unit> {
        return networkHandler.delete<Unit>(ApiEndpoints.getHygieneProduct(id))
    }
}
