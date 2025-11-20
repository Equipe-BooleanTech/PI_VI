package edu.fatec.petwise.features.labs.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.labs.domain.models.LabResult
import edu.fatec.petwise.features.labs.domain.usecases.AddLabResultUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive

data class AddLabResultUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class AddLabResultUiEvent {
    data class AddLabResult(
        val petId: String,
        val labType: String,
        val labDate: String,
        val results: String?,
        val notes: String?
    ) : AddLabResultUiEvent()
    data class Submit(val formData: Map<String, JsonPrimitive>) : AddLabResultUiEvent()
    object ClearState : AddLabResultUiEvent()
}

class AddLabResultViewModel(
    private val addLabResultUseCase: AddLabResultUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddLabResultUiState())
    val uiState: StateFlow<AddLabResultUiState> = _uiState.asStateFlow()

    fun onEvent(event: AddLabResultUiEvent) {
        when (event) {
            is AddLabResultUiEvent.AddLabResult -> addLabResult(event)
            is AddLabResultUiEvent.Submit -> submitForm(event.formData)
            is AddLabResultUiEvent.ClearState -> clearState()
        }
    }

    private fun addLabResult(event: AddLabResultUiEvent.AddLabResult) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val labResult = LabResult(
                    id = "",
                    petId = event.petId,
                    veterinaryId = "",  // Not needed for create
                    labType = event.labType,
                    labDate = event.labDate,
                    results = event.results,
                    status = "PENDING",
                    notes = event.notes,
                    attachmentUrl = null,
                    createdAt = "",
                    updatedAt = ""
                )

                addLabResultUseCase(labResult).fold(
                    onSuccess = { addedLabResult ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao adicionar resultado de laboratório"
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
                val labType = formData["labType"]?.content ?: ""
                val labDate = formData["labDate"]?.content ?: ""
                val results = formData["results"]?.content?.takeIf { it.isNotBlank() }
                val status = formData["status"]?.content ?: "PENDING"
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

                val labResult = LabResult(
                    id = "",
                    petId = "", // This should be filled from form or context
                    veterinaryId = "",  // Not needed for create
                    labType = labType,
                    labDate = labDate,
                    results = results,
                    status = status,
                    notes = notes,
                    attachmentUrl = null,
                    createdAt = "",
                    updatedAt = ""
                )

                addLabResultUseCase(labResult).fold(
                    onSuccess = { addedLabResult ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao adicionar resultado de laboratório"
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
        _uiState.value = AddLabResultUiState()
    }
}