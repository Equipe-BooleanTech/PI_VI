package edu.fatec.petwise.features.toys.domain.repository

import edu.fatec.petwise.features.toys.domain.models.Toy
import kotlinx.coroutines.flow.Flow

interface ToyRepository {
    fun getAllToys(): Flow<List<Toy>>
    fun getToyById(id: String): Flow<Toy?>
    fun searchToys(query: String): Flow<List<Toy>>
    fun getToysByCategory(category: String): Flow<List<Toy>>
    suspend fun addToy(toy: Toy): Result<Toy>
    suspend fun updateToy(toy: Toy): Result<Toy>
    suspend fun deleteToy(id: String): Result<Unit>
}
