package edu.fatec.petwise.features.food.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.food.di.FoodDependencyContainer
import edu.fatec.petwise.features.food.domain.models.Food
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class FoodUiState(
    val foods: List<Food> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null
)

sealed class FoodUiEvent {
    object LoadFoods : FoodUiEvent()
    data class SearchFoods(val query: String) : FoodUiEvent()
    data class FilterByCategory(val category: String?) : FoodUiEvent()
    data class AddFood(val food: Food) : FoodUiEvent()
    data class UpdateFood(val food: Food) : FoodUiEvent()
    data class DeleteFood(val foodId: String) : FoodUiEvent()
}

class FoodViewModel : ViewModel() {

    private val getFoodUseCase = FoodDependencyContainer.getFoodUseCase
    private val addFoodUseCase = FoodDependencyContainer.addFoodUseCase
    private val updateFoodUseCase = FoodDependencyContainer.updateFoodUseCase
    private val deleteFoodUseCase = FoodDependencyContainer.deleteFoodUseCase

    private val _uiState = MutableStateFlow(FoodUiState())
    val uiState: StateFlow<FoodUiState> = _uiState.asStateFlow()

    private var currentDataJob: kotlinx.coroutines.Job? = null

    init {
        loadFoods()
    }

    override fun onCleared() {
        super.onCleared()
        currentDataJob?.cancel()
    }

    fun onEvent(event: FoodUiEvent) {
        when (event) {
            is FoodUiEvent.LoadFoods -> loadFoods()
            is FoodUiEvent.SearchFoods -> searchFoods(event.query)
            is FoodUiEvent.FilterByCategory -> filterByCategory(event.category)
            is FoodUiEvent.AddFood -> addFood(event.food)
            is FoodUiEvent.UpdateFood -> updateFood(event.food)
            is FoodUiEvent.DeleteFood -> deleteFood(event.foodId)
        }
    }

    private fun loadFoods() {
        // Cancel any existing data loading job
        currentDataJob?.cancel()

        currentDataJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                getFoodUseCase().collectLatest { foods ->
                    _uiState.value = _uiState.value.copy(
                        foods = foods,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Erro ao carregar alimentos"
                    )
                }
            }
        }
    }



    private fun searchFoods(query: String) {
        // Cancel any existing data loading job
        currentDataJob?.cancel()

        currentDataJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(searchQuery = query, isLoading = true)

            try {
                if (query.isEmpty()) {
                    getFoodUseCase().collectLatest { foods ->
                        _uiState.value = _uiState.value.copy(
                            foods = foods,
                            isLoading = false
                        )
                    }
                } else {
                    getFoodUseCase.searchFood(query).collectLatest { foods ->
                        _uiState.value = _uiState.value.copy(
                            foods = foods,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Erro ao buscar alimentos"
                    )
                }
            }
        }
    }

    private fun filterByCategory(category: String?) {
        // Cancel any existing data loading job
        currentDataJob?.cancel()

        currentDataJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(selectedCategory = category, isLoading = true)

            try {
                if (category == null) {
                    getFoodUseCase().collectLatest { foods ->
                        _uiState.value = _uiState.value.copy(
                            foods = foods,
                            isLoading = false
                        )
                    }
                } else {
                    getFoodUseCase.getFoodByCategory(category).collectLatest { foods ->
                        _uiState.value = _uiState.value.copy(
                            foods = foods,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Erro ao filtrar alimentos"
                    )
                }
            }
        }
    }

    private fun addFood(food: Food) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                addFoodUseCase(food).fold(
                    onSuccess = { newFood ->
                        // Reload foods to get updated list
                        loadFoods()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao adicionar alimento"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao adicionar alimento"
                )
            }
        }
    }

    private fun updateFood(food: Food) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                updateFoodUseCase(food).fold(
                    onSuccess = { updatedFood ->
                        // Reload foods to get updated list
                        loadFoods()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao atualizar alimento"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao atualizar alimento"
                )
            }
        }
    }

    private fun deleteFood(foodId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                deleteFoodUseCase(foodId).fold(
                    onSuccess = {
                        // Reload foods to get updated list
                        loadFoods()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao excluir alimento"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao excluir alimento"
                )
            }
        }
    }
}