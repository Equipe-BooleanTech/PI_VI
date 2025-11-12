package edu.fatec.petwise.features.pets.data.repository

import edu.fatec.petwise.core.config.AppConfig
import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.core.data.MockDataProvider
import edu.fatec.petwise.features.pets.data.datasource.RemotePetDataSourceImpl
import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.models.PetFilterOptions
import edu.fatec.petwise.features.pets.domain.models.HealthStatus
import edu.fatec.petwise.features.pets.domain.repository.PetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class PetRepositoryImpl(
    private val remoteDataSource: RemotePetDataSourceImpl
) : PetRepository {

    override fun getAllPets(): Flow<List<Pet>> = flow {
        try {
            println("Repositório: Buscando todos os pets via API")
            val pets = remoteDataSource.getAllPets()
            println("Repositório: ${pets.size} pets carregados com sucesso da API")
            emit(pets)
        } catch (e: Exception) {
            if (AppConfig.useMockDataFallback) {
                println("Repositório: Erro ao buscar pets da API - ${e.message}. Usando dados mock como fallback")
                try {
                    val mockPets = MockDataProvider.getMockPets()
                    emit(mockPets)
                } catch (emitError: Exception) {
                    println("Repositório: Erro ao emitir dados mock - ${emitError.message}")
                }
            } else {
                println("Repositório: Erro ao buscar pets da API - ${e.message}")
                throw e
            }
        }
    }

    override fun getPetById(id: String): Flow<Pet?> = flow {
        try {
            println("Repositório: Buscando pet por ID '$id' via API")
            val pet = remoteDataSource.getPetById(id)
            if (pet != null) {
                println("Repositório: Pet '${pet.name}' encontrado com sucesso")
            } else {
                println("Repositório: Pet com ID '$id' não encontrado")
            }
            emit(pet)
        } catch (e: Exception) {
            if (AppConfig.useMockDataFallback) {
                println("Repositório: Erro ao buscar pet por ID '$id' - ${e.message}. Usando dados mock como fallback")
                try {
                    val mockPet = MockDataProvider.getMockPets().find { it.id == id }
                    emit(mockPet)
                } catch (emitError: Exception) {
                    println("Repositório: Erro ao emitir dados mock - ${emitError.message}")
                }
            } else {
                println("Repositório: Erro ao buscar pet por ID '$id' - ${e.message}")
                throw e
            }
        }
    }

    override fun searchPets(query: String): Flow<List<Pet>> = flow {
        try {
            println("Repositório: Iniciando busca de pets com consulta '$query'")
            val pets = remoteDataSource.searchPets(query)
            println("Repositório: Busca concluída - ${pets.size} pets encontrados")
            emit(pets)
        } catch (e: Exception) {
            if (AppConfig.useMockDataFallback) {
                println("Repositório: Erro ao buscar pets na API - ${e.message}. Usando dados mock como fallback")
                try {
                    val mockPets = MockDataProvider.getMockPets()
                        .filter { it.name.contains(query, ignoreCase = true) || it.breed.contains(query, ignoreCase = true) }
                    emit(mockPets)
                } catch (emitError: Exception) {
                    println("Repositório: Erro ao emitir dados mock - ${emitError.message}")
                }
            } else {
                println("Repositório: Erro ao buscar pets na API - ${e.message}")
                throw e
            }
        }
    }

    override fun filterPets(options: PetFilterOptions): Flow<List<Pet>> = flow {
        try {
            println("Repositório: Aplicando filtros nos pets via API")
            val pets = remoteDataSource.getAllPets()
            val filteredPets = pets.filter { pet ->
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
            println("Repositório: Filtros aplicados - ${filteredPets.size} pets encontrados")
            emit(filteredPets)
        } catch (e: Exception) {
            if (AppConfig.useMockDataFallback) {
                println("Repositório: Erro ao filtrar pets - ${e.message}. Usando dados mock como fallback")
                try {
                    val mockPets = MockDataProvider.getMockPets()
                    val filteredPets = mockPets.filter { pet ->
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
                    emit(filteredPets)
                } catch (emitError: Exception) {
                    println("Repositório: Erro ao emitir dados mock - ${emitError.message}")
                }
            } else {
                println("Repositório: Erro ao filtrar pets - ${e.message}")
                throw e
            }
        }
    }

    override fun getFavoritePets(): Flow<List<Pet>> = flow {
        try {
            println("Repositório: Buscando pets favoritos via API")
            val favoritePets = remoteDataSource.getFavoritePets()
            println("Repositório: ${favoritePets.size} pets favoritos encontrados")
            emit(favoritePets)
        } catch (e: Exception) {
            if (AppConfig.useMockDataFallback) {
                println("Repositório: Erro ao buscar pets favoritos - ${e.message}. Usando dados mock como fallback")
                try {
                    val mockPets = MockDataProvider.getMockPets().filter { it.isFavorite }
                    emit(mockPets)
                } catch (emitError: Exception) {
                    println("Repositório: Erro ao emitir dados mock - ${emitError.message}")
                }
            } else {
                println("Repositório: Erro ao buscar pets favoritos - ${e.message}")
                throw e
            }
        }
    }

    override suspend fun addPet(pet: Pet): Result<Pet> {
        return try {
            println("Repositório: Adicionando novo pet '${pet.name}' via API")
            val createdPet = remoteDataSource.createPet(pet)
            println("Repositório: Pet '${createdPet.name}' criado com sucesso - ID: ${createdPet.id}")
            DataRefreshManager.notifyPetsUpdated()
            Result.success(createdPet)
        } catch (e: Exception) {
            println("Repositório: Erro ao criar pet '${pet.name}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updatePet(pet: Pet): Result<Pet> {
        return try {
            println("Repositório: Atualizando pet '${pet.name}' (ID: ${pet.id}) via API")
            val updatedPet = remoteDataSource.updatePet(pet)
            println("Repositório: Pet '${updatedPet.name}' atualizado com sucesso - notificando atualizações")
            DataRefreshManager.notifyPetUpdated(updatedPet.id)
            DataRefreshManager.notifyPetsUpdated()
            println("Repositório: Eventos de atualização enviados para pet '${updatedPet.id}'")
            Result.success(updatedPet)
        } catch (e: Exception) {
            println("Repositório: Erro ao atualizar pet '${pet.name}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deletePet(id: String): Result<Unit> {
        return try {
            println("Repositório: Excluindo pet com ID '$id' via API")
            remoteDataSource.deletePet(id)
            println("Repositório: Pet excluído com sucesso")
            DataRefreshManager.notifyPetsUpdated()
            Result.success(Unit)
        } catch (e: Exception) {
            println("Repositório: Erro ao excluir pet com ID '$id' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(id: String): Result<Pet> {
        return try {
            println("Repositório: Alternando status de favorito do pet '$id' via API")
            val updatedPet = remoteDataSource.toggleFavorite(id)
            val status = if (updatedPet.isFavorite) "adicionado aos favoritos" else "removido dos favoritos"
            println("Repositório: Pet '${updatedPet.name}' $status com sucesso")
            DataRefreshManager.notifyPetUpdated(updatedPet.id)
            DataRefreshManager.notifyPetsUpdated()
            Result.success(updatedPet)
        } catch (e: Exception) {
            println("Repositório: Erro ao alternar favorito do pet '$id' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateHealthStatus(id: String, status: HealthStatus): Result<Pet> {
        return try {
            println("Repositório: Atualizando status de saúde do pet '$id' para '${status.displayName}' via API")
            val updatedPet = remoteDataSource.updateHealthStatus(id, status)
            println("Repositório: Status de saúde do pet '${updatedPet.name}' atualizado com sucesso")
            DataRefreshManager.notifyPetUpdated(updatedPet.id)
            DataRefreshManager.notifyPetsUpdated()
            Result.success(updatedPet)
        } catch (e: Exception) {
            println("Repositório: Erro ao atualizar status de saúde do pet '$id' - ${e.message}")
            Result.failure(e)
        }
    }

}