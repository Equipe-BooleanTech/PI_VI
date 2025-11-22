package edu.fatec.petwise.features.labs.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.features.labs.domain.models.Lab
import edu.fatec.petwise.features.labs.domain.usecases.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LabsUiState(
    val labs: List<Lab> = emptyList(),
    val filteredLabs: List<Lab> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedLab: Lab? = null
)

sealed class LabsUiEvent {
    object LoadLabs : LabsUiEvent()
    data class DeleteLab(val id: String) : LabsUiEvent()
    data class SelectLab(val lab: Lab?) : LabsUiEvent()
    object ClearError : LabsUiEvent()
}

class LabsViewModel(
    private val getLabsUseCase: GetLabsUseCase,
    private val deleteLabUseCase: DeleteLabUseCase
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
                    is DataRefreshEvent.LabResultsUpdated -> loadLabs()
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
            is LabsUiEvent.LoadLabs -> loadLabs()
            is LabsUiEvent.DeleteLab -> deleteLab(event.id)
            is LabsUiEvent.SelectLab -> selectLab(event.lab)
            is LabsUiEvent.ClearError -> clearError()
            is DataRefreshEvent.UserLoggedOut -> {
                println("LabsViewModel: Usuário deslogou — limpando estado")
                _uiState.value = LabsUiState()
            }
        }
    }

    private fun loadLabs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                getLabsUseCase().collect { labs ->
                    _uiState.value = _uiState.value.copy(
                        labs = labs,
                        filteredLabs = labs,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar laboratórios"
                )
            }
        }
    }

    private fun deleteLab(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                deleteLabUseCase(id).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        loadLabs()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Erro ao deletar laboratório"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao deletar laboratório"
                )
            }
        }
    }

    private fun selectLab(lab: Lab?) {
        _uiState.value = _uiState.value.copy(selectedLab = lab)
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}