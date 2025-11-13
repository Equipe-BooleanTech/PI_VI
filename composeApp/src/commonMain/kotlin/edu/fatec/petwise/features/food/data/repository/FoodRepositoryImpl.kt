package edu.fatec.petwise.features.food.data.repository

import edu.fatec.petwise.features.food.data.datasource.RemoteFoodDataSource
import edu.fatec.petwise.features.food.domain.models.Food
import edu.fatec.petwise.features.food.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FoodRepositoryImpl(
    private val remoteDataSource: RemoteFoodDataSource
) : FoodRepository {

    override fun getAllFood(): Flow<List<Food>> = flow {
        try {
            println("Repositório: Buscando todos os alimentos via API")
            val food = remoteDataSource.getAllFood()
            println("Repositório: ${food.size} alimentos carregados com sucesso da API")
            emit(food)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar alimentos da API - ${e.message}")
                throw e
            }
        }

    override fun getFoodById(id: String): Flow<Food?> = flow {
        try {
            println("Repositório: Buscando alimento por ID '$id' via API")
            val food = remoteDataSource.getFoodById(id)
            if (food != null) {
                println("Repositório: Alimento '${food.name}' encontrado com sucesso")
            } else {
                println("Repositório: Alimento com ID '$id' não encontrado")
            }
            emit(food)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar alimento por ID '$id' - ${e.message}")
                throw e
            }
        }

    override fun searchFood(query: String): Flow<List<Food>> = flow {
        try {
            println("Repositório: Iniciando busca de alimentos com consulta '$query'")
            val food = remoteDataSource.searchFood(query)
            println("Repositório: Busca concluída - ${food.size} alimentos encontrados")
            emit(food)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar alimentos na API - ${e.message}")
                throw e
            }
        }

    override fun getFoodByCategory(category: String): Flow<List<Food>> = flow {
        try {
            println("Repositório: Buscando alimentos da categoria '$category' via API")
            val food = remoteDataSource.getFoodByCategory(category)
            println("Repositório: ${food.size} alimentos encontrados")
            emit(food)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar alimentos da categoria - ${e.message}")
                throw e
        }
    }

    override suspend fun addFood(food: Food): Result<Food> {
        return try {
            println("Repositório: Adicionando novo alimento '${food.name}' via API")
            val createdFood = remoteDataSource.createFood(food)
            println("Repositório: Alimento '${createdFood.name}' criado com sucesso - ID: ${createdFood.id}")
            Result.success(createdFood)
        } catch (e: Exception) {
            println("Repositório: Erro ao criar alimento '${food.name}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateFood(food: Food): Result<Food> {
        return try {
            println("Repositório: Atualizando alimento '${food.name}' (ID: ${food.id}) via API")
            val updatedFood = remoteDataSource.updateFood(food)
            println("Repositório: Alimento '${updatedFood.name}' atualizado com sucesso")
            Result.success(updatedFood)
        } catch (e: Exception) {
            println("Repositório: Erro ao atualizar alimento '${food.name}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteFood(id: String): Result<Unit> {
        return try {
            println("Repositório: Excluindo alimento com ID '$id' via API")
            remoteDataSource.deleteFood(id)
            println("Repositório: Alimento excluído com sucesso")
            Result.success(Unit)
        } catch (e: Exception) {
            println("Repositório: Erro ao excluir alimento com ID '$id' - ${e.message}")
            Result.failure(e)
        }
    }
}

