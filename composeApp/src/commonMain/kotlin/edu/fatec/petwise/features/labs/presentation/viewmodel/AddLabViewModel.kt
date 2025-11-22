package edu.fatec.petwise.features.labs.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.labs.domain.models.Lab
import edu.fatec.petwise.features.labs.domain.usecases.AddLabUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive

data class AddLabUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class AddLabUiEvent {
    data class AddLab(
        val name: String,
        val contactInfo: String?
    ) : AddLabUiEvent()
    data class Submit(val formData: Map<String, JsonPrimitive>) : AddLabUiEvent()
    object ClearState : AddLabUiEvent()
}

class AddLabViewModel(
    private val addLabUseCase: AddLabUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddLabUiState())
    val uiState: StateFlow<AddLabUiState> = _uiState.asStateFlow()

    fun onEvent(event: AddLabUiEvent) {
        when (event) {
            is AddLabUiEvent.AddLab -> addLab(event)
            is AddLabUiEvent.Submit -> submitForm(event.formData)
            is AddLabUiEvent.ClearState -> clearState()
        }
    }

    private fun addLab(event: AddLabUiEvent.AddLab) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val lab = Lab(
                    id = "",
                    name = event.name,
                    contactInfo = event.contactInfo,
                    createdAt = "",
                    updatedAt = ""
                )

                addLabUseCase(lab).fold(
                    onSuccess = { addedLab ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao adicionar laboratório"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    private fun submitForm(formData: Map<String, JsonPrimitive>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // Extract form data
                val name = formData["name"]?.content ?: ""
                val contactInfo = formData["contactInfo"]?.content?.takeIf { it.isNotBlank() }

                // Validate required fields
                if (name.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Nome do laboratório é obrigatório"
                    )
                    return@launch
                }

                val lab = Lab(
                    id = "",
                    name = name,
                    contactInfo = contactInfo,
                    createdAt = "",
                    updatedAt = ""
                )

                addLabUseCase(lab).fold(
                    onSuccess = { addedLab ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao adicionar laboratório"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    private fun clearState() {
        _uiState.value = AddLabUiState()
    }
}