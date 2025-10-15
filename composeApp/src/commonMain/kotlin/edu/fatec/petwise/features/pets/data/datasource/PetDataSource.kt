package edu.fatec.petwise.features.pets.data.datasource

import edu.fatec.petwise.features.pets.domain.models.Pet

interface RemotePetDataSource {
    suspend fun getAllPets(): List<Pet>
    suspend fun getPetById(id: String): Pet?
    suspend fun createPet(pet: Pet): Pet
    suspend fun updatePet(pet: Pet): Pet
    suspend fun deletePet(id: String)
    suspend fun toggleFavorite(id: String): Pet
    suspend fun updateHealthStatus(id: String, status: edu.fatec.petwise.features.pets.domain.models.HealthStatus, notes: String? = null): Pet
    suspend fun searchPets(query: String): List<Pet>
    suspend fun getFavoritePets(): List<Pet>
}