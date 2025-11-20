package edu.fatec.petwise.features.exams.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.exams.domain.models.Exam
import edu.fatec.petwise.features.exams.domain.usecases.UpdateExamUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.JsonPrimitive

data class UpdateExamUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class UpdateExamUiEvent {
    data class UpdateExam(val examId: String, val formData: Map<String, JsonPrimitive>) : UpdateExamUiEvent()
    object ClearState : UpdateExamUiEvent()
}

class UpdateExamViewModel(
    private val updateExamUseCase: UpdateExamUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateExamUiState())
    val uiState: StateFlow<UpdateExamUiState> = _uiState.asStateFlow()

    fun onEvent(event: UpdateExamUiEvent) {
        when (event) {
            is UpdateExamUiEvent.UpdateExam -> updateExam(event.examId, event.formData)
            is UpdateExamUiEvent.ClearState -> clearState()
        }
    }

    private fun updateExam(examId: String, formData: Map<String, JsonPrimitive>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isSuccess = false)

            try {
                // Extract form data
                val examType = formData["examType"]?.content ?: ""
                val examDate = formData["examDate"]?.content ?: ""
                val results = formData["results"]?.content?.takeIf { it.isNotBlank() }
                val status = formData["status"]?.content ?: ""
                val notes = formData["notes"]?.content?.takeIf { it.isNotBlank() }

                // Validate required fields
                if (examType.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Tipo de exame é obrigatório"
                    )
                    return@launch
                }

                if (examDate.isBlank()) {
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

                // Create updated exam object
                val currentTime = Clock.System.now().toEpochMilliseconds().toString()
                val updatedExam = Exam(
                    id = examId,
                    petId = "", // This should be filled from original exam
                    veterinaryId = "", // This should be filled from original exam
                    examType = examType,
                    examDate = examDate,
                    results = results,
                    status = status,
                    notes = notes,
                    attachmentUrl = null,
                    createdAt = "", // This should be filled from original exam
                    updatedAt = currentTime
                )

                // Call use case
                updateExamUseCase(updatedExam).fold(
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
                            errorMessage = error.message ?: "Erro ao atualizar exame"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro inesperado ao atualizar exame"
                )
            }
        }
    }

    private fun clearState() {
        _uiState.value = UpdateExamUiState()
    }
}