package edu.fatec.petwise.features.pets.data.repository

import edu.fatec.petwise.features.pets.data.datasource.LocalPetDataSource
import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.models.PetFilterOptions
import edu.fatec.petwise.features.pets.domain.models.HealthStatus
import edu.fatec.petwise.features.pets.domain.repository.PetRepository
import edu.fatec.petwise.presentation.shared.form.currentTimeMs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PetRepositoryImpl(
    private val localDataSource: LocalPetDataSource
) : PetRepository {

    override fun getAllPets(): Flow<List<Pet>> = localDataSource.getAllPets()

    override fun getPetById(id: String): Flow<Pet?> = localDataSource.getPetById(id)

    override fun searchPets(query: String): Flow<List<Pet>> {
        return localDataSource.getAllPets().map { pets ->
            pets.filter { pet ->
                pet.name.contains(query, ignoreCase = true) ||
                pet.breed.contains(query, ignoreCase = true) ||
                pet.ownerName.contains(query, ignoreCase = true)
            }
        }
    }

    override fun filterPets(options: PetFilterOptions): Flow<List<Pet>> {
        return localDataSource.getAllPets().map { pets ->
            pets.filter { pet ->
                val speciesMatch = options.species?.let { pet.species == it } ?: true
                val healthMatch = options.healthStatus?.let { pet.healthStatus == it } ?: true
                val favoriteMatch = if (options.favoritesOnly) pet.isFavorite else true
                val searchMatch = if (options.searchQuery.isNotBlank()) {
                    pet.name.contains(options.searchQuery, ignoreCase = true) ||
                    pet.breed.contains(options.searchQuery, ignoreCase = true) ||
                    pet.ownerName.contains(options.searchQuery, ignoreCase = true)
                } else true

                speciesMatch && healthMatch && favoriteMatch && searchMatch
            }
        }
    }

    override fun getFavoritePets(): Flow<List<Pet>> {
        return localDataSource.getAllPets().map { pets ->
            pets.filter { it.isFavorite }
        }
    }

    override suspend fun addPet(pet: Pet): Result<Pet> {
        return try {
            val newPet = pet.copy(
                id = generateId(),
                createdAt = getCurrentTimestamp(),
                updatedAt = getCurrentTimestamp()
            )
            localDataSource.insertPet(newPet)
            Result.success(newPet)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePet(pet: Pet): Result<Pet> {
        return try {
            val updatedPet = pet.copy(updatedAt = getCurrentTimestamp())
            localDataSource.updatePet(updatedPet)
            Result.success(updatedPet)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePet(id: String): Result<Unit> {
        return try {
            localDataSource.deletePet(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(id: String): Result<Pet> {
        return try {
            val currentPet = localDataSource.getPetById(id).map { it }.first()

            currentPet?.let { pet ->
                val updatedPet = pet.copy(
                    isFavorite = !pet.isFavorite,
                    updatedAt = getCurrentTimestamp()
                )
                localDataSource.updatePet(updatedPet)
                Result.success(updatedPet)
            } ?: Result.failure(IllegalArgumentException("Pet not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateHealthStatus(id: String, status: HealthStatus): Result<Pet> {
        return try {
            val currentPet = localDataSource.getPetById(id).map { it }.first()

            currentPet?.let { pet ->
                val updatedPet = pet.copy(
                    healthStatus = status,
                    updatedAt = getCurrentTimestamp()
                )
                localDataSource.updatePet(updatedPet)
                Result.success(updatedPet)
            } ?: Result.failure(IllegalArgumentException("Pet not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateId(): String = currentTimeMs().toString()

    private fun getCurrentTimestamp(): String {
        return currentTimeMs().toString()
    }
}