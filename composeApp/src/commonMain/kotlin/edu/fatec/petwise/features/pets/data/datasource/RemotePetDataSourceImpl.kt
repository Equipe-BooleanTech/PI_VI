package edu.fatec.petwise.features.pets.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.PetApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.pets.domain.models.HealthStatus
import edu.fatec.petwise.features.pets.domain.models.Pet

class RemotePetDataSourceImpl(
    private val petApiService: PetApiService
) : RemotePetDataSource {

    override suspend fun getAllPets(): List<Pet> {
        return when (val result = petApiService.getAllPets()) {
            is NetworkResult.Success -> result.data.pets.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getPetById(id: String): Pet? {
        return when (val result = petApiService.getPetById(id)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> {
                if (result.exception is edu.fatec.petwise.core.network.NetworkException.NotFound) {
                    null
                } else {
                    throw result.exception
                }
            }
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun createPet(pet: Pet): Pet {
        val request = CreatePetRequest(
            name = pet.name,
            breed = pet.breed,
            species = pet.species.name,
            gender = pet.gender.name,
            age = pet.age,
            weight = pet.weight,
            healthStatus = pet.healthStatus.name,
            ownerName = pet.ownerName,
            ownerPhone = pet.ownerPhone,
            healthHistory = pet.healthHistory,
            profileImageUrl = pet.profileImageUrl
        )

        return when (val result = petApiService.createPet(request)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun updatePet(pet: Pet): Pet {
        val request = UpdatePetRequest(
            name = pet.name,
            breed = pet.breed,
            age = pet.age,
            weight = pet.weight,
            healthStatus = pet.healthStatus.name,
            ownerName = pet.ownerName,
            ownerPhone = pet.ownerPhone,
            healthHistory = pet.healthHistory,
            profileImageUrl = pet.profileImageUrl
        )

        return when (val result = petApiService.updatePet(pet.id, request)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun deletePet(id: String) {
        when (val result = petApiService.deletePet(id)) {
            is NetworkResult.Success -> Unit
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    suspend fun toggleFavorite(id: String): Pet {
        return when (val result = petApiService.toggleFavorite(id)) {
            is NetworkResult.Success -> {
                getPetById(id) ?: throw Exception("Pet não encontrado após alteração de favorito")
            }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    suspend fun updateHealthStatus(id: String, status: HealthStatus, notes: String? = null): Pet {
        val request = UpdateHealthStatusRequest(
            healthStatus = status.name,
            notes = notes
        )

        return when (val result = petApiService.updateHealthStatus(id, request)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    suspend fun searchPets(query: String): List<Pet> {
        return when (val result = petApiService.searchPets(query)) {
            is NetworkResult.Success -> result.data.pets.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }

    suspend fun getFavoritePets(): List<Pet> {
        return when (val result = petApiService.getFavoritePets()) {
            is NetworkResult.Success -> result.data.pets.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }
}
