package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*

interface FoodApiService {
    suspend fun getAllFood(page: Int = 1, pageSize: Int = 20): NetworkResult<FoodListResponse>
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

    override suspend fun getAllFood(page: Int, pageSize: Int): NetworkResult<FoodListResponse> {
        return networkHandler.get<FoodListResponse>(ApiEndpoints.FOOD) {
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
