package edu.fatec.petwise.features.pets.data.datasource

import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.models.PetFilterOptions
import kotlinx.coroutines.flow.Flow

interface LocalPetDataSource {
    fun getAllPets(): Flow<List<Pet>>
    fun getPetById(id: String): Flow<Pet?>
    suspend fun insertPet(pet: Pet)
    suspend fun updatePet(pet: Pet)
    suspend fun deletePet(id: String)
}

interface RemotePetDataSource {
    suspend fun getAllPets(): List<Pet>
    suspend fun getPetById(id: String): Pet?
    suspend fun createPet(pet: Pet): Pet
    suspend fun updatePet(pet: Pet): Pet
    suspend fun deletePet(id: String)
}