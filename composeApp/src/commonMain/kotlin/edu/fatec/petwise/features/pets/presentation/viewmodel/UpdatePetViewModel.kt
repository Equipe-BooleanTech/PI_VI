package edu.fatec.petwise.features.pets.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.models.PetSpecies
import edu.fatec.petwise.features.pets.domain.models.PetGender
import edu.fatec.petwise.features.pets.domain.models.HealthStatus
import edu.fatec.petwise.features.pets.domain.usecases.UpdatePetUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UpdatePetUiState(
    val pet: Pet? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class UpdatePetUiEvent {
    data class LoadPet(val pet: Pet) : UpdatePetUiEvent()
    data class UpdatePet(
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
    ) : UpdatePetUiEvent()
    object ClearState : UpdatePetUiEvent()
}

class UpdatePetViewModel(
    private val updatePetUseCase: UpdatePetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdatePetUiState())
    val uiState: StateFlow<UpdatePetUiState> = _uiState.asStateFlow()

    init {
        observeLogout()
    }

    private fun observeLogout() {
        viewModelScope.launch {
            edu.fatec.petwise.core.data.DataRefreshManager.refreshEvents.collect { event ->
                if (event is edu.fatec.petwise.core.data.DataRefreshEvent.UserLoggedOut) {
                    println("UpdatePetViewModel: Usuário deslogou — limpando estado")
                    clearState()
                }
            }
        }
    }

    fun onEvent(event: UpdatePetUiEvent) {
        when (event) {
            is UpdatePetUiEvent.LoadPet -> loadPet(event.pet)
            is UpdatePetUiEvent.UpdatePet -> updatePet(event)
            is UpdatePetUiEvent.ClearState -> clearState()
        }
    }

    private fun loadPet(pet: Pet) {
        println("Carregando pet para edição: ${pet.name}")
        _uiState.value = _uiState.value.copy(pet = pet)
    }

    private fun updatePet(event: UpdatePetUiEvent.UpdatePet) {
        viewModelScope.launch {
            println("Iniciando atualização do pet...")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val currentPet = _uiState.value.pet
                if (currentPet == null) {
                    println("Erro: Pet não encontrado para atualização")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Pet não encontrado"
                    )
                    return@launch
                }

                val ageInMonths = event.age.toIntOrNull()
                val weightInKg = event.weight.toFloatOrNull()

                if (ageInMonths == null || ageInMonths <= 0) {
                    println("Erro de validação: Idade inválida - ${event.age}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Idade deve ser um número válido maior que 0"
                    )
                    return@launch
                }

                if (weightInKg == null || weightInKg <= 0) {
                    println("Erro de validação: Peso inválido - ${event.weight}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Peso deve ser um número válido maior que 0"
                    )
                    return@launch
                }

                val updatedPet = currentPet.copy(
                    name = event.name,
                    breed = event.breed,
                    species = event.species,
                    gender = event.gender,
                    age = ageInMonths,
                    weight = weightInKg,
                    healthStatus = event.healthStatus,
                    ownerName = event.ownerName,
                    ownerPhone = event.ownerPhone,
                    healthHistory = event.healthHistory
                )

                println("Atualizando pet com dados: nome=${updatedPet.name}, raça=${updatedPet.breed}")

                updatePetUseCase(updatedPet).fold(
                    onSuccess = { result ->
                        println("Pet atualizado com sucesso: ${result.name}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null,
                            pet = result
                        )
                    },
                    onFailure = { error ->
                        println("Erro ao atualizar pet: ${error.message}")
                        
                        val errorMessage = when {
                            error.message?.contains("Token expirado") == true ||
                            error.message?.contains("Sessão expirada") == true ||
                            error.message?.contains("deve estar logado") == true -> {
                                println("UpdatePetViewModel: Erro de autenticação detectado - ${error.message}")
                                "Sua sessão expirou. Faça login novamente para atualizar pets."
                            }
                            else -> error.message ?: "Erro ao atualizar pet"
                        }
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = errorMessage
                        )
                    }
                )
            } catch (e: Exception) {
                println("Exceção durante atualização do pet: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    private fun clearState() {
        println("Limpando estado do UpdatePetViewModel")
        _uiState.value = UpdatePetUiState()
    }
}