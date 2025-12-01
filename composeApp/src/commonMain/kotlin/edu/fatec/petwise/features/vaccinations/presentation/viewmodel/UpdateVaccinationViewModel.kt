package edu.fatec.petwise.features.vaccinations.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.vaccinations.domain.models.VaccineType
import edu.fatec.petwise.features.vaccinations.domain.models.VaccinationStatus
import edu.fatec.petwise.features.vaccinations.domain.usecases.UpdateVaccinationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.JsonPrimitive

data class UpdateVaccinationUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class UpdateVaccinationUiEvent {
    data class UpdateVaccination(val vaccinationId: String, val formData: Map<String, JsonPrimitive>) : UpdateVaccinationUiEvent()
    object ClearState : UpdateVaccinationUiEvent()
}

class UpdateVaccinationViewModel(
    private val updateVaccinationUseCase: UpdateVaccinationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateVaccinationUiState())
    val uiState: StateFlow<UpdateVaccinationUiState> = _uiState.asStateFlow()

    fun onEvent(event: UpdateVaccinationUiEvent) {
        when (event) {
            is UpdateVaccinationUiEvent.UpdateVaccination -> updateVaccination(event.vaccinationId, event.formData)
            is UpdateVaccinationUiEvent.ClearState -> clearState()
        }
    }

    private fun updateVaccination(vaccinationId: String, formData: Map<String, JsonPrimitive>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isSuccess = false)
            
            try {
                
                val vaccineTypeStr = formData["vaccineType"]?.content ?: ""
                
                
                val vaccinationDate = try {
                    
                    val dateStr = formData["vaccinationDate"]?.content ?: ""
                    if (dateStr.contains("T")) {
                        kotlinx.datetime.LocalDateTime.parse(dateStr)
                    } else {
                        
                        kotlinx.datetime.LocalDateTime.parse("${dateStr}T00:00:00")
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Data de vacinação inválida"
                    )
                    return@launch
                }
                
                val nextDoseDate = try {
                    val nextDateStr = formData["nextDoseDate"]?.content?.takeIf { it.isNotBlank() }
                    nextDateStr?.let {
                        if (it.contains("T")) {
                            kotlinx.datetime.LocalDateTime.parse(it)
                        } else {
                            kotlinx.datetime.LocalDateTime.parse("${it}T00:00:00")
                        }
                    }
                } catch (e: Exception) {
                    null
                }
                
                val totalDoses = formData["totalDoses"]?.content?.toIntOrNull() ?: 1
                val manufacturer = formData["manufacturer"]?.content?.takeIf { it.isNotBlank() }
                val statusStr = formData["status"]?.content ?: ""
                val observations = formData["observations"]?.content ?: ""

                
                if (vaccineTypeStr.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Tipo de vacina é obrigatório"
                    )
                    return@launch
                }

                if (statusStr.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Status é obrigatório"
                    )
                    return@launch
                }
                
                if (totalDoses < 1) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Número de doses deve ser pelo menos 1"
                    )
                    return@launch
                }

                val status = try {
                    VaccinationStatus.valueOf(statusStr)
                } catch (e: IllegalArgumentException) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Status inválido"
                    )
                    return@launch
                }

                val currentTime = Clock.System.now().toEpochMilliseconds().toString()
                val updatedVaccination = Vaccination(
                    id = vaccinationId,
                    petId = "", 
                    veterinarianId = "", 
                    vaccineType = vaccineTypeStr.let { VaccineType.valueOf(it) },
                    vaccinationDate = vaccinationDate,
                    nextDoseDate = nextDoseDate,
                    totalDoses = totalDoses,
                    manufacturer = manufacturer,
                    observations = observations,
                    status = status,
                    createdAt = "", 
                    updatedAt = currentTime
                )

                
                updateVaccinationUseCase(updatedVaccination).fold(
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
                            errorMessage = error.message ?: "Erro ao atualizar vacinação"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro inesperado ao atualizar vacinação"
                )
            }
        }
    }

    private fun clearState() {
        _uiState.value = UpdateVaccinationUiState()
    }
}