package edu.fatec.petwise.features.prescriptions.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.features.prescriptions.domain.usecases.GetPrescriptionByIdUseCase
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase
import edu.fatec.petwise.features.prescriptions.domain.usecases.UpdatePrescriptionUseCase
import edu.fatec.petwise.core.data.DataRefreshManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class UpdatePrescriptionUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class UpdatePrescriptionUiEvent {
    data class UpdatePrescription(val prescriptionId: String, val formData: Map<String, Any>) : UpdatePrescriptionUiEvent()
    object ClearState : UpdatePrescriptionUiEvent()
}

class UpdatePrescriptionViewModel(
    private val updatePrescriptionUseCase: UpdatePrescriptionUseCase,
    private val getPrescriptionByIdUseCase: GetPrescriptionByIdUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdatePrescriptionUiState())
    val uiState: StateFlow<UpdatePrescriptionUiState> = _uiState.asStateFlow()

    fun onEvent(event: UpdatePrescriptionUiEvent) {
        when (event) {
            is UpdatePrescriptionUiEvent.UpdatePrescription -> updatePrescription(event.prescriptionId, event.formData)
            is UpdatePrescriptionUiEvent.ClearState -> clearState()
        }
    }

    private fun updatePrescription(prescriptionId: String, formData: Map<String, Any>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isSuccess = false)

            try {
                val originalPrescription = getPrescriptionByIdUseCase(prescriptionId).firstOrNull() ?: run {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Prescrição não encontrada"
                    )
                    return@launch
                }

                val instructions = formData["instructions"] as? String ?: ""
                val diagnosis = formData["diagnosis"] as? String
                val validUntil = formData["validUntil"] as? String
                val status = formData["status"] as? String ?: ""
                val medications = formData["medications"] as? String ?: ""
                val observations = formData["observations"] as? String ?: ""
                val active = formData["active"] as? Boolean ?: true

                if (instructions.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Instruções são obrigatórias"
                    )
                    return@launch
                }

                if (medications.isNullOrBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Medicamentos são obrigatórios"
                    )
                    return@launch
                }

                val currentTime = Clock.System.now().toEpochMilliseconds().toString()
                val updatedPrescription = Prescription(
                    id = prescriptionId,
                    petId = originalPrescription.petId,
                    userId = originalPrescription.userId,
                    veterinaryId = originalPrescription.veterinaryId,
                    medicalRecordId = originalPrescription.medicalRecordId,
                    prescriptionDate = originalPrescription.prescriptionDate,
                    instructions = instructions,
                    diagnosis = diagnosis,
                    validUntil = validUntil,
                    status = status.ifBlank { "ATIVA" },
                    medications = medications,
                    observations = observations,
                    active = active,
                    createdAt = originalPrescription.createdAt,
                    updatedAt = currentTime
                )

                updatePrescriptionUseCase(updatedPrescription).fold(
                    onSuccess = {
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
                            errorMessage = error.message ?: "Erro ao atualizar prescrição"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro inesperado ao atualizar prescrição"
                )
            }
        }
    }

    private fun clearState() {
        _uiState.value = UpdatePrescriptionUiState()
    }
}