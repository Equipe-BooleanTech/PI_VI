package edu.fatec.petwise.features.pets.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.PetApiService
import edu.fatec.petwise.core.network.api.AuthApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.pets.domain.models.HealthStatus
import edu.fatec.petwise.features.pets.domain.models.Pet

class RemotePetDataSourceImpl(
    private val petApiService: PetApiService,
    private val authApiService: AuthApiService
) : RemotePetDataSource {

    override suspend fun getAllPets(): List<Pet> { 
        println("API: Buscando todos os pets")
        return when (val result = petApiService.getAllPets()) {
            is NetworkResult.Success -> {
                println("API: ${result.data.pets.size} pets obtidos com sucesso")
                result.data.pets.map { it.toDomain() }
            }
            is NetworkResult.Error -> {
                println("API: Erro ao buscar pets - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getPetById(id: String): Pet? {
        println("API: Buscando pet por ID: $id")
        return when (val result = petApiService.getPetById(id)) {
            is NetworkResult.Success -> {
                println("API: Pet encontrado - ${result.data.name}")
                result.data.toDomain()
            }
            is NetworkResult.Error -> {
                if (result.exception is edu.fatec.petwise.core.network.NetworkException.NotFound) {
                    println("API: Pet não encontrado")
                    null
                } else {
                    println("API: Erro ao buscar pet - ${result.exception.message}")
                    throw result.exception
                }
            }
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun createPet(pet: Pet): Pet {
        println("API: Criando novo pet - ${pet.name}")
        
        val userProfile = try {
            kotlinx.coroutines.withContext(kotlinx.coroutines.NonCancellable) {
                when (val result = authApiService.getUserProfile()) {
                    is NetworkResult.Success -> result.data
                    is NetworkResult.Error -> {
                        when (result.exception) {
                            is edu.fatec.petwise.core.network.NetworkException.Unauthorized -> {
                                println("API: Usuário não autenticado - operação não permitida")
                                throw edu.fatec.petwise.core.network.NetworkException.Unauthorized(
                                    message = "Usuário deve estar logado para criar pets",
                                    shouldRefreshToken = false
                                )
                            }
                            else -> {
                                println("API: Erro ao buscar perfil do usuário - ${result.exception.message}")
                                throw result.exception
                            }
                        }
                    }
                    is NetworkResult.Loading -> {
                        throw IllegalStateException("Busca de perfil em andamento")
                    }
                }
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            println("API: Operação de criação de pet cancelada")
            throw edu.fatec.petwise.core.network.NetworkException.Unauthorized(
                message = "Operação cancelada. Faça login novamente.",
                shouldRefreshToken = false
            )
        }
        
        val request = CreatePetRequest(
            name = pet.name,
            breed = pet.breed,
            species = pet.species.name,
            gender = pet.gender.name,
            age = pet.age,
            weight = pet.weight,
            healthStatus = pet.healthStatus.name,
            ownerName = userProfile.fullName,
            ownerPhone = userProfile.phone ?: "",
            healthHistory = pet.healthHistory,
            profileImageUrl = pet.profileImageUrl
        )

        return when (val result = petApiService.createPet(request)) {
            is NetworkResult.Success -> {
                println("API: Pet criado com sucesso - ID: ${result.data.id}")
                result.data.toDomain()
            }
            is NetworkResult.Error -> {
                println("API: Erro ao criar pet - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun updatePet(pet: Pet): Pet {
        println("API: Atualizando pet - ${pet.name} (ID: ${pet.id})")
        
        val userProfile = try {
            kotlinx.coroutines.withContext(kotlinx.coroutines.NonCancellable) {
                when (val result = authApiService.getUserProfile()) {
                    is NetworkResult.Success -> result.data
                    is NetworkResult.Error -> {
                        when (result.exception) {
                            is edu.fatec.petwise.core.network.NetworkException.Unauthorized -> {
                                println("API: Usuário não autenticado - operação não permitida")
                                throw edu.fatec.petwise.core.network.NetworkException.Unauthorized(
                                    message = "Usuário deve estar logado para atualizar pets",
                                    shouldRefreshToken = false
                                )
                            }
                            else -> {
                                println("API: Erro ao buscar perfil do usuário - ${result.exception.message}")
                                throw result.exception
                            }
                        }
                    }
                    is NetworkResult.Loading -> {
                        throw IllegalStateException("Busca de perfil em andamento")
                    }
                }
            }
        } catch (e: kotlinx.coroutines.CancellationException) {
            println("API: Operação de atualização de pet cancelada")
            throw edu.fatec.petwise.core.network.NetworkException.Unauthorized(
                message = "Operação cancelada. Faça login novamente.",
                shouldRefreshToken = false
            )
        }
        
        val request = UpdatePetRequest(
            name = pet.name,
            breed = pet.breed,
            species = pet.species.name,
            gender = pet.gender.name,
            age = pet.age,
            weight = pet.weight,
            healthStatus = pet.healthStatus.name,
            ownerName = userProfile.fullName,
            ownerPhone = userProfile.phone ?: "",
            healthHistory = pet.healthHistory,
            profileImageUrl = pet.profileImageUrl
        )

        return when (val result = petApiService.updatePet(pet.id, request)) {
            is NetworkResult.Success -> {
                println("API: Pet atualizado com sucesso")
                result.data.toDomain()
            }
            is NetworkResult.Error -> {
                println("API: Erro ao atualizar pet - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun deletePet(id: String) {
        println("API: Excluindo pet com ID: $id")
        when (val result = petApiService.deletePet(id)) {
            is NetworkResult.Success -> {
                println("API: Pet excluído com sucesso")
                Unit
            }
            is NetworkResult.Error -> {
                println("API: Erro ao excluir pet - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun toggleFavorite(id: String): Pet {
        println("API: Alterando favorito do pet ID: $id")
        return when (val result = petApiService.toggleFavorite(id)) {
            is NetworkResult.Success -> {
                println("API: Favorito alterado, buscando pet atualizado")
                getPetById(id) ?: throw Exception("Pet não encontrado após alteração de favorito")
            }
            is NetworkResult.Error -> {
                println("API: Erro ao alterar favorito - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun updateHealthStatus(id: String, status: HealthStatus, notes: String?): Pet {
        println("API: Atualizando status de saúde do pet ID: $id para ${status.displayName}")
        val request = UpdateHealthStatusRequest(
            healthStatus = status.name,
            notes = notes
        )

        return when (val result = petApiService.updateHealthStatus(id, request)) {
            is NetworkResult.Success -> {
                println("API: Status de saúde atualizado com sucesso")
                result.data.toDomain()
            }
            is NetworkResult.Error -> {
                println("API: Erro ao atualizar status de saúde - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun searchPets(query: String): List<Pet> {
        println("API: Buscando pets com query: '$query'")
        return when (val result = petApiService.searchPets(query)) {
            is NetworkResult.Success -> {
                println("API: Busca concluída - ${result.data.pets.size} pets encontrados")
                result.data.pets.map { it.toDomain() }
            }
            is NetworkResult.Error -> {
                println("API: Erro na busca - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getFavoritePets(): List<Pet> {
        println("API: Buscando pets favoritos")
        return when (val result = petApiService.getFavoritePets()) {
            is NetworkResult.Success -> {
                println("API: Pets favoritos obtidos - ${result.data.pets.size} pets")
                result.data.pets.map { it.toDomain() }
            }
            is NetworkResult.Error -> {
                println("API: Erro ao buscar favoritos - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> emptyList()
        }
    }
}
