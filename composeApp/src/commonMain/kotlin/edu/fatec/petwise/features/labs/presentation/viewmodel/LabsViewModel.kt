package edu.fatec.petwise.features.labs.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.features.labs.domain.models.LabResult
import edu.fatec.petwise.features.labs.domain.usecases.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LabsUiState(
    val labResults: List<LabResult> = emptyList(),
    val filteredLabResults: List<LabResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedLabResult: LabResult? = null
)

sealed class LabsUiEvent {
    object LoadLabResults : LabsUiEvent()
    data class LoadLabResultsByPet(val petId: String) : LabsUiEvent()
    data class DeleteLabResult(val id: String) : LabsUiEvent()
    data class SelectLabResult(val labResult: LabResult?) : LabsUiEvent()
    object ClearError : LabsUiEvent()
}

class LabsViewModel(
    private val getLabResultsUseCase: GetLabResultsUseCase,
    private val deleteLabResultUseCase: DeleteLabResultUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LabsUiState())
    val uiState: StateFlow<LabsUiState> = _uiState.asStateFlow()

    init {
        observeDataRefresh()
    }

    private fun observeDataRefresh() {
        viewModelScope.launch {
            DataRefreshManager.refreshEvents.collect { event ->
                when (event) {
                    is DataRefreshEvent.LabResultsUpdated -> loadLabResults()
                    is DataRefreshEvent.AllDataUpdated -> {
                        _uiState.value = LabsUiState()
                        println("LabsViewModel: Estado limpo após logout")
                    }
                    else -> {}
                }
            }
        }
    }

    fun onEvent(event: LabsUiEvent) {
        when (event) {
            is LabsUiEvent.LoadLabResults -> loadLabResults()
            is LabsUiEvent.LoadLabResultsByPet -> loadLabResultsByPet(event.petId)
            is LabsUiEvent.DeleteLabResult -> deleteLabResult(event.id)
            is LabsUiEvent.SelectLabResult -> selectLabResult(event.labResult)
            is LabsUiEvent.ClearError -> clearError()
            is DataRefreshEvent.UserLoggedOut -> {
                println("LabsViewModel: Usuário deslogou — limpando estado")
                _uiState.value = LabsUiState()
            }
        }
    }

    private fun loadLabResults() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                getLabResultsUseCase().collect { labResults ->
                    _uiState.value = _uiState.value.copy(
                        labResults = labResults,
                        filteredLabResults = labResults,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar resultados de laboratório"
                )
            }
        }
    }

    private fun loadLabResultsByPet(petId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // For now, filter from all results. In a real implementation, you'd have a specific use case
                getLabResultsUseCase().collect { labResults ->
                    val filteredResults = labResults.filter { it.petId == petId }
                    _uiState.value = _uiState.value.copy(
                        labResults = filteredResults,
                        filteredLabResults = filteredResults,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar resultados de laboratório do pet"
                )
            }
        }
    }

    private fun deleteLabResult(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                deleteLabResultUseCase(id).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        loadLabResults()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Erro ao deletar resultado de laboratório"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao deletar resultado de laboratório"
                )
            }
        }
    }

    private fun selectLabResult(labResult: LabResult?) {
        _uiState.value = _uiState.value.copy(selectedLabResult = labResult)
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}