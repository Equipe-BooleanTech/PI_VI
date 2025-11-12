package edu.fatec.petwise.features.toys.data.datasource

import edu.fatec.petwise.features.toys.domain.models.Toy

interface RemoteToyDataSource {
    suspend fun getAllToys(): List<Toy>
    suspend fun getToyById(id: String): Toy?
    suspend fun createToy(toy: Toy): Toy
    suspend fun updateToy(toy: Toy): Toy
    suspend fun deleteToy(id: String)
    suspend fun searchToys(query: String): List<Toy>
    suspend fun getToysByCategory(category: String): List<Toy>
}
