package edu.fatec.petwise.features.toys.domain.usecases

import edu.fatec.petwise.features.toys.domain.models.Toy
import edu.fatec.petwise.features.toys.domain.repository.ToyRepository
import kotlinx.coroutines.flow.Flow

class GetToysUseCase(
    private val repository: ToyRepository
) {
    operator fun invoke(): Flow<List<Toy>> = repository.getAllToys()

    fun searchToys(query: String): Flow<List<Toy>> = repository.searchToys(query)

    fun getToysByCategory(category: String): Flow<List<Toy>> = repository.getToysByCategory(category)
}

class GetToyByIdUseCase(
    private val repository: ToyRepository
) {
    operator fun invoke(id: String): Flow<Toy?> = repository.getToyById(id)
}

class AddToyUseCase(
    private val repository: ToyRepository
) {
    suspend operator fun invoke(toy: Toy): Result<Toy> {
        return if (validateToy(toy)) {
            repository.addToy(toy)
        } else {
            Result.failure(IllegalArgumentException("Toy data is invalid"))
        }
    }

    private fun validateToy(toy: Toy): Boolean {
        return toy.name.isNotBlank() &&
               toy.brand.isNotBlank() &&
               toy.category.isNotBlank() &&
               toy.price > 0 &&
               toy.stock >= 0
    }
}

class UpdateToyUseCase(
    private val repository: ToyRepository
) {
    suspend operator fun invoke(toy: Toy): Result<Toy> = repository.updateToy(toy)
}

class DeleteToyUseCase(
    private val repository: ToyRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteToy(id)
}
