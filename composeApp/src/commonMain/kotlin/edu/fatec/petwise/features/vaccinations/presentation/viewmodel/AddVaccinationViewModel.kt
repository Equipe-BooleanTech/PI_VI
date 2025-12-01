package edu.fatec.petwise.features.vaccinations.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.vaccinations.domain.models.VaccineType
import edu.fatec.petwise.features.vaccinations.domain.models.VaccinationStatus
import edu.fatec.petwise.features.vaccinations.domain.usecases.AddVaccinationUseCase
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.core.data.DataRefreshManager
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
        val vaccineType: VaccineType,
        val vaccinationDate: kotlinx.datetime.LocalDateTime,
        val nextDoseDate: kotlinx.datetime.LocalDateTime?,
        val totalDoses: String,
        val manufacturer: String,
        val observations: String
    ) : AddVaccinationUiEvent()
    object ClearState : AddVaccinationUiEvent()
}

class AddVaccinationViewModel(
    private val addVaccinationUseCase: AddVaccinationUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddVaccinationUiState())
    val uiState: StateFlow<AddVaccinationUiState> = _uiState.asStateFlow()

    init {
        observeLogout()
    }

    private fun observeLogout() {
        viewModelScope.launch {
            DataRefreshManager.refreshEvents.collect { event ->
                if (event is DataRefreshEvent.UserLoggedOut) {
                    println("AddVaccinationViewModel: Usuário deslogou — limpando estado")
                    clearState()
                }
            }
        }
    }
    
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
                
                val userProfileResult = getUserProfileUseCase.execute()
                val veterinarianId = userProfileResult.fold(
                    onSuccess = { profile -> profile.id },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Erro ao obter perfil do usuário: ${error.message}"
                        )
                        return@launch
                    }
                )

                val totalDoses = event.totalDoses.toIntOrNull()

                if (totalDoses == null || totalDoses <= 0) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Total de doses deve ser um valor válido maior que 0"
                    )
                    return@launch
                }

                val vaccination = Vaccination(
                    id = "",
                    petId = event.petId,
                    veterinarianId = veterinarianId,
                    vaccineType = event.vaccineType,
                    vaccinationDate = event.vaccinationDate,
                    nextDoseDate = event.nextDoseDate,
                    totalDoses = totalDoses,
                    manufacturer = event.manufacturer,
                    observations = event.observations,
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
