package edu.fatec.petwise.features.labs.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.labs.domain.models.LabResult
import edu.fatec.petwise.features.labs.domain.usecases.UpdateLabResultUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.JsonPrimitive

data class UpdateLabResultUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class UpdateLabResultUiEvent {
    data class UpdateLabResult(val labResultId: String, val formData: Map<String, JsonPrimitive>) : UpdateLabResultUiEvent()
    object ClearState : UpdateLabResultUiEvent()
}

class UpdateLabResultViewModel(
    private val updateLabResultUseCase: UpdateLabResultUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateLabResultUiState())
    val uiState: StateFlow<UpdateLabResultUiState> = _uiState.asStateFlow()

    fun onEvent(event: UpdateLabResultUiEvent) {
        when (event) {
            is UpdateLabResultUiEvent.UpdateLabResult -> updateLabResult(event.labResultId, event.formData)
            is UpdateLabResultUiEvent.ClearState -> clearState()
        }
    }

    private fun updateLabResult(labResultId: String, formData: Map<String, JsonPrimitive>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isSuccess = false)

            try {
                // Extract form data
                val labType = formData["labType"]?.content ?: ""
                val labDate = formData["labDate"]?.content ?: ""
                val results = formData["results"]?.content?.takeIf { it.isNotBlank() }
                val status = formData["status"]?.content ?: ""
                val notes = formData["notes"]?.content?.takeIf { it.isNotBlank() }

                // Validate required fields
                if (labType.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Tipo de exame laboratorial é obrigatório"
                    )
                    return@launch
                }

                if (labDate.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Data do exame é obrigatória"
                    )
                    return@launch
                }

                if (status.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Status é obrigatório"
                    )
                    return@launch
                }

                // Create updated lab result object
                val currentTime = Clock.System.now().toEpochMilliseconds().toString()
                val updatedLabResult = LabResult(
                    id = labResultId,
                    petId = "", // This should be filled from original lab result
                    veterinaryId = "", // This should be filled from original lab result
                    labType = labType,
                    labDate = labDate,
                    results = results,
                    status = status,
                    notes = notes,
                    attachmentUrl = null,
                    createdAt = "", // This should be filled from original lab result
                    updatedAt = currentTime
                )

                // Call use case
                updateLabResultUseCase(updatedLabResult).fold(
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
                            errorMessage = error.message ?: "Erro ao atualizar resultado de laboratório"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro inesperado ao atualizar resultado de laboratório"
                )
            }
        }
    }

    private fun clearState() {
        _uiState.value = UpdateLabResultUiState()
    }
}