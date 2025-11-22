package edu.fatec.petwise.features.exams.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.exams.domain.models.Exam
import edu.fatec.petwise.features.exams.domain.usecases.AddExamUseCase
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.core.data.DataRefreshManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime


data class AddExamUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class AddExamUiEvent {
    data class AddExam(
        val petId: String,
        val examType: String,
        val examDateTime: LocalDateTime,
        val results: String?,
        val notes: String?
    ) : AddExamUiEvent()
    object ClearState : AddExamUiEvent()
}

class AddExamViewModel(
    private val addExamUseCase: AddExamUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExamUiState())
    val uiState: StateFlow<AddExamUiState> = _uiState.asStateFlow()

    init {
        // No logout observation needed
    }

    private fun observeLogout() {
        // Not used
    }

    fun onEvent(event: AddExamUiEvent) {
        when (event) {
            is AddExamUiEvent.AddExam -> addExam(event)
            is AddExamUiEvent.ClearState -> clearState()
        }
    }

    private fun addExam(event: AddExamUiEvent.AddExam) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // Get current user profile to get veterinary ID
                val userProfileResult = getUserProfileUseCase.execute()
                val veterinaryId = userProfileResult.getOrNull()?.id ?: ""

                val exam = Exam(
                    id = "",
                    petId = event.petId,
                    veterinaryId = veterinaryId,
                    examType = event.examType,
                    examDate = event.examDateTime,
                    examTime = "${event.examDateTime.hour.toString().padStart(2, '0')}:${event.examDateTime.minute.toString().padStart(2, '0')}",
                    results = event.results,
                    status = "PENDING",
                    notes = event.notes,
                    attachmentUrl = null,
                    createdAt = "",
                    updatedAt = ""
                )

                addExamUseCase(exam).fold(
                    onSuccess = { addedExam ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao adicionar exame"
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
        _uiState.value = AddExamUiState()
    }
}