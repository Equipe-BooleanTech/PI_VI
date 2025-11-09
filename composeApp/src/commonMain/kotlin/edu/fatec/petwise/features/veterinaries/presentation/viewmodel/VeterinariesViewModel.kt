package edu.fatec.petwise.features.veterinaries.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.veterinaries.domain.models.Veterinary
import edu.fatec.petwise.features.veterinaries.domain.models.VeterinaryFilterOptions
import edu.fatec.petwise.features.veterinaries.domain.usecases.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class VeterinariesUiState(
    val veterinaries: List<Veterinary> = emptyList(),
    val filteredVeterinaries: List<Veterinary> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val filterOptions: VeterinaryFilterOptions = VeterinaryFilterOptions(),
    val searchQuery: String = "",
    val selectedVeterinary: Veterinary? = null,
    val showVeterinaryDetails: Boolean = false
)

sealed class VeterinariesUiEvent {
    object LoadVeterinaries : VeterinariesUiEvent()
    data class SearchVeterinaries(val query: String) : VeterinariesUiEvent()
    data class FilterVeterinaries(val options: VeterinaryFilterOptions) : VeterinariesUiEvent()
    data class SelectVeterinary(val veterinary: Veterinary?) : VeterinariesUiEvent()
    object ShowVeterinaryDetails : VeterinariesUiEvent()
    object HideVeterinaryDetails : VeterinariesUiEvent()
    object ClearError : VeterinariesUiEvent()
}

class VeterinariesViewModel(
    private val veterinaryUseCases: VeterinaryUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(VeterinariesUiState())
    val uiState: StateFlow<VeterinariesUiState> = _uiState.asStateFlow()

    init {
        loadVeterinaries()
    }

    fun handleEvent(event: VeterinariesUiEvent) {
        when (event) {
            is VeterinariesUiEvent.LoadVeterinaries -> loadVeterinaries()
            is VeterinariesUiEvent.SearchVeterinaries -> searchVeterinaries(event.query)
            is VeterinariesUiEvent.FilterVeterinaries -> filterVeterinaries(event.options)
            is VeterinariesUiEvent.SelectVeterinary -> selectVeterinary(event.veterinary)
            is VeterinariesUiEvent.ShowVeterinaryDetails -> showVeterinaryDetails()
            is VeterinariesUiEvent.HideVeterinaryDetails -> hideVeterinaryDetails()
            is VeterinariesUiEvent.ClearError -> clearError()
        }
    }

    private fun loadVeterinaries() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                veterinaryUseCases.getAllVeterinaries().collect { veterinaries ->
                    _uiState.value = _uiState.value.copy(
                        veterinaries = veterinaries,
                        filteredVeterinaries = applyFilters(veterinaries),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar veterinários: ${e.message}"
                )
            }
        }
    }

    private fun searchVeterinaries(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                filteredVeterinaries = applyFilters(_uiState.value.veterinaries)
            )
            return
        }

        viewModelScope.launch {
            try {
                veterinaryUseCases.searchVeterinaries(query).collect { veterinaries ->
                    _uiState.value = _uiState.value.copy(
                        filteredVeterinaries = applyFilters(veterinaries),
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erro na busca: ${e.message}"
                )
            }
        }
    }

    private fun filterVeterinaries(options: VeterinaryFilterOptions) {
        _uiState.value = _uiState.value.copy(filterOptions = options)
        
        viewModelScope.launch {
            try {
                veterinaryUseCases.filterVeterinaries(options).collect { veterinaries ->
                    _uiState.value = _uiState.value.copy(
                        filteredVeterinaries = veterinaries,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erro ao filtrar veterinários: ${e.message}"
                )
            }
        }
    }

    private fun selectVeterinary(veterinary: Veterinary?) {
        _uiState.value = _uiState.value.copy(selectedVeterinary = veterinary)
    }

    private fun showVeterinaryDetails() {
        _uiState.value = _uiState.value.copy(showVeterinaryDetails = true)
    }

    private fun hideVeterinaryDetails() {
        _uiState.value = _uiState.value.copy(
            showVeterinaryDetails = false,
            selectedVeterinary = null
        )
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun applyFilters(veterinaries: List<Veterinary>): List<Veterinary> {
        val currentState = _uiState.value
        var filtered = veterinaries

        if (currentState.searchQuery.isNotBlank()) {
            val query = currentState.searchQuery.lowercase()
            filtered = filtered.filter { veterinary ->
                veterinary.fullName.lowercase().contains(query) ||
                veterinary.email.lowercase().contains(query)
            }
        }

        val options = currentState.filterOptions
        if (options.verified != null) {
            filtered = filtered.filter { it.verified == options.verified }
        }

        if (options.searchQuery.isNotBlank()) {
            val query = options.searchQuery.lowercase()
            filtered = filtered.filter { veterinary ->
                veterinary.fullName.lowercase().contains(query) ||
                veterinary.email.lowercase().contains(query)
            }
        }

        return filtered
    }
}