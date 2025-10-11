package edu.fatec.petwise.features.pets.data.datasource

import edu.fatec.petwise.features.pets.domain.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class LocalPetDataSourceImpl : LocalPetDataSource {
    private val _pets = MutableStateFlow(generateMockPets())

    override fun getAllPets(): Flow<List<Pet>> = _pets

    override fun getPetById(id: String): Flow<Pet?> = _pets.map { pets ->
        pets.find { it.id == id }
    }

    override suspend fun insertPet(pet: Pet) {
        _pets.value = _pets.value + pet
    }

    override suspend fun updatePet(pet: Pet) {
        _pets.value = _pets.value.map { existingPet ->
            if (existingPet.id == pet.id) pet else existingPet
        }
    }

    override suspend fun deletePet(id: String) {
        _pets.value = _pets.value.filter { it.id != id }
    }

    private fun generateMockPets(): List<Pet> {
        return listOf(
            Pet(
                id = "1",
                name = "Max",
                breed = "Golden Retriever",
                species = PetSpecies.DOG,
                gender = PetGender.MALE,
                age = 36,
                weight = 28.0f,
                healthStatus = HealthStatus.GOOD,
                ownerName = "João Silva",
                ownerPhone = "(11) 99999-1234",
                healthHistory = "Histórico de alergias alimentares. Vacinação em dia.",
                isFavorite = true,
                nextAppointment = "09/11/2024",
                createdAt = "2024-01-15T10:00:00Z",
                updatedAt = "2024-10-08T10:00:00Z"
            ),
            Pet(
                id = "2",
                name = "Luna",
                breed = "Siamês",
                species = PetSpecies.CAT,
                gender = PetGender.FEMALE,
                age = 24,
                weight = 4.0f,
                healthStatus = HealthStatus.EXCELLENT,
                ownerName = "Maria Santos",
                ownerPhone = "(11) 99999-5678",
                healthHistory = "Sem problemas de saúde conhecidos. Castrada.",
                isFavorite = false,
                nextAppointment = "15/11/2024",
                createdAt = "2024-02-20T14:30:00Z",
                updatedAt = "2024-10-08T14:30:00Z"
            ),
            Pet(
                id = "3",
                name = "Buddy",
                breed = "Vira-lata",
                species = PetSpecies.DOG,
                gender = PetGender.MALE,
                age = 48,
                weight = 15.5f,
                healthStatus = HealthStatus.ATTENTION,
                ownerName = "Carlos Oliveira",
                ownerPhone = "(11) 99999-9012",
                healthHistory = "Problemas cardíacos leves. Requer medicação diária.",
                isFavorite = true,
                nextAppointment = "12/11/2024",
                createdAt = "2024-03-10T09:15:00Z",
                updatedAt = "2024-10-08T09:15:00Z"
            ),
            Pet(
                id = "4",
                name = "Bella",
                breed = "Poodle",
                species = PetSpecies.DOG,
                gender = PetGender.FEMALE,
                age = 72,
                weight = 12.0f,
                healthStatus = HealthStatus.REGULAR,
                ownerName = "Ana Costa",
                ownerPhone = "(11) 99999-3456",
                healthHistory = "Artrite nas patas traseiras. Tratamento em andamento.",
                isFavorite = false,
                nextAppointment = "20/11/2024",
                createdAt = "2024-01-05T16:45:00Z",
                updatedAt = "2024-10-08T16:45:00Z"
            )
        )
    }
}