package edu.fatec.petwise.features.food.domain.usecases

import edu.fatec.petwise.features.food.domain.models.Food
import edu.fatec.petwise.features.food.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow

class GetFoodUseCase(
    private val repository: FoodRepository
) {
    operator fun invoke(): Flow<List<Food>> = repository.getAllFood()

    fun searchFood(query: String): Flow<List<Food>> = repository.searchFood(query)

    fun getFoodByCategory(category: String): Flow<List<Food>> = repository.getFoodByCategory(category)
}

class GetFoodByIdUseCase(
    private val repository: FoodRepository
) {
    operator fun invoke(id: String): Flow<Food?> = repository.getFoodById(id)
}

class AddFoodUseCase(
    private val repository: FoodRepository
) {
    suspend operator fun invoke(food: Food): Result<Food> {
        return if (validateFood(food)) {
            repository.addFood(food)
        } else {
            Result.failure(IllegalArgumentException("Food data is invalid"))
        }
    }

    private fun validateFood(food: Food): Boolean {
        return food.name.isNotBlank() &&
               food.brand.isNotBlank() &&
               food.category.isNotBlank() &&
               food.price > 0 &&
               food.stock >= 0
    }
}

class UpdateFoodUseCase(
    private val repository: FoodRepository
) {
    suspend operator fun invoke(food: Food): Result<Food> = repository.updateFood(food)
}

class DeleteFoodUseCase(
    private val repository: FoodRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteFood(id)
}
