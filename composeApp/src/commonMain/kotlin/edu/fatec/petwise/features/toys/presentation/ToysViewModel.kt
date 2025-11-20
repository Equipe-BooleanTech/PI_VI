package edu.fatec.petwise.features.toys.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.toys.di.ToyDependencyContainer
import edu.fatec.petwise.features.toys.domain.models.Toy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ToysUiState(
    val toys: List<Toy> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null
)

sealed class ToysUiEvent {
    object LoadToys : ToysUiEvent()
    data class SearchToys(val query: String) : ToysUiEvent()
    data class FilterByCategory(val category: String?) : ToysUiEvent()
    data class AddToy(val toy: Toy) : ToysUiEvent()
    data class UpdateToy(val toy: Toy) : ToysUiEvent()
    data class DeleteToy(val toyId: String) : ToysUiEvent()
}

class ToysViewModel : ViewModel() {

    private val getToysUseCase = ToyDependencyContainer.getToysUseCase
    private val addToyUseCase = ToyDependencyContainer.addToyUseCase
    private val updateToyUseCase = ToyDependencyContainer.updateToyUseCase
    private val deleteToyUseCase = ToyDependencyContainer.deleteToyUseCase

    private val _uiState = MutableStateFlow(ToysUiState())
    val uiState: StateFlow<ToysUiState> = _uiState.asStateFlow()

    init {
        loadToys()
    }

    fun onEvent(event: ToysUiEvent) {
        when (event) {
            is ToysUiEvent.LoadToys -> loadToys()
            is ToysUiEvent.SearchToys -> searchToys(event.query)
            is ToysUiEvent.FilterByCategory -> filterByCategory(event.category)
            is ToysUiEvent.AddToy -> addToy(event.toy)
            is ToysUiEvent.UpdateToy -> updateToy(event.toy)
            is ToysUiEvent.DeleteToy -> deleteToy(event.toyId)
        }
    }

    private fun loadToys() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                getToysUseCase().collectLatest { toys ->
                    _uiState.value = _uiState.value.copy(
                        toys = toys,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao carregar brinquedos"
                )
            }
        }
    }

    private fun searchToys(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(searchQuery = query, isLoading = true)

            try {
                if (query.isEmpty()) {
                    getToysUseCase().collectLatest { toys ->
                        _uiState.value = _uiState.value.copy(
                            toys = toys,
                            isLoading = false
                        )
                    }
                } else {
                    getToysUseCase.searchToys(query).collectLatest { toys ->
                        _uiState.value = _uiState.value.copy(
                            toys = toys,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao buscar brinquedos"
                )
            }
        }
    }

    private fun filterByCategory(category: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedCategory = category, isLoading = true)

            try {
                if (category == null) {
                    getToysUseCase().collectLatest { toys ->
                        _uiState.value = _uiState.value.copy(
                            toys = toys,
                            isLoading = false
                        )
                    }
                } else {
                    getToysUseCase.getToysByCategory(category).collectLatest { toys ->
                        _uiState.value = _uiState.value.copy(
                            toys = toys,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao filtrar brinquedos"
                )
            }
        }
    }

    private fun addToy(toy: Toy) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                addToyUseCase(toy).fold(
                    onSuccess = { newToy ->
                        // Reload toys to get updated list
                        loadToys()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao adicionar brinquedo"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao adicionar brinquedo"
                )
            }
        }
    }

    private fun updateToy(toy: Toy) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                updateToyUseCase(toy).fold(
                    onSuccess = { updatedToy ->
                        // Reload toys to get updated list
                        loadToys()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao atualizar brinquedo"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao atualizar brinquedo"
                )
            }
        }
    }

    private fun deleteToy(toyId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                deleteToyUseCase(toyId).fold(
                    onSuccess = {
                        // Reload toys to get updated list
                        loadToys()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao excluir brinquedo"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao excluir brinquedo"
                )
            }
        }
    }
}