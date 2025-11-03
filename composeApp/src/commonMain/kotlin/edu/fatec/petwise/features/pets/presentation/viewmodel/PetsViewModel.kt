package edu.fatec.petwise.features.pets.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.core.data.DataRefreshManager
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
        observeDataRefresh()
    }

    private fun observeDataRefresh() {
    viewModelScope.launch {
        DataRefreshManager.refreshEvents.collect { event ->
            when (event) {
                is DataRefreshEvent.PetsUpdated -> loadPets()
                is DataRefreshEvent.AllDataUpdated -> {
                    _uiState.value = PetsUiState()
                    println("PetsViewModel: Estado limpo após logout")
                }
                else -> {}
            }
        }
    }
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
            println("Iniciando carregamento de pets...")
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                getPetsUseCase().collect { pets ->
                    println("Pets carregados com sucesso: ${pets.size} pets encontrados")
                    _uiState.value = _uiState.value.copy(
                        pets = pets,
                        filteredPets = pets,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                println("Erro ao carregar pets: ${e.message}")
                
                val errorMessage = when {
                    e.message?.contains("Token expirado") == true ||
                    e.message?.contains("Sessão expirada") == true ||
                    e.message?.contains("deve estar logado") == true -> {
                        println("PetsViewModel: Erro de autenticação detectado - ${e.message}")
                        "Sua sessão expirou. Faça login novamente."
                    }
                    else -> e.message ?: "Erro desconhecido"
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage
                )
            }
        }
    }

    private fun searchPets(query: String) {
        println("Iniciando busca de pets com query: '$query'")
        _uiState.value = _uiState.value.copy(searchQuery = query)

        if (query.isBlank()) {
            println("Query vazia, exibindo todos os pets")
            _uiState.value = _uiState.value.copy(filteredPets = _uiState.value.pets)
            return
        }

        val petsFiltrados = _uiState.value.pets.filter { pet ->
            pet.name.contains(query, ignoreCase = true) ||
            pet.breed.contains(query, ignoreCase = true) ||
            pet.ownerName.contains(query, ignoreCase = true)
        }
        
        println("Busca concluída: ${petsFiltrados.size} pets encontrados para '$query'")
        _uiState.value = _uiState.value.copy(
            filteredPets = petsFiltrados,
            errorMessage = null
        )
    }

    private fun filterPets(options: PetFilterOptions) {
        _uiState.value = _uiState.value.copy(filterOptions = options)

        val petsFiltrados = _uiState.value.pets.filter { pet ->
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
        
        _uiState.value = _uiState.value.copy(filteredPets = petsFiltrados)
    }

    private fun toggleFavorite(petId: String) {
        viewModelScope.launch {
            println("Alterando status de favorito para pet ID: $petId")
            try {
                toggleFavoriteUseCase(petId).fold(
                    onSuccess = { updatedPet ->
                        println("Status de favorito alterado com sucesso para pet: ${updatedPet.name}")
                        loadPets()
                    },
                    onFailure = { error ->
                        println("Erro ao alterar favorito: ${error.message}")
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message ?: "Erro ao favoritar pet"
                        )
                    }
                )
            } catch (e: Exception) {
                println("Exceção ao alterar favorito: ${e.message}")
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
                        println("Status de saúde atualizado com sucesso para pet: ${updatedPet.name}")
                        loadPets()
                    },
                    onFailure = { error ->
                        println("Erro ao atualizar status de saúde: ${error.message}")
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message ?: "Erro ao atualizar status"
                        )
                    }
                )
            } catch (e: Exception) {
                println("Exceção ao atualizar status de saúde: ${e.message}")
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
            println("Iniciando exclusão do pet ID: $petId")
            try {
                deletePetUseCase(petId).fold(
                    onSuccess = {
                        println("Pet excluído com sucesso: $petId")
                        loadPets()
                    },
                    onFailure = { error ->
                        println("Erro ao excluir pet: ${error.message}")
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message ?: "Erro ao deletar pet"
                        )
                    }
                )
            } catch (e: Exception) {
                println("Exceção durante exclusão do pet: ${e.message}")
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