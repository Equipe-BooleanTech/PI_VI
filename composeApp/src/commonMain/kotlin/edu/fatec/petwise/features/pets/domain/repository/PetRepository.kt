package edu.fatec.petwise.features.pets.domain.repository

import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.models.PetFilterOptions
import kotlinx.coroutines.flow.Flow
import edu.fatec.petwise.features.pets.domain.models.HealthStatus

interface PetRepository {
    fun getAllPets(): Flow<List<Pet>>

    fun getPetById(id: String): Flow<Pet?>

    fun searchPets(query: String): Flow<List<Pet>>

    fun filterPets(options: PetFilterOptions): Flow<List<Pet>>

    fun getFavoritePets(): Flow<List<Pet>>

    suspend fun addPet(pet: Pet): Result<Pet>

    suspend fun updatePet(pet: Pet): Result<Pet>

    suspend fun deletePet(id: String): Result<Unit>

    suspend fun toggleFavorite(id: String): Result<Pet>

    suspend fun updateHealthStatus(id: String, status: HealthStatus): Result<Pet>
}