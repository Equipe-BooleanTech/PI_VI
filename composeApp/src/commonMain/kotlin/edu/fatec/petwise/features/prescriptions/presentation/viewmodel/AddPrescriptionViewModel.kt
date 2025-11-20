package edu.fatec.petwise.features.prescriptions.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.features.prescriptions.domain.usecases.AddPrescriptionUseCase
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.core.data.DataRefreshManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddPrescriptionUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class AddPrescriptionUiEvent {
    data class AddPrescription(
        val petId: String,
        val medicationName: String,
        val dosage: String,
        val frequency: String,
        val duration: String,
        val startDate: String,
        val endDate: String?,
        val instructions: String?,
        val notes: String?,
        val status: String
    ) : AddPrescriptionUiEvent()
    object ClearState : AddPrescriptionUiEvent()
}

class AddPrescriptionViewModel(
    private val addPrescriptionUseCase: AddPrescriptionUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPrescriptionUiState())
    val uiState: StateFlow<AddPrescriptionUiState> = _uiState.asStateFlow()

    init {
        observeLogout()
    }

    private fun observeLogout() {
        viewModelScope.launch {
            DataRefreshManager.refreshEvents.collect { event ->
                if (event is DataRefreshEvent.UserLoggedOut) {
                    println("AddPrescriptionViewModel: Usuário deslogou — limpando estado")
                    clearState()
                }
            }
        }
    }
    
    fun onEvent(event: AddPrescriptionUiEvent) {
        when (event) {
            is AddPrescriptionUiEvent.AddPrescription -> addPrescription(event)
            is AddPrescriptionUiEvent.ClearState -> clearState()
        }
    }

    private fun addPrescription(event: AddPrescriptionUiEvent.AddPrescription) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // Get user profile to obtain veterinaryId
                val userProfileResult = getUserProfileUseCase.execute()
                val veterinaryId = userProfileResult.fold(
                    onSuccess = { profile -> profile.id },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Erro ao obter perfil do usuário: ${error.message}"
                        )
                        return@launch
                    }
                )

                val prescription = Prescription(
                    id = "",
                    petId = event.petId,
                    veterinaryId = veterinaryId,
                    medicationName = event.medicationName,
                    dosage = event.dosage,
                    frequency = event.frequency,
                    duration = event.duration,
                    startDate = event.startDate,
                    endDate = event.endDate,
                    instructions = event.instructions,
                    notes = event.notes,
                    status = event.status,
                    createdAt = "",
                    updatedAt = ""
                )

                addPrescriptionUseCase(prescription).fold(
                    onSuccess = { addedPrescription ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao adicionar prescrição"
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
        _uiState.value = AddPrescriptionUiState()
    }
}