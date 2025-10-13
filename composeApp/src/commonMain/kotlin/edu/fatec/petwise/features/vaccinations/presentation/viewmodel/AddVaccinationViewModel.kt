package edu.fatec.petwise.features.vaccinations.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.vaccinations.domain.models.VaccineType
import edu.fatec.petwise.features.vaccinations.domain.models.VaccinationStatus
import edu.fatec.petwise.features.vaccinations.domain.usecases.AddVaccinationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddVaccinationUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class AddVaccinationUiEvent {
    data class AddVaccination(
        val petId: String,
        val petName: String,
        val vaccineName: String,
        val vaccineType: VaccineType,
        val applicationDate: String,
        val nextDoseDate: String?,
        val doseNumber: String,
        val totalDoses: String,
        val veterinarianName: String,
        val veterinarianCrmv: String,
        val clinicName: String,
        val batchNumber: String,
        val manufacturer: String,
        val observations: String
    ) : AddVaccinationUiEvent()
    object ClearState : AddVaccinationUiEvent()
}

class AddVaccinationViewModel(
    private val addVaccinationUseCase: AddVaccinationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddVaccinationUiState())
    val uiState: StateFlow<AddVaccinationUiState> = _uiState.asStateFlow()

    fun onEvent(event: AddVaccinationUiEvent) {
        when (event) {
            is AddVaccinationUiEvent.AddVaccination -> addVaccination(event)
            is AddVaccinationUiEvent.ClearState -> clearState()
        }
    }

    private fun addVaccination(event: AddVaccinationUiEvent.AddVaccination) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val doseNumber = event.doseNumber.toIntOrNull()
                val totalDoses = event.totalDoses.toIntOrNull()

                if (doseNumber == null || doseNumber <= 0) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Número da dose deve ser um valor válido maior que 0"
                    )
                    return@launch
                }

                if (totalDoses == null || totalDoses <= 0) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Total de doses deve ser um valor válido maior que 0"
                    )
                    return@launch
                }

                if (doseNumber > totalDoses) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Número da dose não pode ser maior que o total de doses"
                    )
                    return@launch
                }

                val vaccination = Vaccination(
                    id = "",
                    petId = event.petId,
                    petName = event.petName,
                    vaccineName = event.vaccineName,
                    vaccineType = event.vaccineType,
                    applicationDate = event.applicationDate,
                    nextDoseDate = event.nextDoseDate,
                    doseNumber = doseNumber,
                    totalDoses = totalDoses,
                    veterinarianName = event.veterinarianName,
                    veterinarianCrmv = event.veterinarianCrmv,
                    clinicName = event.clinicName,
                    batchNumber = event.batchNumber,
                    manufacturer = event.manufacturer,
                    observations = event.observations,
                    sideEffects = "",
                    status = VaccinationStatus.AGENDADA,
                    createdAt = "",
                    updatedAt = ""
                )

                addVaccinationUseCase(vaccination).fold(
                    onSuccess = { addedVaccination ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao adicionar vacinação"
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
        _uiState.value = AddVaccinationUiState()
    }
}
