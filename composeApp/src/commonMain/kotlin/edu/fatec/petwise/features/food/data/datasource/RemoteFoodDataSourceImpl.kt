package edu.fatec.petwise.features.food.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.FoodApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.food.domain.models.Food

class RemoteFoodDataSourceImpl(
    private val foodApiService: FoodApiService
) : RemoteFoodDataSource {

    override suspend fun getAllFood(): List<Food> {
        return when (val result = foodApiService.getAllFood(1, 1000)) {
            is NetworkResult.Success -> result.data.map { it.toFood() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getFoodById(id: String): Food? {
        return when (val result = foodApiService.getFoodById(id)) {
            is NetworkResult.Success -> result.data.toFood()
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                null
            }
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun createFood(food: Food): Food {
        val request = CreateFoodRequest(
            name = food.name,
            brand = food.brand,
            category = food.category,
            description = food.description,
            price = food.price,
            stock = food.stock,
            unit = food.unit,
            expiryDate = food.expiryDate,
            imageUrl = food.imageUrl,
            active = food.active
        )
        return when (val result = foodApiService.createFood(request)) {
            is NetworkResult.Success -> result.data.toFood()
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun updateFood(food: Food): Food {
        val request = UpdateFoodRequest(
            name = food.name,
            brand = food.brand,
            category = food.category,
            description = food.description,
            price = food.price,
            stock = food.stock,
            unit = food.unit,
            expiryDate = food.expiryDate,
            imageUrl = food.imageUrl,
            active = food.active
        )
        return when (val result = foodApiService.updateFood(food.id, request)) {
            is NetworkResult.Success -> result.data.toFood()
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun deleteFood(id: String) {
        when (val result = foodApiService.deleteFood(id)) {
            is NetworkResult.Success -> Unit
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun searchFood(query: String): List<Food> {
        return when (val result = foodApiService.searchFood(query)) {
            is NetworkResult.Success -> result.data.map { it.toFood() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getFoodByCategory(category: String): List<Food> {
        return when (val result = foodApiService.getFoodByCategory(category)) {
            is NetworkResult.Success -> result.data.map { it.toFood() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }
}
