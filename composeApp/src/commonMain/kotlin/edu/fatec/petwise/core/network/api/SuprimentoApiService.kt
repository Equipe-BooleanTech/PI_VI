package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.suprimentos.domain.models.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json

interface SuprimentoApiService {
    suspend fun getAllSuprimentos(): NetworkResult<List<SuprimentoDto>>
    suspend fun getSuprimentoById(id: String): NetworkResult<SuprimentoDto>
    suspend fun getSuprimentosByPetId(petId: String): NetworkResult<List<SuprimentoDto>>
    suspend fun getSuprimentosByCategory(category: String): NetworkResult<List<SuprimentoDto>>
    suspend fun searchSuprimentos(query: String): NetworkResult<List<SuprimentoDto>>
    suspend fun filterSuprimentos(options: SuprimentoFilterOptions): NetworkResult<List<SuprimentoDto>>
    suspend fun addSuprimento(suprimento: SuprimentoDto): NetworkResult<SuprimentoDto>
    suspend fun updateSuprimento(id: String, suprimento: SuprimentoDto): NetworkResult<SuprimentoDto>
    suspend fun deleteSuprimento(id: String): NetworkResult<Unit>
    suspend fun getRecentSuprimentos(limit: Int): NetworkResult<List<SuprimentoDto>>
    suspend fun getSuprimentosByPriceRange(minPrice: Float, maxPrice: Float): NetworkResult<List<SuprimentoDto>>
    suspend fun getSuprimentosByShop(shopName: String): NetworkResult<List<SuprimentoDto>>
}

class SuprimentoApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : SuprimentoApiService {

    override suspend fun getAllSuprimentos(): NetworkResult<List<SuprimentoDto>> {
        return networkHandler.getWithCustomDeserializer(
            urlString = ApiEndpoints.SUPRIMENTOS,
            deserializer = { jsonString ->
                val json = Json { ignoreUnknownKeys = true }
                try {
                    // Try to parse as direct array first
                    json.decodeFromString<List<SuprimentoDto>>(jsonString)
                } catch (e: Exception) {
                    // Fallback to wrapped object
                    val wrapped = json.decodeFromString<SuprimentoListResponse>(jsonString)
                    wrapped.suprimentos ?: emptyList()
                }
            }
        )
    }

    override suspend fun getSuprimentoById(id: String): NetworkResult<SuprimentoDto> {
        return networkHandler.get<SuprimentoDto>(ApiEndpoints.getSuprimento(id))
    }

    override suspend fun getSuprimentosByPetId(petId: String): NetworkResult<List<SuprimentoDto>> {
        return networkHandler.get<List<SuprimentoDto>>(ApiEndpoints.getSuprimentosByPet(petId))
    }

    override suspend fun getSuprimentosByCategory(category: String): NetworkResult<List<SuprimentoDto>> {
        return networkHandler.get<List<SuprimentoDto>>(ApiEndpoints.SUPRIMENTOS_BY_CATEGORY) {
            parameter("category", category)
        }
    }

    override suspend fun searchSuprimentos(query: String): NetworkResult<List<SuprimentoDto>> {
        return networkHandler.getWithCustomDeserializer(
            urlString = ApiEndpoints.SUPRIMENTOS_SEARCH,
            deserializer = { jsonString ->
                val json = Json { ignoreUnknownKeys = true }
                try {
                    // Try to parse as direct array first
                    json.decodeFromString<List<SuprimentoDto>>(jsonString)
                } catch (e: Exception) {
                    // Fallback to wrapped object
                    val wrapped = json.decodeFromString<SuprimentoListResponse>(jsonString)
                    wrapped.suprimentos ?: emptyList()
                }
            }
        ) {
            parameter("query", query)
        }
    }

    override suspend fun filterSuprimentos(options: SuprimentoFilterOptions): NetworkResult<List<SuprimentoDto>> {
        return networkHandler.getWithCustomDeserializer(
            urlString = ApiEndpoints.SUPRIMENTOS_FILTER,
            deserializer = { jsonString ->
                val json = Json { ignoreUnknownKeys = true }
                try {
                    // Try to parse as direct array first
                    json.decodeFromString<List<SuprimentoDto>>(jsonString)
                } catch (e: Exception) {
                    // Fallback to wrapped object
                    val wrapped = json.decodeFromString<SuprimentoListResponse>(jsonString)
                    wrapped.suprimentos ?: emptyList()
                }
            }
        ) {
            options.petId?.let { parameter("petId", it) }
            options.category?.let { parameter("category", it.name) }
            options.searchQuery?.let { parameter("searchQuery", it) }
            options.minPrice?.let { parameter("minPrice", it) }
            options.maxPrice?.let { parameter("maxPrice", it) }
            options.shopName?.let { parameter("shopName", it) }
        }
    }

    override suspend fun addSuprimento(suprimento: SuprimentoDto): NetworkResult<SuprimentoDto> {
        return networkHandler.post<SuprimentoDto, SuprimentoDto>(
            urlString = ApiEndpoints.SUPRIMENTOS,
            body = suprimento
        )
    }

    override suspend fun updateSuprimento(id: String, suprimento: SuprimentoDto): NetworkResult<SuprimentoDto> {
        return networkHandler.put<SuprimentoDto, SuprimentoDto>(
            urlString = ApiEndpoints.getSuprimento(id),
            body = suprimento
        )
    }

    override suspend fun deleteSuprimento(id: String): NetworkResult<Unit> {
        return networkHandler.delete<Unit>(ApiEndpoints.getSuprimento(id))
    }

    override suspend fun getRecentSuprimentos(limit: Int): NetworkResult<List<SuprimentoDto>> {
        return networkHandler.get<List<SuprimentoDto>>(ApiEndpoints.SUPRIMENTOS_RECENT) {
            parameter("limit", limit)
        }
    }

    override suspend fun getSuprimentosByPriceRange(
        minPrice: Float,
        maxPrice: Float
    ): NetworkResult<List<SuprimentoDto>> {
        return networkHandler.get<List<SuprimentoDto>>(ApiEndpoints.SUPRIMENTOS_PRICE_RANGE) {
            parameter("minPrice", minPrice)
            parameter("maxPrice", maxPrice)
        }
    }

    override suspend fun getSuprimentosByShop(shopName: String): NetworkResult<List<SuprimentoDto>> {
        return networkHandler.get<List<SuprimentoDto>>(ApiEndpoints.SUPRIMENTOS_BY_SHOP) {
            parameter("shopName", shopName)
        }
    }
}