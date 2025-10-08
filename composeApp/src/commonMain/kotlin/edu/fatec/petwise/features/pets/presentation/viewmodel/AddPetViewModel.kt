package edu.fatec.petwise.features.pets.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.models.PetSpecies
import edu.fatec.petwise.features.pets.domain.models.PetGender
import edu.fatec.petwise.features.pets.domain.models.HealthStatus
import edu.fatec.petwise.features.pets.domain.usecases.AddPetUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddPetUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class AddPetUiEvent {
    data class AddPet(
        val name: String,
        val breed: String,
        val species: PetSpecies,
        val gender: PetGender,
        val age: String,
        val weight: String,
        val healthStatus: HealthStatus,
        val ownerName: String,
        val ownerPhone: String,
        val healthHistory: String
    ) : AddPetUiEvent()
    object ClearState : AddPetUiEvent()
}

class AddPetViewModel(
    private val addPetUseCase: AddPetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPetUiState())
    val uiState: StateFlow<AddPetUiState> = _uiState.asStateFlow()

    fun onEvent(event: AddPetUiEvent) {
        when (event) {
            is AddPetUiEvent.AddPet -> addPet(event)
            is AddPetUiEvent.ClearState -> clearState()
        }
    }

    private fun addPet(event: AddPetUiEvent.AddPet) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val ageInMonths = event.age.toIntOrNull()
                val weightInKg = event.weight.toFloatOrNull()

                if (ageInMonths == null || ageInMonths <= 0) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Idade deve ser um número válido maior que 0"
                    )
                    return@launch
                }

                if (weightInKg == null || weightInKg <= 0) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Peso deve ser um número válido maior que 0"
                    )
                    return@launch
                }

                val pet = Pet(
                    id = "",
                    name = event.name,
                    breed = event.breed,
                    species = event.species,
                    gender = event.gender,
                    age = ageInMonths,
                    weight = weightInKg,
                    healthStatus = event.healthStatus,
                    ownerName = event.ownerName,
                    ownerPhone = event.ownerPhone,
                    healthHistory = event.healthHistory,
                    isFavorite = false,
                    nextAppointment = null,
                    createdAt = "", 
                    updatedAt = ""
                )

                addPetUseCase(pet).fold(
                    onSuccess = { addedPet ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao adicionar pet"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    private fun clearState() {
        _uiState.value = AddPetUiState()
    }
}