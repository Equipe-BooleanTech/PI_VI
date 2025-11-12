package edu.fatec.petwise.features.food.domain.repository

import edu.fatec.petwise.features.food.domain.models.Food
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getAllFood(): Flow<List<Food>>
    fun getFoodById(id: String): Flow<Food?>
    fun searchFood(query: String): Flow<List<Food>>
    fun getFoodByCategory(category: String): Flow<List<Food>>
    suspend fun addFood(food: Food): Result<Food>
    suspend fun updateFood(food: Food): Result<Food>
    suspend fun deleteFood(id: String): Result<Unit>
}
