package edu.fatec.petwise.features.food.data.datasource

import edu.fatec.petwise.features.food.domain.models.Food

interface RemoteFoodDataSource {
    suspend fun getAllFood(): List<Food>
    suspend fun getFoodById(id: String): Food?
    suspend fun createFood(food: Food): Food
    suspend fun updateFood(food: Food): Food
    suspend fun deleteFood(id: String)
    suspend fun searchFood(query: String): List<Food>
    suspend fun getFoodByCategory(category: String): List<Food>
}
