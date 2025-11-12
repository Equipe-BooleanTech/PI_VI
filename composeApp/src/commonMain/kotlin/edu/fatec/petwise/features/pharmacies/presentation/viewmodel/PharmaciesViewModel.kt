package edu.fatec.petwise.features.pharmacies.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.pharmacies.domain.models.Pharmacy
import edu.fatec.petwise.features.pharmacies.domain.models.PharmacyFilterOptions
import edu.fatec.petwise.features.pharmacies.domain.usecases.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PharmaciesUiState(
    val pharmacies: List<Pharmacy> = emptyList(),
    val filteredPharmacies: List<Pharmacy> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val filterOptions: PharmacyFilterOptions = PharmacyFilterOptions(),
    val searchQuery: String = "",
    val selectedPharmacy: Pharmacy? = null,
    val showPharmacyDetails: Boolean = false
)

sealed class PharmaciesUiEvent {
    object LoadPharmacies : PharmaciesUiEvent()
    data class SearchPharmacies(val query: String) : PharmaciesUiEvent()
    data class FilterPharmacies(val options: PharmacyFilterOptions) : PharmaciesUiEvent()
    data class SelectPharmacy(val pharmacy: Pharmacy?) : PharmaciesUiEvent()
    object ShowPharmacyDetails : PharmaciesUiEvent()
    object HidePharmacyDetails : PharmaciesUiEvent()
    object ClearError : PharmaciesUiEvent()
}

class PharmaciesViewModel(
    private val pharmacyUseCases: PharmacyUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(PharmaciesUiState())
    val uiState: StateFlow<PharmaciesUiState> = _uiState.asStateFlow()

    init {
        loadPharmacies()
    }

    fun handleEvent(event: PharmaciesUiEvent) {
        when (event) {
            is PharmaciesUiEvent.LoadPharmacies -> loadPharmacies()
            is PharmaciesUiEvent.SearchPharmacies -> searchPharmacies(event.query)
            is PharmaciesUiEvent.FilterPharmacies -> filterPharmacies(event.options)
            is PharmaciesUiEvent.SelectPharmacy -> selectPharmacy(event.pharmacy)
            is PharmaciesUiEvent.ShowPharmacyDetails -> showPharmacyDetails()
            is PharmaciesUiEvent.HidePharmacyDetails -> hidePharmacyDetails()
            is PharmaciesUiEvent.ClearError -> clearError()
        }
    }

    private fun loadPharmacies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                pharmacyUseCases.getAllPharmacies().collect { pharmacies ->
                    _uiState.value = _uiState.value.copy(
                        pharmacies = pharmacies,
                        filteredPharmacies = applyFilters(pharmacies),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar farmácias: ${e.message}"
                )
            }
        }
    }

    private fun searchPharmacies(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        
        // Apply local search filter
        _uiState.value = _uiState.value.copy(
            filteredPharmacies = applyFilters(_uiState.value.pharmacies)
        )
    }

    private fun filterPharmacies(options: PharmacyFilterOptions) {
        _uiState.value = _uiState.value.copy(filterOptions = options)
        
        viewModelScope.launch {
            try {
                pharmacyUseCases.filterPharmacies(options).collect { pharmacies ->
                    _uiState.value = _uiState.value.copy(
                        filteredPharmacies = pharmacies,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erro ao filtrar farmácias: ${e.message}"
                )
            }
        }
    }

    private fun selectPharmacy(pharmacy: Pharmacy?) {
        _uiState.value = _uiState.value.copy(selectedPharmacy = pharmacy)
    }

    private fun showPharmacyDetails() {
        _uiState.value = _uiState.value.copy(showPharmacyDetails = true)
    }

    private fun hidePharmacyDetails() {
        _uiState.value = _uiState.value.copy(
            showPharmacyDetails = false,
            selectedPharmacy = null
        )
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun applyFilters(pharmacies: List<Pharmacy>): List<Pharmacy> {
        val currentState = _uiState.value
        var filtered = pharmacies

        if (currentState.searchQuery.isNotBlank()) {
            val query = currentState.searchQuery.lowercase()
            filtered = filtered.filter { pharmacy ->
                pharmacy.fullName.lowercase().contains(query) ||
                pharmacy.email.lowercase().contains(query) ||
                pharmacy.phone?.lowercase()?.contains(query) == true
            }
        }

        val options = currentState.filterOptions
        if (options.verified != null) {
            filtered = filtered.filter { it.verified == options.verified }
        }

        if (options.searchQuery.isNotBlank()) {
            val query = options.searchQuery.lowercase()
            filtered = filtered.filter { pharmacy ->
                pharmacy.fullName.lowercase().contains(query) ||
                pharmacy.email.lowercase().contains(query) ||
                pharmacy.phone?.lowercase()?.contains(query) == true
            }
        }

        return filtered
    }
}
