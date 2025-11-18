package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json

interface FoodApiService {
    suspend fun getAllFood(page: Int = 1, pageSize: Int = 20): NetworkResult<List<FoodDto>>
    suspend fun getFoodById(id: String): NetworkResult<FoodDto>
    suspend fun getFoodByCategory(category: String): NetworkResult<List<FoodDto>>
    suspend fun searchFood(query: String): NetworkResult<List<FoodDto>>
    suspend fun createFood(request: CreateFoodRequest): NetworkResult<FoodDto>
    suspend fun updateFood(id: String, request: UpdateFoodRequest): NetworkResult<FoodDto>
    suspend fun deleteFood(id: String): NetworkResult<Unit>
}

class FoodApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : FoodApiService {

    override suspend fun getAllFood(page: Int, pageSize: Int): NetworkResult<List<FoodDto>> {
        return networkHandler.getWithCustomDeserializer(
            urlString = ApiEndpoints.FOOD,
            deserializer = { jsonString ->
                val json = Json { ignoreUnknownKeys = true }
                try {
                    // Try to parse as direct array first
                    json.decodeFromString<List<FoodDto>>(jsonString)
                } catch (e: Exception) {
                    // Fallback to wrapped object
                    val wrapped = json.decodeFromString<FoodListResponse>(jsonString)
                    wrapped.foods ?: emptyList()
                }
            }
        ) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getFoodById(id: String): NetworkResult<FoodDto> {
        return networkHandler.get<FoodDto>(ApiEndpoints.getFood(id))
    }

    override suspend fun getFoodByCategory(category: String): NetworkResult<List<FoodDto>> {
        return networkHandler.get<List<FoodDto>>(ApiEndpoints.FOOD_BY_CATEGORY) {
            parameter("category", category)
        }
    }

    override suspend fun searchFood(query: String): NetworkResult<List<FoodDto>> {
        return networkHandler.get<List<FoodDto>>(ApiEndpoints.FOOD_SEARCH) {
            parameter("q", query)
        }
    }

    override suspend fun createFood(request: CreateFoodRequest): NetworkResult<FoodDto> {
        return networkHandler.post<FoodDto, CreateFoodRequest>(
            urlString = ApiEndpoints.FOOD,
            body = request
        )
    }

    override suspend fun updateFood(id: String, request: UpdateFoodRequest): NetworkResult<FoodDto> {
        return networkHandler.put<FoodDto, UpdateFoodRequest>(
            urlString = ApiEndpoints.getFood(id),
            body = request
        )
    }

    override suspend fun deleteFood(id: String): NetworkResult<Unit> {
        return networkHandler.delete<Unit>(ApiEndpoints.getFood(id))
    }
}
