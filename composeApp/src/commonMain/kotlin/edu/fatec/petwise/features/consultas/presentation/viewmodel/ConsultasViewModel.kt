package edu.fatec.petwise.features.consultas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.models.ConsultaFilterOptions
import edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus
import edu.fatec.petwise.features.consultas.domain.usecases.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConsultasUiState(
    val consultas: List<Consulta> = emptyList(),
    val filteredConsultas: List<Consulta> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val filterOptions: ConsultaFilterOptions = ConsultaFilterOptions(),
    val searchQuery: String = "",
    val showAddConsultaDialog: Boolean = false,
    val selectedConsulta: Consulta? = null
)

sealed class ConsultasUiEvent {
    object LoadConsultas : ConsultasUiEvent()
    data class SearchConsultas(val query: String) : ConsultasUiEvent()
    data class FilterConsultas(val options: ConsultaFilterOptions) : ConsultasUiEvent()
    data class UpdateConsultaStatus(val consultaId: String, val status: ConsultaStatus) : ConsultasUiEvent()
    data class SelectConsulta(val consulta: Consulta?) : ConsultasUiEvent()
    data class DeleteConsulta(val consultaId: String) : ConsultasUiEvent()
    data class MarkAsPaid(val consultaId: String) : ConsultasUiEvent()
    object ShowAddConsultaDialog : ConsultasUiEvent()
    object HideAddConsultaDialog : ConsultasUiEvent()
    object ClearError : ConsultasUiEvent()
}

class ConsultasViewModel(
    private val getConsultasUseCase: GetConsultasUseCase,
    private val updateConsultaStatusUseCase: UpdateConsultaStatusUseCase,
    private val deleteConsultaUseCase: DeleteConsultaUseCase,
    private val markConsultaAsPaidUseCase: MarkConsultaAsPaidUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConsultasUiState())
    val uiState: StateFlow<ConsultasUiState> = _uiState.asStateFlow()

    init {
        loadConsultas()
        observeDataRefresh()
    }

    private fun observeDataRefresh() {
        viewModelScope.launch {
            DataRefreshManager.refreshEvents.collect { event ->
                when (event) {
                    is DataRefreshEvent.ConsultasUpdated -> loadConsultas()
                    is DataRefreshEvent.AllDataUpdated -> loadConsultas()
                    else -> {}
                }
            }
        }
    }

    fun onEvent(event: ConsultasUiEvent) {
        when (event) {
            is ConsultasUiEvent.LoadConsultas -> loadConsultas()
            is ConsultasUiEvent.SearchConsultas -> searchConsultas(event.query)
            is ConsultasUiEvent.FilterConsultas -> filterConsultas(event.options)
            is ConsultasUiEvent.UpdateConsultaStatus -> updateConsultaStatus(event.consultaId, event.status)
            is ConsultasUiEvent.SelectConsulta -> selectConsulta(event.consulta)
            is ConsultasUiEvent.DeleteConsulta -> deleteConsulta(event.consultaId)
            is ConsultasUiEvent.MarkAsPaid -> markAsPaid(event.consultaId)
            is ConsultasUiEvent.ShowAddConsultaDialog -> showAddConsultaDialog()
            is ConsultasUiEvent.HideAddConsultaDialog -> hideAddConsultaDialog()
            is ConsultasUiEvent.ClearError -> clearError()
        }
    }

    private fun loadConsultas() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                getConsultasUseCase().collect { consultas ->
                    _uiState.value = _uiState.value.copy(
                        consultas = consultas,
                        filteredConsultas = consultas,
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

    private fun searchConsultas(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(filteredConsultas = _uiState.value.consultas)
            return
        }

        viewModelScope.launch {
            try {
                getConsultasUseCase.searchConsultas(query).collect { consultas ->
                    _uiState.value = _uiState.value.copy(
                        filteredConsultas = consultas,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erro ao buscar consultas: ${e.message}"
                )
            }
        }
    }

    private fun filterConsultas(options: ConsultaFilterOptions) {
        _uiState.value = _uiState.value.copy(filterOptions = options)

        viewModelScope.launch {
            try {
                getConsultasUseCase.filterConsultas(options).collect { consultas ->
                    _uiState.value = _uiState.value.copy(filteredConsultas = consultas)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Erro ao filtrar consultas"
                )
            }
        }
    }

    private fun updateConsultaStatus(consultaId: String, status: ConsultaStatus) {
        viewModelScope.launch {
            try {
                updateConsultaStatusUseCase(consultaId, status).fold(
                    onSuccess = { updatedConsulta ->
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

    private fun selectConsulta(consulta: Consulta?) {
        _uiState.value = _uiState.value.copy(selectedConsulta = consulta)
    }

    private fun deleteConsulta(consultaId: String) {
        viewModelScope.launch {
            try {
                deleteConsultaUseCase(consultaId).fold(
                    onSuccess = {
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message ?: "Erro ao deletar consulta"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Erro ao deletar consulta"
                )
            }
        }
    }

    private fun markAsPaid(consultaId: String) {
        viewModelScope.launch {
            try {
                markConsultaAsPaidUseCase(consultaId).fold(
                    onSuccess = { updatedConsulta ->
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message ?: "Erro ao marcar como pago"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Erro ao marcar como pago"
                )
            }
        }
    }

    private fun showAddConsultaDialog() {
        _uiState.value = _uiState.value.copy(showAddConsultaDialog = true)
    }

    private fun hideAddConsultaDialog() {
        _uiState.value = _uiState.value.copy(showAddConsultaDialog = false)
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
