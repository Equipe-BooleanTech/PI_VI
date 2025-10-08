package edu.fatec.petwise.features.pets.domain.usecases

import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.models.PetFilterOptions
import edu.fatec.petwise.features.pets.domain.repository.PetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPetsUseCase(
    private val repository: PetRepository
) {
    operator fun invoke(): Flow<List<Pet>> = repository.getAllPets()
    
    fun filterPets(options: PetFilterOptions): Flow<List<Pet>> = repository.filterPets(options)
    
    fun searchPets(query: String): Flow<List<Pet>> = repository.searchPets(query)
    
    fun getFavorites(): Flow<List<Pet>> = repository.getFavoritePets()
}

class GetPetByIdUseCase(
    private val repository: PetRepository
) {
    operator fun invoke(id: String): Flow<Pet?> = repository.getPetById(id)
}

class AddPetUseCase(
    private val repository: PetRepository
) {
    suspend operator fun invoke(pet: Pet): Result<Pet> {
        return if (validatePet(pet)) {
            repository.addPet(pet)
        } else {
            Result.failure(IllegalArgumentException("Pet data is invalid"))
        }
    }
    
    private fun validatePet(pet: Pet): Boolean {
        return pet.name.isNotBlank() && 
               pet.breed.isNotBlank() && 
               pet.ownerName.isNotBlank() && 
               pet.ownerPhone.isNotBlank() &&
               pet.age > 0 &&
               pet.weight > 0
    }
}

class UpdatePetUseCase(
    private val repository: PetRepository
) {
    suspend operator fun invoke(pet: Pet): Result<Pet> = repository.updatePet(pet)
}

class DeletePetUseCase(
    private val repository: PetRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deletePet(id)
}

class ToggleFavoriteUseCase(
    private val repository: PetRepository
) {
    suspend operator fun invoke(id: String): Result<Pet> = repository.toggleFavorite(id)
}

class UpdateHealthStatusUseCase(
    private val repository: PetRepository
) {
    suspend operator fun invoke(id: String, status: edu.fatec.petwise.features.pets.domain.models.HealthStatus): Result<Pet> {
        return repository.updateHealthStatus(id, status)
    }
}