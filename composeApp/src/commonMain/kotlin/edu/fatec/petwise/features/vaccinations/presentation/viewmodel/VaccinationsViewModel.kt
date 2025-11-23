package edu.fatec.petwise.features.vaccinations.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.vaccinations.domain.models.VaccinationFilterOptions
import edu.fatec.petwise.features.vaccinations.domain.usecases.GetVaccinationsUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.GetVaccinationsByPetIdUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.FilterVaccinationsUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.GetUpcomingVaccinationsUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.GetOverdueVaccinationsUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.DeleteVaccinationUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.MarkVaccinationAsAppliedUseCase
import edu.fatec.petwise.features.vaccinations.domain.usecases.ScheduleNextDoseUseCase
import edu.fatec.petwise.features.pets.domain.models.Pet
import edu.fatec.petwise.features.pets.domain.usecases.GetPetsUseCase
import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.core.data.DataRefreshEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class VaccinationsUiState(
    val vaccinations: List<Vaccination> = emptyList(),
    val filteredVaccinations: List<Vaccination> = emptyList(),
    val pets: List<Pet> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterOptions: VaccinationFilterOptions = VaccinationFilterOptions(),
    val selectedVaccination: Vaccination? = null,
    val showingUpcoming: Boolean = false,
    val showingOverdue: Boolean = false
)

sealed class VaccinationsUiEvent {
    object LoadVaccinations : VaccinationsUiEvent()
    data class LoadVaccinationsByPet(val petId: String) : VaccinationsUiEvent()
    data class FilterVaccinations(val options: VaccinationFilterOptions) : VaccinationsUiEvent()
    object LoadUpcomingVaccinations : VaccinationsUiEvent()
    object LoadOverdueVaccinations : VaccinationsUiEvent()
    data class DeleteVaccination(val id: String) : VaccinationsUiEvent()
    data class MarkAsApplied(val id: String, val observations: String) : VaccinationsUiEvent()
    data class ScheduleNextDose(val id: String, val nextDoseDate: String) : VaccinationsUiEvent()
    data class SelectVaccination(val vaccination: Vaccination?) : VaccinationsUiEvent()
    object ClearError : VaccinationsUiEvent()
}

class VaccinationsViewModel(
    private val getVaccinationsUseCase: GetVaccinationsUseCase,
    private val getVaccinationsByPetIdUseCase: GetVaccinationsByPetIdUseCase,
    private val filterVaccinationsUseCase: FilterVaccinationsUseCase,
    private val getUpcomingVaccinationsUseCase: GetUpcomingVaccinationsUseCase,
    private val getOverdueVaccinationsUseCase: GetOverdueVaccinationsUseCase,
    private val deleteVaccinationUseCase: DeleteVaccinationUseCase,
    private val markVaccinationAsAppliedUseCase: MarkVaccinationAsAppliedUseCase,
    private val scheduleNextDoseUseCase: ScheduleNextDoseUseCase,
    private val getPetsUseCase: GetPetsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaccinationsUiState())
    val uiState: StateFlow<VaccinationsUiState> = _uiState.asStateFlow()

    init {
        observeDataRefresh()
    }

    private fun observeDataRefresh() {
        viewModelScope.launch {
            DataRefreshManager.refreshEvents.collect { event ->
                when (event) {
                    is DataRefreshEvent.VaccinationsUpdated -> loadVaccinations()
                    is DataRefreshEvent.AllDataUpdated -> {
                        _uiState.value = VaccinationsUiState()
                        println("VaccinationsViewModel: Estado limpo após logout")
                    }
                    else -> {}
                }
            }
        }
    }
    fun onEvent(event: VaccinationsUiEvent) {
        when (event) {
            is VaccinationsUiEvent.LoadVaccinations -> loadVaccinations()
            is VaccinationsUiEvent.LoadVaccinationsByPet -> loadVaccinationsByPet(event.petId)
            is VaccinationsUiEvent.FilterVaccinations -> filterVaccinations(event.options)
            is VaccinationsUiEvent.LoadUpcomingVaccinations -> loadUpcomingVaccinations()
            is VaccinationsUiEvent.LoadOverdueVaccinations -> loadOverdueVaccinations()
            is VaccinationsUiEvent.DeleteVaccination -> deleteVaccination(event.id)
            is VaccinationsUiEvent.MarkAsApplied -> markAsApplied(event.id, event.observations)
            is VaccinationsUiEvent.ScheduleNextDose -> scheduleNextDose(event.id, event.nextDoseDate)
            is VaccinationsUiEvent.SelectVaccination -> selectVaccination(event.vaccination)
            is VaccinationsUiEvent.ClearError -> clearError()
            is DataRefreshEvent.UserLoggedOut -> {
                println("VaccinationsViewModel: Usuário deslogou — limpando estado")
                _uiState.value = VaccinationsUiState()
            }
        }
    }

    private fun loadVaccinations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, showingUpcoming = false, showingOverdue = false)
            try {
                getVaccinationsUseCase().collect { vaccinations ->
                    getPetsUseCase().collect { pets ->
                        _uiState.value = _uiState.value.copy(
                            vaccinations = vaccinations,
                            filteredVaccinations = vaccinations,
                            pets = pets,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar vacinações"
                )
            }
        }
    }

    private fun loadVaccinationsByPet(petId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, showingUpcoming = false, showingOverdue = false)
            try {
                getVaccinationsByPetIdUseCase(petId).collect { vaccinations ->
                    _uiState.value = _uiState.value.copy(
                        vaccinations = vaccinations,
                        filteredVaccinations = vaccinations,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar vacinações do pet"
                )
            }
        }
    }

    private fun filterVaccinations(options: VaccinationFilterOptions) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                filterOptions = options,
                isLoading = true,
                error = null,
                showingUpcoming = false,
                showingOverdue = false
            )
            try {
                filterVaccinationsUseCase(options).collect { vaccinations ->
                    _uiState.value = _uiState.value.copy(
                        filteredVaccinations = vaccinations,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao filtrar vacinações"
                )
            }
        }
    }

    private fun loadUpcomingVaccinations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, showingUpcoming = true, showingOverdue = false)
            try {
                getUpcomingVaccinationsUseCase(30).collect { vaccinations ->
                    _uiState.value = _uiState.value.copy(
                        filteredVaccinations = vaccinations,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar vacinações próximas"
                )
            }
        }
    }

    private fun loadOverdueVaccinations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, showingUpcoming = false, showingOverdue = true)
            try {
                getOverdueVaccinationsUseCase().collect { vaccinations ->
                    _uiState.value = _uiState.value.copy(
                        filteredVaccinations = vaccinations,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao carregar vacinações atrasadas"
                )
            }
        }
    }

    private fun deleteVaccination(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                deleteVaccinationUseCase(id).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        loadVaccinations()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Erro ao deletar vacinação"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao deletar vacinação"
                )
            }
        }
    }

    private fun markAsApplied(id: String, observations: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                markVaccinationAsAppliedUseCase(id, observations).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        loadVaccinations()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Erro ao marcar vacinação como aplicada"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao marcar vacinação como aplicada"
                )
            }
        }
    }

    private fun scheduleNextDose(id: String, nextDoseDate: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                scheduleNextDoseUseCase(id, nextDoseDate).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        loadVaccinations()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message ?: "Erro ao agendar próxima dose"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Erro ao agendar próxima dose"
                )
            }
        }
    }

    private fun selectVaccination(vaccination: Vaccination?) {
        _uiState.value = _uiState.value.copy(selectedVaccination = vaccination)
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
