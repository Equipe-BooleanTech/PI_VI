package edu.fatec.petwise.features.exams.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.exams.domain.models.Exam
import edu.fatec.petwise.features.exams.domain.usecases.GetExamByIdUseCase
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase
import edu.fatec.petwise.features.exams.domain.usecases.UpdateExamUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
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
    private val updateExamUseCase: UpdateExamUseCase,
    private val getExamByIdUseCase: GetExamByIdUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
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
                // Get original exam
                val originalExam = getExamByIdUseCase(examId).firstOrNull() ?: run {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Exame não encontrado"
                    )
                    return@launch
                }

                // Get current user profile to get veterinary ID
                val userProfileResult = getUserProfileUseCase.execute()
                val veterinaryId = userProfileResult.getOrNull()?.id ?: ""

                val examType = formData["examType"]?.content ?: ""
                val examDateTimeStr = formData["examDate"]?.content ?: ""
                val results = formData["results"]?.content?.takeIf { it.isNotBlank() }
                val status = formData["status"]?.content ?: ""
                val notes = formData["notes"]?.content?.takeIf { it.isNotBlank() }

                if (examType.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Tipo de exame é obrigatório"
                    )
                    return@launch
                }

                if (examDateTimeStr.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Data e horário do exame são obrigatórios"
                    )
                    return@launch
                }

                val examDateTime = try {
                    if (examDateTimeStr.contains('T')) {
                        // ISO format: 2023-11-23T15:30:00
                        kotlinx.datetime.LocalDateTime.parse(examDateTimeStr)
                    } else {
                        // User friendly format: DD/MM/YYYY HH:mm
                        val parts = examDateTimeStr.split(" ")
                        if (parts.size == 2) {
                            val dateParts = parts[0].split("/")
                            val timeParts = parts[1].split(":")
                            if (dateParts.size == 3 && timeParts.size >= 2) {
                                val day = dateParts[0].toInt()
                                val month = dateParts[1].toInt()
                                val year = dateParts[2].toInt()
                                val hour = timeParts[0].toInt()
                                val minute = timeParts[1].toInt()
                                
                                val date = kotlinx.datetime.LocalDate(year, month, day)
                                val time = kotlinx.datetime.LocalTime(hour, minute)
                                kotlinx.datetime.LocalDateTime(date, time)
                            } else {
                                throw IllegalArgumentException("Invalid date/time format")
                            }
                        } else {
                            throw IllegalArgumentException("Expected date and time separated by space")
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Formato de data/hora inválido: ${e.message}"
                    )
                    return@launch
                }

                val examTime = "${examDateTime.hour.toString().padStart(2, '0')}:${examDateTime.minute.toString().padStart(2, '0')}"

                val currentTime = Clock.System.now().toEpochMilliseconds().toString()
                val updatedExam = Exam(
                    id = examId,
                    petId = originalExam.petId,
                    veterinaryId = veterinaryId,
                    examType = examType,
                    examDate = examDateTime,
                    examTime = examTime,
                    results = results,
                    status = status,
                    notes = notes,
                    attachmentUrl = originalExam.attachmentUrl,
                    createdAt = originalExam.createdAt,
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