package edu.fatec.petwise.features.pets.data.repository

import edu.fatec.petwise.features.pets.data.datasource.LocalPetDataSource
import edu.fatec.petwise.features.pets.data.datasource.RemotePetDataSourceImpl
import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.models.PetFilterOptions
import edu.fatec.petwise.features.pets.domain.models.HealthStatus
import edu.fatec.petwise.features.pets.domain.repository.PetRepository
import edu.fatec.petwise.presentation.shared.form.currentTimeMs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PetRepositoryImpl(
    private val localDataSource: LocalPetDataSource,
    private val remoteDataSource: RemotePetDataSourceImpl? = null,
    private val syncStrategy: SyncStrategy = SyncStrategy.CACHE_FIRST
) : PetRepository {

    override fun getAllPets(): Flow<List<Pet>> = flow {
        if (remoteDataSource == null) {
            localDataSource.getAllPets().collect { emit(it) }
            return@flow
        }

        when (syncStrategy) {
            SyncStrategy.CACHE_FIRST -> {
                val cachedPets = localDataSource.getAllPets().first()
                if (cachedPets.isNotEmpty()) {
                    emit(cachedPets)
                }

                try {
                    val remotePets = remoteDataSource.getAllPets()
                    remotePets.forEach { localDataSource.insertPet(it) }
                    emit(remotePets)
                } catch (e: Exception) {
                    if (cachedPets.isEmpty()) {
                        throw e
                    }
                }
            }

            SyncStrategy.REMOTE_FIRST -> {
                try {
                    val remotePets = remoteDataSource.getAllPets()
                    remotePets.forEach { localDataSource.insertPet(it) }
                    emit(remotePets)
                } catch (e: Exception) {
                    val cachedPets = localDataSource.getAllPets().first()
                    if (cachedPets.isNotEmpty()) {
                        emit(cachedPets)
                    } else {
                        throw e
                    }
                }
            }

            SyncStrategy.CACHE_ONLY -> {
                localDataSource.getAllPets().collect { emit(it) }
            }

            SyncStrategy.REMOTE_ONLY -> {
                val remotePets = remoteDataSource.getAllPets()
                emit(remotePets)
            }
        }
    }

    override fun getPetById(id: String): Flow<Pet?> = flow {
        if (remoteDataSource == null) {
            localDataSource.getPetById(id).collect { emit(it) }
            return@flow
        }

        when (syncStrategy) {
            SyncStrategy.CACHE_FIRST -> {
                val cachedPet = localDataSource.getPetById(id).first()
                if (cachedPet != null) {
                    emit(cachedPet)
                }

                try {
                    val remotePet = remoteDataSource.getPetById(id)
                    if (remotePet != null) {
                        localDataSource.insertPet(remotePet)
                        emit(remotePet)
                    }
                } catch (e: Exception) {
                    if (cachedPet == null) {
                        throw e
                    }
                }
            }

            SyncStrategy.REMOTE_FIRST -> {
                try {
                    val remotePet = remoteDataSource.getPetById(id)
                    if (remotePet != null) {
                        localDataSource.insertPet(remotePet)
                        emit(remotePet)
                    } else {
                        emit(null)
                    }
                } catch (e: Exception) {
                    val cachedPet = localDataSource.getPetById(id).first()
                    emit(cachedPet)
                }
            }

            SyncStrategy.CACHE_ONLY -> {
                localDataSource.getPetById(id).collect { emit(it) }
            }

            SyncStrategy.REMOTE_ONLY -> {
                val remotePet = remoteDataSource.getPetById(id)
                emit(remotePet)
            }
        }
    }

    override fun searchPets(query: String): Flow<List<Pet>> = flow {
        if (remoteDataSource == null) {
            localDataSource.getAllPets().map { pets ->
                pets.filter { pet ->
                    pet.name.contains(query, ignoreCase = true) ||
                    pet.breed.contains(query, ignoreCase = true) ||
                    pet.ownerName.contains(query, ignoreCase = true)
                }
            }.collect { emit(it) }
            return@flow
        }

        try {
            val remotePets = remoteDataSource.searchPets(query)
            emit(remotePets)
        } catch (e: Exception) {
            localDataSource.getAllPets().map { pets ->
                pets.filter { pet ->
                    pet.name.contains(query, ignoreCase = true) ||
                    pet.breed.contains(query, ignoreCase = true) ||
                    pet.ownerName.contains(query, ignoreCase = true)
                }
            }.collect { emit(it) }
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

    override fun getFavoritePets(): Flow<List<Pet>> = flow {
        if (remoteDataSource == null) {
            localDataSource.getAllPets().map { pets ->
                pets.filter { it.isFavorite }
            }.collect { emit(it) }
            return@flow
        }

        try {
            val favoritePets = remoteDataSource.getFavoritePets()
            emit(favoritePets)
        } catch (e: Exception) {
            localDataSource.getAllPets().map { pets ->
                pets.filter { it.isFavorite }
            }.collect { emit(it) }
        }
    }

    override suspend fun addPet(pet: Pet): Result<Pet> {
        if (remoteDataSource == null) {
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

        return try {
            val createdPet = remoteDataSource.createPet(pet)
            localDataSource.insertPet(createdPet)
            Result.success(createdPet)
        } catch (e: Exception) {
            localDataSource.insertPet(pet)
            Result.failure(e)
        }
    }

    override suspend fun updatePet(pet: Pet): Result<Pet> {
        if (remoteDataSource == null) {
            return try {
                val updatedPet = pet.copy(updatedAt = getCurrentTimestamp())
                localDataSource.updatePet(updatedPet)
                Result.success(updatedPet)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        return try {
            val updatedPet = remoteDataSource.updatePet(pet)
            localDataSource.updatePet(updatedPet)
            Result.success(updatedPet)
        } catch (e: Exception) {
            localDataSource.updatePet(pet)
            Result.failure(e)
        }
    }

    override suspend fun deletePet(id: String): Result<Unit> {
        if (remoteDataSource == null) {
            return try {
                localDataSource.deletePet(id)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        return try {
            remoteDataSource.deletePet(id)
            localDataSource.deletePet(id)
            Result.success(Unit)
        } catch (e: Exception) {
            localDataSource.deletePet(id)
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(id: String): Result<Pet> {
        if (remoteDataSource == null) {
            return try {
                val currentPet = localDataSource.getPetById(id).map { it }.first()

                currentPet?.let { pet ->
                    val updatedPet = pet.copy(
                        isFavorite = !pet.isFavorite,
                        updatedAt = getCurrentTimestamp()
                    )
                    localDataSource.updatePet(updatedPet)
                    Result.success(updatedPet)
                } ?: Result.failure(IllegalArgumentException("Pet não encontrado"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        return try {
            val updatedPet = remoteDataSource.toggleFavorite(id)
            localDataSource.updatePet(updatedPet)
            Result.success(updatedPet)
        } catch (e: Exception) {
            val pet = localDataSource.getPetById(id).first()
            if (pet != null) {
                val toggledPet = pet.copy(isFavorite = !pet.isFavorite)
                localDataSource.updatePet(toggledPet)
                Result.success(toggledPet)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun updateHealthStatus(id: String, status: HealthStatus): Result<Pet> {
        if (remoteDataSource == null) {
            return try {
                val currentPet = localDataSource.getPetById(id).map { it }.first()

                currentPet?.let { pet ->
                    val updatedPet = pet.copy(
                        healthStatus = status,
                        updatedAt = getCurrentTimestamp()
                    )
                    localDataSource.updatePet(updatedPet)
                    Result.success(updatedPet)
                } ?: Result.failure(IllegalArgumentException("Pet não encontrado"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        return try {
            val updatedPet = remoteDataSource.updateHealthStatus(id, status)
            localDataSource.updatePet(updatedPet)
            Result.success(updatedPet)
        } catch (e: Exception) {
            val pet = localDataSource.getPetById(id).first()
            if (pet != null) {
                val updatedPet = pet.copy(healthStatus = status)
                localDataSource.updatePet(updatedPet)
                Result.success(updatedPet)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun syncPendingChanges(): Result<Unit> = try {
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun generateId(): String = currentTimeMs().toString()

    private fun getCurrentTimestamp(): String {
        return currentTimeMs().toString()
    }
}

enum class SyncStrategy {
    CACHE_FIRST,
    REMOTE_FIRST,
    CACHE_ONLY,
    REMOTE_ONLY
}