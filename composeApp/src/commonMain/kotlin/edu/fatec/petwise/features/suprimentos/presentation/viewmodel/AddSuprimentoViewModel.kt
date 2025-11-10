package edu.fatec.petwise.features.suprimentos.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.features.suprimentos.domain.models.Suprimento
import edu.fatec.petwise.features.suprimentos.domain.models.SuprimentCategory
import edu.fatec.petwise.features.suprimentos.domain.usecases.AddSuprimentoUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddSuprimentoViewModel(
    private val addSuprimentoUseCase: AddSuprimentoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddSuprimentoUiState())
    val uiState: StateFlow<AddSuprimentoUiState> = _uiState.asStateFlow()

    fun handleEvent(event: AddSuprimentoUiEvent) {
        when (event) {
            is AddSuprimentoUiEvent.AddSuprimento -> addSuprimento(event.suprimento)
            is AddSuprimentoUiEvent.ClearError -> clearError()
            is AddSuprimentoUiEvent.ClearSuccess -> clearSuccess()
            is AddSuprimentoUiEvent.ResetForm -> resetForm()
        }
    }

    private fun addSuprimento(suprimento: Suprimento) {
        viewModelScope.launch {
            addSuprimentoUseCase(suprimento).collect { result ->
                _uiState.value = when (result) {
                    is NetworkResult.Loading -> _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is NetworkResult.Success -> _uiState.value.copy(
                        isLoading = false,
                        addedSuprimento = result.data,
                        successMessage = "Suprimento adicionado com sucesso!",
                        errorMessage = null
                    )
                    is NetworkResult.Error -> _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message,
                        addedSuprimento = null
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
            addedSuprimento = null
        )
    }

    private fun resetForm() {
        _uiState.value = AddSuprimentoUiState()
    }

    fun createSuprimento(
        petId: String,
        description: String,
        category: String,
        price: Float,
        orderDate: String,
        shopName: String
    ): Suprimento {
        return Suprimento(
            petId = petId,
            description = description,
            category = SuprimentCategory.fromDisplayName(category),
            price = price,
            orderDate = orderDate,
            shopName = shopName
        )
    }
}

data class AddSuprimentoUiState(
    val isLoading: Boolean = false,
    val addedSuprimento: Suprimento? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

sealed class AddSuprimentoUiEvent {
    data class AddSuprimento(val suprimento: Suprimento) : AddSuprimentoUiEvent()
    object ClearError : AddSuprimentoUiEvent()
    object ClearSuccess : AddSuprimentoUiEvent()
    object ResetForm : AddSuprimentoUiEvent()
}