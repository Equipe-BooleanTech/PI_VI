package edu.fatec.petwise.features.medications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.medications.domain.models.Medication
import edu.fatec.petwise.features.medications.domain.usecases.GetMedicationByIdUseCase
import edu.fatec.petwise.features.medications.domain.usecases.UpdateMedicationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class UpdateMedicationUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val medication: Medication? = null
)

class UpdateMedicationViewModel(
    private val updateMedicationUseCase: UpdateMedicationUseCase,
    private val getMedicationByIdUseCase: GetMedicationByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateMedicationUiState())
    val uiState: StateFlow<UpdateMedicationUiState> = _uiState.asStateFlow()

    fun loadMedication(medicationId: String) {
        viewModelScope.launch {
            println("Carregando medicamento para edição: $medicationId")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                getMedicationByIdUseCase(medicationId).collect { medication ->
                    if (medication != null) {
                        println("Medicamento carregado: ${medication.medicationName}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            medication = medication,
                            errorMessage = null
                        )
                    } else {
                        println("Medicamento não encontrado: $medicationId")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Medicamento não encontrado"
                        )
                    }
                }
            } catch (e: Exception) {
                println("Erro ao carregar medicamento: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao carregar medicamento"
                )
            }
        }
    }

    fun updateMedication(medicationId: String, formData: Map<String, Any>) {
        viewModelScope.launch {
            println("Iniciando atualização de medicamento: $medicationId")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val currentMedication = _uiState.value.medication
                if (currentMedication == null) {
                    println("Erro: Medicamento não carregado para atualização")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Medicamento não encontrado"
                    )
                    return@launch
                }

                val medicationName = formData["medicationName"] as? String ?: currentMedication.medicationName
                val dosage = formData["dosage"] as? String ?: currentMedication.dosage
                val frequency = formData["frequency"] as? String ?: currentMedication.frequency
                val durationDaysStr = formData["durationDays"] as? String ?: currentMedication.durationDays.toString()
                val startDate = formData["startDate"] as? String ?: currentMedication.startDate
                val endDate = formData["endDate"] as? String ?: currentMedication.endDate
                val prescriptionId = formData["prescriptionId"] as? String ?: currentMedication.prescriptionId
                val sideEffects = formData["sideEffects"] as? String ?: currentMedication.sideEffects

                // Convert string to int
                val durationDays = durationDaysStr.toIntOrNull()
                if (durationDays == null || durationDays <= 0) {
                    println("Erro de validação: Duração inválida - $durationDaysStr")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Duração deve ser um número válido maior que 0"
                    )
                    return@launch
                }

                // Generate current timestamp for updatedAt
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()

                val updatedMedication = currentMedication.copy(
                    medicationName = medicationName,
                    dosage = dosage,
                    frequency = frequency,
                    durationDays = durationDays,
                    startDate = startDate,
                    endDate = endDate,
                    prescriptionId = prescriptionId,
                    sideEffects = sideEffects,
                    updatedAt = now
                )

                println("Salvando medicamento atualizado: nome=${updatedMedication.medicationName}")

                updateMedicationUseCase(updatedMedication).fold(
                    onSuccess = { savedMedication ->
                        println("Medicamento atualizado com sucesso: ${savedMedication.medicationName}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            medication = savedMedication,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        println("Erro ao atualizar medicamento: ${error.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao atualizar medicamento"
                        )
                    }
                )
            } catch (e: Exception) {
                println("Exceção durante atualização do medicamento: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    fun clearState() {
        println("Limpando estado do UpdateMedicationViewModel")
        _uiState.value = UpdateMedicationUiState()
    }
}