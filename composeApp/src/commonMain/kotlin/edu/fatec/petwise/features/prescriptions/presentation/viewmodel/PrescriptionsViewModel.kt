package edu.fatec.petwise.features.prescriptions.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.features.prescriptions.domain.usecases.*
import edu.fatec.petwise.features.pets.domain.usecases.GetPetsUseCase
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PrescriptionsUiState(
    val prescriptions: List<Prescription> = emptyList(),
    val filteredPrescriptions: List<Prescription> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedPrescription: Prescription? = null,
    val searchQuery: String = "",
    val filterStatus: String? = null,
    val petNames: Map<String, String> = emptyMap(),
    val veterinaryName: String = ""
)

sealed class PrescriptionsUiEvent {
    object LoadPrescriptions : PrescriptionsUiEvent()
    data class LoadPrescriptionsByPet(val petId: String) : PrescriptionsUiEvent()
    data class DeletePrescription(val id: String) : PrescriptionsUiEvent()
    data class SelectPrescription(val prescription: Prescription?) : PrescriptionsUiEvent()
    data class SearchPrescriptions(val query: String) : PrescriptionsUiEvent()
    data class FilterByStatus(val status: String?) : PrescriptionsUiEvent()
    object ClearError : PrescriptionsUiEvent()
}

class PrescriptionsViewModel(
    private val getPrescriptionsUseCase: GetPrescriptionsUseCase,
    private val deletePrescriptionUseCase: DeletePrescriptionUseCase,
    private val getPetsUseCase: GetPetsUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrescriptionsUiState())
    val uiState: StateFlow<PrescriptionsUiState> = _uiState.asStateFlow()

    init {
        observeDataRefresh()
        loadPetNames()
        loadVeterinaryName()
    }

    private fun observeDataRefresh() {
        viewModelScope.launch {
            DataRefreshManager.refreshEvents.collect { event ->
                when (event) {
                    is DataRefreshEvent.PrescriptionsUpdated -> loadPrescriptions()
                    is DataRefreshEvent.AllDataUpdated -> {
                        _uiState.value = PrescriptionsUiState()
                        println("PrescriptionsViewModel: Estado limpo após logout")
                    }
                    else -> {}
                }
            }
        }
    }

    fun onEvent(event: PrescriptionsUiEvent) {
        when (event) {
            is PrescriptionsUiEvent.LoadPrescriptions -> loadPrescriptions()
            is PrescriptionsUiEvent.LoadPrescriptionsByPet -> loadPrescriptionsByPet(event.petId)
            is PrescriptionsUiEvent.DeletePrescription -> deletePrescription(event.id)
            is PrescriptionsUiEvent.SelectPrescription -> selectPrescription(event.prescription)
            is PrescriptionsUiEvent.SearchPrescriptions -> searchPrescriptions(event.query)
            is PrescriptionsUiEvent.FilterByStatus -> filterByStatus(event.status)
            is PrescriptionsUiEvent.ClearError -> clearError()
            is DataRefreshEvent.UserLoggedOut -> {
                println("PrescriptionsViewModel: Usuário deslogou — limpando estado")
                _uiState.value = PrescriptionsUiState()
            }
        }
    }

    private fun loadPrescriptions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                getPrescriptionsUseCase().collect { prescriptions ->
                    val filteredPrescriptions = applyFilters(prescriptions, _uiState.value.searchQuery, _uiState.value.filterStatus)
                    _uiState.value = _uiState.value.copy(
                        prescriptions = prescriptions,
                        filteredPrescriptions = filteredPrescriptions,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar prescrições"
                )
            }
        }
    }

    private fun loadPrescriptionsByPet(petId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                getPrescriptionsUseCase.getPrescriptionsByPetId(petId).collect { prescriptions ->
                    val filteredPrescriptions = applyFilters(prescriptions, _uiState.value.searchQuery, _uiState.value.filterStatus)
                    _uiState.value = _uiState.value.copy(
                        prescriptions = prescriptions,
                        filteredPrescriptions = filteredPrescriptions,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar prescrições do pet"
                )
            }
        }
    }

    private fun deletePrescription(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                deletePrescriptionUseCase(id).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        loadPrescriptions()
                        DataRefreshManager.notifyPrescriptionsUpdated()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Erro ao deletar prescrição"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao deletar prescrição"
                )
            }
        }
    }

    private fun selectPrescription(prescription: Prescription?) {
        _uiState.value = _uiState.value.copy(selectedPrescription = prescription)
    }

    private fun searchPrescriptions(query: String) {
        val filteredPrescriptions = applyFilters(_uiState.value.prescriptions, query, _uiState.value.filterStatus)
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredPrescriptions = filteredPrescriptions
        )
    }

    private fun filterByStatus(status: String?) {
        val filteredPrescriptions = applyFilters(_uiState.value.prescriptions, _uiState.value.searchQuery, status)
        _uiState.value = _uiState.value.copy(
            filterStatus = status,
            filteredPrescriptions = filteredPrescriptions
        )
    }

    private fun applyFilters(prescriptions: List<Prescription>, query: String, status: String?): List<Prescription> {
        return prescriptions.filter { prescription ->
            val matchesQuery = query.isEmpty() ||
                prescription.medications.contains(query, ignoreCase = true) ||
                prescription.diagnosis?.contains(query, ignoreCase = true) == true ||
                prescription.observations.contains(query, ignoreCase = true)

            val matchesStatus = status == null || prescription.status == status

            matchesQuery && matchesStatus
        }
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun loadPetNames() {
        viewModelScope.launch {
            try {
                getPetsUseCase().collect { pets ->
                    val petNamesMap = pets.associate { it.id to it.name }
                    _uiState.value = _uiState.value.copy(petNames = petNamesMap)
                }
            } catch (e: Exception) {
                println("PrescriptionsViewModel: Erro ao carregar nomes dos pets: ${e.message}")
            }
        }
    }

    private fun loadVeterinaryName() {
        viewModelScope.launch {
            try {
                val result = getUserProfileUseCase.execute()
                result.fold(
                    onSuccess = { profile ->
                        _uiState.value = _uiState.value.copy(veterinaryName = profile.fullName)
                    },
                    onFailure = { error ->
                        println("PrescriptionsViewModel: Erro ao carregar nome do veterinário: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                println("PrescriptionsViewModel: Erro ao carregar nome do veterinário: ${e.message}")
            }
        }
    }
}