package edu.fatec.petwise.features.prescriptions.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.features.prescriptions.domain.usecases.AddPrescriptionUseCase
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
    data class AddPrescription(val formData: Map<String, Any>) : AddPrescriptionUiEvent()
    object ClearState : AddPrescriptionUiEvent()
}

class AddPrescriptionViewModel(
    private val addPrescriptionUseCase: AddPrescriptionUseCase
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
                val prescription = Prescription(
                    id = null,
                    petId = event.formData["petId"] as? String ?: "",
                    userId = "", 
                    veterinaryId = event.formData["veterinarian"] as? String ?: "",
                    medicalRecordId = event.formData["medicalRecordId"] as? String,
                    prescriptionDate = event.formData["prescriptionDate"] as? String ?: "",
                    instructions = event.formData["instructions"] as? String ?: "",
                    diagnosis = event.formData["diagnosis"] as? String,
                    validUntil = event.formData["validUntil"] as? String,
                    status = "ATIVA",
                    medications = event.formData["medications"] as? String ?: "",
                    observations = event.formData["observations"] as? String ?: "",
                    active = true,
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
                        DataRefreshManager.notifyPrescriptionsUpdated()
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