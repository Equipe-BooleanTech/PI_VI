package edu.fatec.petwise.features.food.data.datasource

import edu.fatec.petwise.features.food.domain.models.Food

class RemoteFoodDataSourceImpl : RemoteFoodDataSource {

    override suspend fun getAllFood(): List<Food> {
        println("API: Buscando todos os alimentos")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun getFoodById(id: String): Food? {
        println("API: Buscando alimento por ID: $id")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun createFood(food: Food): Food {
        println("API: Criando novo alimento - ${food.name}")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun updateFood(food: Food): Food {
        println("API: Atualizando alimento - ${food.name} (ID: ${food.id})")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun deleteFood(id: String) {
        println("API: Excluindo alimento com ID: $id")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun searchFood(query: String): List<Food> {
        println("API: Buscando alimentos com query: '$query'")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun getFoodByCategory(category: String): List<Food> {
        println("API: Buscando alimentos da categoria: $category")
        throw NotImplementedError("API endpoint not implemented yet")
    }
}
