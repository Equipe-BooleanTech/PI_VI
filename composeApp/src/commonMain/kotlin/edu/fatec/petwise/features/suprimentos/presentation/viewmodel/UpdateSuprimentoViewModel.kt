package edu.fatec.petwise.features.suprimentos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.features.suprimentos.domain.models.Suprimento
import edu.fatec.petwise.features.suprimentos.domain.models.SuprimentCategory
import edu.fatec.petwise.features.suprimentos.domain.usecases.GetSuprimentoByIdUseCase
import edu.fatec.petwise.features.suprimentos.domain.usecases.UpdateSuprimentoUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for updating existing supplies
 */
class UpdateSuprimentoViewModel(
    private val updateSuprimentoUseCase: UpdateSuprimentoUseCase,
    private val getSuprimentoByIdUseCase: GetSuprimentoByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateSuprimentoUiState())
    val uiState: StateFlow<UpdateSuprimentoUiState> = _uiState.asStateFlow()

    fun handleEvent(event: UpdateSuprimentoUiEvent) {
        when (event) {
            is UpdateSuprimentoUiEvent.LoadSuprimento -> loadSuprimento(event.id)
            is UpdateSuprimentoUiEvent.UpdateSuprimento -> updateSuprimento(event.suprimento)
            is UpdateSuprimentoUiEvent.ClearError -> clearError()
            is UpdateSuprimentoUiEvent.ClearSuccess -> clearSuccess()
        }
    }

    private fun loadSuprimento(id: String) {
        viewModelScope.launch {
            getSuprimentoByIdUseCase(id).collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is NetworkResult.Success -> _uiState.value.copy(
                        isLoading = false,
                        currentSuprimento = result.data,
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

    private fun updateSuprimento(suprimento: Suprimento) {
        viewModelScope.launch {
            updateSuprimentoUseCase(suprimento).collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is NetworkResult.Success -> _uiState.value.copy(
                        isLoading = false,
                        updatedSuprimento = result.data,
                        successMessage = "Suprimento atualizado com sucesso!",
                        errorMessage = null
                    )
                    is NetworkResult.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message,
                        updatedSuprimento = null
                    )
                }
            }
        }
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }

    private fun clearSuccess() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            updatedSuprimento = null
        )
    }

    fun updateSuprimentoData(
        current: Suprimento,
        petId: String,
        description: String,
        category: String,
        price: Float,
        orderDate: String,
        shopName: String
    ): Suprimento {
        return current.copy(
            petId = petId,
            description = description,
            category = SuprimentCategory.fromDisplayName(category),
            price = price,
            orderDate = orderDate,
            shopName = shopName
        )
    }
}

/**
 * UI State for Update Suprimento screen
 */
data class UpdateSuprimentoUiState(
    val isLoading: Boolean = false,
    val currentSuprimento: Suprimento? = null,
    val updatedSuprimento: Suprimento? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

/**
 * UI Events for Update Suprimento screen
 */
sealed class UpdateSuprimentoUiEvent {
    data class LoadSuprimento(val id: String) : UpdateSuprimentoUiEvent()
    data class UpdateSuprimento(val suprimento: Suprimento) : UpdateSuprimentoUiEvent()
    object ClearError : UpdateSuprimentoUiEvent()
    object ClearSuccess : UpdateSuprimentoUiEvent()
}