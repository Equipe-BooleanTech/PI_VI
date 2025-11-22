package edu.fatec.petwise.features.labs.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.labs.domain.models.Lab
import edu.fatec.petwise.features.labs.domain.usecases.UpdateLabUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.JsonPrimitive

data class UpdateLabUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class UpdateLabUiEvent {
    data class UpdateLab(val labId: String, val formData: Map<String, JsonPrimitive>) : UpdateLabUiEvent()
    object ClearState : UpdateLabUiEvent()
}

class UpdateLabViewModel(
    private val updateLabUseCase: UpdateLabUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateLabUiState())
    val uiState: StateFlow<UpdateLabUiState> = _uiState.asStateFlow()

    fun onEvent(event: UpdateLabUiEvent) {
        when (event) {
            is UpdateLabUiEvent.UpdateLab -> updateLab(event.labId, event.formData)
            is UpdateLabUiEvent.ClearState -> clearState()
        }
    }

    private fun updateLab(labId: String, formData: Map<String, JsonPrimitive>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isSuccess = false)

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

                // Create updated lab object
                val currentTime = Clock.System.now().toEpochMilliseconds().toString()
                val updatedLab = Lab(
                    id = labId,
                    name = name,
                    contactInfo = contactInfo,
                    createdAt = "", // This should be filled from original lab
                    updatedAt = currentTime
                )

                // Call use case
                updateLabUseCase(updatedLab).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao atualizar laboratório"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro inesperado ao atualizar laboratório"
                )
            }
        }
    }

    private fun clearState() {
        _uiState.value = UpdateLabUiState()
    }
}