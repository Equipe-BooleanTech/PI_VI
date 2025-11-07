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
 * Estado da UI para atualização de farmácia.
 */
data class UpdateFarmaciaUiState(
    val farmacia: Farmacia? = null,
    val isLoading: Boolean = true,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel para atualização de farmácias existentes.
 */
class UpdateFarmaciaViewModel(
    private val farmaciaId: String,
    private val getFarmaciaByIdUseCase: GetFarmaciaByIdUseCase,
    private val updateFarmaciaUseCase: UpdateFarmaciaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateFarmaciaUiState())
    val uiState: StateFlow<UpdateFarmaciaUiState> = _uiState.asStateFlow()

    init {
        loadFarmacia()
    }

    private fun loadFarmacia() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            getFarmaciaByIdUseCase(farmaciaId).fold(
                onSuccess = { farmacia ->
                    _uiState.value = _uiState.value.copy(
                        farmacia = farmacia,
                        isLoading = false
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar farmácia: ${e.message}"
                    )
                }
            )
        }
    }

    fun updateFarmacia(farmaciaAtualizada: Farmacia) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            updateFarmaciaUseCase(farmaciaId, farmaciaAtualizada).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erro ao atualizar farmácia: ${e.message}"
                    )
                }
            )
        }
    }

    fun clearState() {
        _uiState.value = _uiState.value.copy(
            isSuccess = false,
            errorMessage = null
        )
    }
}
