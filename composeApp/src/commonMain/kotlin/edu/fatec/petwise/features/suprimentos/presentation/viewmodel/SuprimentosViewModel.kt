package edu.fatec.petwise.features.suprimentos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.features.suprimentos.domain.models.*
import edu.fatec.petwise.features.suprimentos.domain.usecases.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SuprimentosViewModel(
    private val getAllSuprimentosUseCase: GetAllSuprimentosUseCase,
    private val getSuprimentosByPetUseCase: GetSuprimentosByPetUseCase,
    private val getSuprimentosByCategoryUseCase: GetSuprimentosByCategoryUseCase,
    private val searchSuprimentosUseCase: SearchSuprimentosUseCase,
    private val filterSuprimentosUseCase: FilterSuprimentosUseCase,
    private val deleteSuprimentoUseCase: DeleteSuprimentoUseCase,
    private val getRecentSuprimentosUseCase: GetRecentSuprimentosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SuprimentosUiState())
    val uiState: StateFlow<SuprimentosUiState> = _uiState.asStateFlow()

    init {
        loadSuprimentos()
    }

    fun handleEvent(event: SuprimentosUiEvent) {
        when (event) {
            is SuprimentosUiEvent.LoadSuprimentos -> loadSuprimentos()
            is SuprimentosUiEvent.LoadSuprimentosByPet -> loadSuprimentosByPet(event.petId)
            is SuprimentosUiEvent.LoadSuprimentosByCategory -> loadSuprimentosByCategory(event.category)
            is SuprimentosUiEvent.SearchSuprimentos -> searchSuprimentos(event.criteria)
            is SuprimentosUiEvent.FilterSuprimentos -> filterSuprimentos(event.options)
            is SuprimentosUiEvent.DeleteSuprimento -> deleteSuprimento(event.id)
            is SuprimentosUiEvent.ShowSuprimentoDetails -> showSuprimentoDetails(event.suprimento)
            is SuprimentosUiEvent.HideSuprimentoDetails -> hideSuprimentoDetails()
            is SuprimentosUiEvent.ShowDeleteConfirmation -> showDeleteConfirmation(event.suprimento)
            is SuprimentosUiEvent.HideDeleteConfirmation -> hideDeleteConfirmation()
            is SuprimentosUiEvent.ClearError -> clearError()
            is SuprimentosUiEvent.LoadRecentSuprimentos -> loadRecentSuprimentos()
        }
    }

    private fun loadSuprimentos() {
        viewModelScope.launch {
            getAllSuprimentosUseCase().collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is NetworkResult.Success -> _uiState.value.copy(
                        isLoading = false,
                        suprimentos = result.data,
                        filteredSuprimentos = result.data,
                        errorMessage = null
                    )
                    is NetworkResult.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
            }
        }
    }

    private fun loadSuprimentosByPet(petId: String) {
        viewModelScope.launch {
            getSuprimentosByPetUseCase(petId).collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is NetworkResult.Success -> _uiState.value.copy(
                        isLoading = false,
                        suprimentos = result.data,
                        filteredSuprimentos = result.data,
                        errorMessage = null,
                        currentPetFilter = petId
                    )
                    is NetworkResult.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
            }
        }
    }

    private fun loadSuprimentosByCategory(category: SuprimentCategory) {
        viewModelScope.launch {
            getSuprimentosByCategoryUseCase(category).collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is NetworkResult.Success -> _uiState.value.copy(
                        isLoading = false,
                        suprimentos = result.data,
                        filteredSuprimentos = result.data,
                        errorMessage = null,
                        currentCategoryFilter = category
                    )
                    is NetworkResult.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
            }
        }
    }

    private fun searchSuprimentos(criteria: SuprimentoSearchCriteria) {
        viewModelScope.launch {
            searchSuprimentosUseCase(criteria).collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is NetworkResult.Success -> _uiState.value.copy(
                        isLoading = false,
                        filteredSuprimentos = result.data,
                        errorMessage = null,
                        currentSearchQuery = criteria.query
                    )
                    is NetworkResult.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
            }
        }
    }

    private fun filterSuprimentos(options: SuprimentoFilterOptions) {
        viewModelScope.launch {
            filterSuprimentosUseCase(options).collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is NetworkResult.Success -> _uiState.value.copy(
                        isLoading = false,
                        filteredSuprimentos = result.data,
                        errorMessage = null,
                        currentFilterOptions = options
                    )
                    is NetworkResult.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
            }
        }
    }

    private fun deleteSuprimento(id: String) {
        viewModelScope.launch {
            deleteSuprimentoUseCase(id).collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is NetworkResult.Success -> {
                        hideDeleteConfirmation()
                        val updatedSuprimentos = _uiState.value.suprimentos.filter { it.id != id }
                        val updatedFiltered = _uiState.value.filteredSuprimentos.filter { it.id != id }
                        _uiState.value.copy(
                            isLoading = false,
                            suprimentos = updatedSuprimentos,
                            filteredSuprimentos = updatedFiltered,
                            errorMessage = null,
                            successMessage = "Suprimento excluído com sucesso"
                        )
                    }
                    is NetworkResult.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
            }
        }
    }

    private fun loadRecentSuprimentos() {
        viewModelScope.launch {
            getRecentSuprimentosUseCase(10).collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is NetworkResult.Success -> _uiState.value.copy(
                        isLoading = false,
                        suprimentos = result.data,
                        filteredSuprimentos = result.data,
                        errorMessage = null
                    )
                    is NetworkResult.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
            }
        }
    }

    private fun showSuprimentoDetails(suprimento: Suprimento) {
        _uiState.value = _uiState.value.copy(
            selectedSuprimento = suprimento,
            showSuprimentoDetails = true
        )
    }

    private fun hideSuprimentoDetails() {
        _uiState.value = _uiState.value.copy(
            selectedSuprimento = null,
            showSuprimentoDetails = false
        )
    }

    private fun showDeleteConfirmation(suprimento: Suprimento) {
        _uiState.value = _uiState.value.copy(
            suprimentoToDelete = suprimento,
            showDeleteConfirmation = true
        )
    }

    private fun hideDeleteConfirmation() {
        _uiState.value = _uiState.value.copy(
            suprimentoToDelete = null,
            showDeleteConfirmation = false
        )
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(
            successMessage = null
        )
    }
}


