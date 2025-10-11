package edu.fatec.petwise.features.pets.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.models.PetFilterOptions
import edu.fatec.petwise.features.pets.domain.models.HealthStatus
import edu.fatec.petwise.features.pets.domain.usecases.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class PetsUiState(
    val pets: List<Pet> = emptyList(),
    val filteredPets: List<Pet> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val filterOptions: PetFilterOptions = PetFilterOptions(),
    val searchQuery: String = "",
    val showAddPetDialog: Boolean = false,
    val selectedPet: Pet? = null
)

sealed class PetsUiEvent {
    object LoadPets : PetsUiEvent()
    data class SearchPets(val query: String) : PetsUiEvent()
    data class FilterPets(val options: PetFilterOptions) : PetsUiEvent()
    data class ToggleFavorite(val petId: String) : PetsUiEvent()
    data class UpdateHealthStatus(val petId: String, val status: HealthStatus) : PetsUiEvent()
    data class SelectPet(val pet: Pet?) : PetsUiEvent()
    data class DeletePet(val petId: String) : PetsUiEvent()
    object ShowAddPetDialog : PetsUiEvent()
    object HideAddPetDialog : PetsUiEvent()
    object ClearError : PetsUiEvent()
}

class PetsViewModel(
    private val getPetsUseCase: GetPetsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val updateHealthStatusUseCase: UpdateHealthStatusUseCase,
    private val deletePetUseCase: DeletePetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetsUiState())
    val uiState: StateFlow<PetsUiState> = _uiState.asStateFlow()

    init {
        loadPets()
    }

    fun onEvent(event: PetsUiEvent) {
        when (event) {
            is PetsUiEvent.LoadPets -> loadPets()
            is PetsUiEvent.SearchPets -> searchPets(event.query)
            is PetsUiEvent.FilterPets -> filterPets(event.options)
            is PetsUiEvent.ToggleFavorite -> toggleFavorite(event.petId)
            is PetsUiEvent.UpdateHealthStatus -> updateHealthStatus(event.petId, event.status)
            is PetsUiEvent.SelectPet -> selectPet(event.pet)
            is PetsUiEvent.DeletePet -> deletePet(event.petId)
            is PetsUiEvent.ShowAddPetDialog -> showAddPetDialog()
            is PetsUiEvent.HideAddPetDialog -> hideAddPetDialog()
            is PetsUiEvent.ClearError -> clearError()
        }
    }

    private fun loadPets() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                getPetsUseCase().collect { pets ->
                    _uiState.value = _uiState.value.copy(
                        pets = pets,
                        filteredPets = pets,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    private fun searchPets(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(filteredPets = _uiState.value.pets)
            return
        }

        viewModelScope.launch {
            try {
                getPetsUseCase.searchPets(query).collect { pets ->
                    _uiState.value = _uiState.value.copy(
                        filteredPets = pets,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erro ao buscar pets: ${e.message}"
                )
            }
        }
    }

    private fun filterPets(options: PetFilterOptions) {
        _uiState.value = _uiState.value.copy(filterOptions = options)

        viewModelScope.launch {
            try {
                getPetsUseCase.filterPets(options).collect { pets ->
                    _uiState.value = _uiState.value.copy(filteredPets = pets)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Erro ao filtrar pets"
                )
            }
        }
    }

    private fun toggleFavorite(petId: String) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(petId).fold(
                    onSuccess = { updatedPet ->

                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message ?: "Erro ao favoritar pet"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Erro ao favoritar pet"
                )
            }
        }
    }

    private fun updateHealthStatus(petId: String, status: HealthStatus) {
        viewModelScope.launch {
            try {
                updateHealthStatusUseCase(petId, status).fold(
                    onSuccess = { updatedPet ->
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message ?: "Erro ao atualizar status"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Erro ao atualizar status"
                )
            }
        }
    }

    private fun selectPet(pet: Pet?) {
        _uiState.value = _uiState.value.copy(selectedPet = pet)
    }

    private fun deletePet(petId: String) {
        viewModelScope.launch {
            try {
                deletePetUseCase(petId).fold(
                    onSuccess = {

                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message ?: "Erro ao deletar pet"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Erro ao deletar pet"
                )
            }
        }
    }

    private fun showAddPetDialog() {
        _uiState.value = _uiState.value.copy(showAddPetDialog = true)
    }

    private fun hideAddPetDialog() {
        _uiState.value = _uiState.value.copy(showAddPetDialog = false)
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}