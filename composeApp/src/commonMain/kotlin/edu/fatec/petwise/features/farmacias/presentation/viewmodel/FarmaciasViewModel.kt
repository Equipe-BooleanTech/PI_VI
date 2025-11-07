package edu.fatec.petwise.features.farmacias.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.farmacias.domain.models.*
import edu.fatec.petwise.features.farmacias.domain.usecases.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado da UI para listagem de farmácias.
 */
data class FarmaciasUiState(
    val farmacias: List<Farmacia> = emptyList(),
    val filteredFarmacias: List<Farmacia> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val filterOptions: FarmaciaFilterOptions = FarmaciaFilterOptions(),
    val searchQuery: String = "",
    val showApenasAtivas: Boolean = false
)

/**
 * Eventos da UI para gerenciamento de farmácias.
 */
sealed class FarmaciasUiEvent {
    object LoadFarmacias : FarmaciasUiEvent()
    data class SearchFarmacias(val query: String) : FarmaciasUiEvent()
    data class FilterFarmacias(val options: FarmaciaFilterOptions) : FarmaciasUiEvent()
    object LoadApenasAtivas : FarmaciasUiEvent()
    data class DeleteFarmacia(val id: String) : FarmaciasUiEvent()
    data class UpdateStatus(val id: String, val novoStatus: StatusFarmacia, val motivo: String?) : FarmaciasUiEvent()
    data class UpdateLimiteCredito(val id: String, val novoLimite: Double) : FarmaciasUiEvent()
    data class GetByCidade(val cidade: String) : FarmaciasUiEvent()
    data class GetByEstado(val estado: String) : FarmaciasUiEvent()
    object ClearError : FarmaciasUiEvent()
}

/**
 * ViewModel para gerenciar a listagem e operações com farmácias.
 */
class FarmaciasViewModel(
    private val getFarmaciasUseCase: GetFarmaciasUseCase,
    private val filterFarmaciasUseCase: FilterFarmaciasUseCase,
    private val getFarmaciasAtivasUseCase: GetFarmaciasAtivasUseCase,
    private val deleteFarmaciaUseCase: DeleteFarmaciaUseCase,
    private val updateStatusUseCase: UpdateStatusFarmaciaUseCase,
    private val updateLimiteCreditoUseCase: UpdateLimiteCreditoUseCase,
    private val getFarmaciasByCidadeUseCase: GetFarmaciasByCidadeUseCase,
    private val getFarmaciasByEstadoUseCase: GetFarmaciasByEstadoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmaciasUiState())
    val uiState: StateFlow<FarmaciasUiState> = _uiState.asStateFlow()

    init {
        loadFarmacias()
    }

    fun onEvent(event: FarmaciasUiEvent) {
        when (event) {
            is FarmaciasUiEvent.LoadFarmacias -> loadFarmacias()
            is FarmaciasUiEvent.SearchFarmacias -> searchFarmacias(event.query)
            is FarmaciasUiEvent.FilterFarmacias -> filterFarmacias(event.options)
            is FarmaciasUiEvent.LoadApenasAtivas -> loadApenasAtivas()
            is FarmaciasUiEvent.DeleteFarmacia -> deleteFarmacia(event.id)
            is FarmaciasUiEvent.UpdateStatus -> updateStatus(event.id, event.novoStatus, event.motivo)
            is FarmaciasUiEvent.UpdateLimiteCredito -> updateLimiteCredito(event.id, event.novoLimite)
            is FarmaciasUiEvent.GetByCidade -> getFarmaciasByCidade(event.cidade)
            is FarmaciasUiEvent.GetByEstado -> getFarmaciasByEstado(event.estado)
            is FarmaciasUiEvent.ClearError -> clearError()
        }
    }

    private fun loadFarmacias() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                getFarmaciasUseCase().collect { farmacias ->
                    _uiState.value = _uiState.value.copy(
                        farmacias = farmacias,
                        filteredFarmacias = farmacias,
                        isLoading = false,
                        showApenasAtivas = false
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

    private fun searchFarmacias(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        val filtered = if (query.isBlank()) {
            _uiState.value.farmacias
        } else {
            _uiState.value.farmacias.filter { farmacia ->
                farmacia.nomeFantasia.contains(query, ignoreCase = true) ||
                farmacia.razaoSocial.contains(query, ignoreCase = true) ||
                farmacia.cnpj.contains(query, ignoreCase = true) ||
                farmacia.cidade.contains(query, ignoreCase = true) ||
                farmacia.estado.contains(query, ignoreCase = true)
            }
        }
        _uiState.value = _uiState.value.copy(filteredFarmacias = filtered)
    }

    private fun filterFarmacias(options: FarmaciaFilterOptions) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                filterOptions = options,
                showApenasAtivas = false
            )
            try {
                filterFarmaciasUseCase(options).collect { farmacias ->
                    _uiState.value = _uiState.value.copy(
                        filteredFarmacias = farmacias,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao filtrar farmácias: ${e.message}"
                )
            }
        }
    }

    private fun loadApenasAtivas() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, showApenasAtivas = true)
            try {
                getFarmaciasAtivasUseCase().collect { farmacias ->
                    _uiState.value = _uiState.value.copy(
                        filteredFarmacias = farmacias,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar farmácias ativas: ${e.message}"
                )
            }
        }
    }

    private fun deleteFarmacia(id: String) {
        viewModelScope.launch {
            try {
                deleteFarmaciaUseCase(id).fold(
                    onSuccess = { loadFarmacias() },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Erro ao deletar farmácia: ${e.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erro ao deletar farmácia: ${e.message}"
                )
            }
        }
    }

    private fun updateStatus(id: String, novoStatus: StatusFarmacia, motivo: String?) {
        viewModelScope.launch {
            try {
                updateStatusUseCase(id, novoStatus, motivo).fold(
                    onSuccess = { loadFarmacias() },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Erro ao atualizar status: ${e.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erro ao atualizar status: ${e.message}"
                )
            }
        }
    }

    private fun updateLimiteCredito(id: String, novoLimite: Double) {
        viewModelScope.launch {
            try {
                updateLimiteCreditoUseCase(id, novoLimite).fold(
                    onSuccess = { loadFarmacias() },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Erro ao atualizar limite de crédito: ${e.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Erro ao atualizar limite de crédito: ${e.message}"
                )
            }
        }
    }

    private fun getFarmaciasByCidade(cidade: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                getFarmaciasByCidadeUseCase(cidade).collect { farmacias ->
                    _uiState.value = _uiState.value.copy(
                        filteredFarmacias = farmacias,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao buscar por cidade: ${e.message}"
                )
            }
        }
    }

    private fun getFarmaciasByEstado(estado: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                getFarmaciasByEstadoUseCase(estado).collect { farmacias ->
                    _uiState.value = _uiState.value.copy(
                        filteredFarmacias = farmacias,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao buscar por estado: ${e.message}"
                )
            }
        }
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