data class SuprimentosUiState(
    val isLoading: Boolean = false,
    val suprimentos: List<Suprimento> = emptyList(),
    val filteredSuprimentos: List<Suprimento> = emptyList(),
    val selectedSuprimento: Suprimento? = null,
    val suprimentoToDelete: Suprimento? = null,
    val showSuprimentoDetails: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val currentSearchQuery: String = "",
    val currentPetFilter: String? = null,
    val currentCategoryFilter: SuprimentCategory? = null,
    val currentFilterOptions: SuprimentoFilterOptions? = null
)

sealed class SuprimentosUiEvent {
    object LoadSuprimentos : SuprimentosUiEvent()
    object LoadRecentSuprimentos : SuprimentosUiEvent()
    data class LoadSuprimentosByPet(val petId: String) : SuprimentosUiEvent()
    data class LoadSuprimentosByCategory(val category: SuprimentCategory) : SuprimentosUiEvent()
    data class SearchSuprimentos(val criteria: SuprimentoSearchCriteria) : SuprimentosUiEvent()
    data class FilterSuprimentos(val options: SuprimentoFilterOptions) : SuprimentosUiEvent()
    data class DeleteSuprimento(val id: String) : SuprimentosUiEvent()
    data class ShowSuprimentoDetails(val suprimento: Suprimento) : SuprimentosUiEvent()
    object HideSuprimentoDetails : SuprimentosUiEvent()
    data class ShowDeleteConfirmation(val suprimento: Suprimento) : SuprimentosUiEvent()
    object HideDeleteConfirmation : SuprimentosUiEvent()
    object ClearError : SuprimentosUiEvent()
}