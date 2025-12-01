package edu.fatec.petwise.features.medications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.medications.domain.models.Medication
import edu.fatec.petwise.features.medications.domain.models.MedicationFilterOptions
import edu.fatec.petwise.features.medications.domain.usecases.MedicationUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MedicationsUiState(
    val medications: List<Medication> = emptyList(),
    val filteredMedications: List<Medication> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val filterOptions: MedicationFilterOptions = MedicationFilterOptions(),
    val searchQuery: String = "",
    val showAddMedicationDialog: Boolean = false,
    val selectedMedication: Medication? = null
)

sealed class MedicationsUiEvent {
    object LoadMedications : MedicationsUiEvent()
    data class SearchMedications(val query: String) : MedicationsUiEvent()
    data class FilterMedications(val options: MedicationFilterOptions) : MedicationsUiEvent()
    data class SelectMedication(val medication: Medication?) : MedicationsUiEvent()
    data class DeleteMedication(val medicationId: String) : MedicationsUiEvent()
    data class MarkAsCompleted(val medicationId: String) : MedicationsUiEvent()
    data class PauseMedication(val medicationId: String) : MedicationsUiEvent()
    data class ResumeMedication(val medicationId: String) : MedicationsUiEvent()
    object ShowAddMedicationDialog : MedicationsUiEvent()
    object HideAddMedicationDialog : MedicationsUiEvent()
    object ClearError : MedicationsUiEvent()
}

class MedicationsViewModel(
    private val medicationUseCases: MedicationUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationsUiState())
    val uiState: StateFlow<MedicationsUiState> = _uiState.asStateFlow()

    init {
        loadMedications()
    }

    fun onEvent(event: MedicationsUiEvent) {
        when (event) {
            is MedicationsUiEvent.LoadMedications -> loadMedications()
            is MedicationsUiEvent.SearchMedications -> searchMedications(event.query)
            is MedicationsUiEvent.FilterMedications -> filterMedications(event.options)
            is MedicationsUiEvent.SelectMedication -> selectMedication(event.medication)
            is MedicationsUiEvent.DeleteMedication -> deleteMedication(event.medicationId)
            is MedicationsUiEvent.MarkAsCompleted -> markAsCompleted(event.medicationId)
            is MedicationsUiEvent.PauseMedication -> pauseMedication(event.medicationId)
            is MedicationsUiEvent.ResumeMedication -> resumeMedication(event.medicationId)
            is MedicationsUiEvent.ShowAddMedicationDialog -> showAddMedicationDialog()
            is MedicationsUiEvent.HideAddMedicationDialog -> hideAddMedicationDialog()
            is MedicationsUiEvent.ClearError -> clearError()
        }
    }

    private fun loadMedications() {
        viewModelScope.launch {
            println("Iniciando carregamento de medicamentos...")
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                medicationUseCases.getMedications().collect { medications ->
                    println("Medicamentos carregados com sucesso: ${medications.size} medicamentos encontrados")
                    _uiState.value = _uiState.value.copy(
                        medications = medications,
                        filteredMedications = medications,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                println("Erro ao carregar medicamentos: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao carregar medicamentos"
                )
            }
        }
    }

    private fun searchMedications(query: String) {
        viewModelScope.launch {
            println("Buscando medicamentos com query: '$query'")
            _uiState.value = _uiState.value.copy(
                searchQuery = query,
                isLoading = true
            )

            try {
                if (query.isBlank()) {
                    _uiState.value = _uiState.value.copy(
                        filteredMedications = _uiState.value.medications,
                        isLoading = false
                    )
                } else {
                    medicationUseCases.getMedications.searchMedications(query).collect { filteredMedications ->
                        println("Busca concluída: ${filteredMedications.size} medicamentos encontrados")
                        _uiState.value = _uiState.value.copy(
                            filteredMedications = filteredMedications,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                println("Erro na busca de medicamentos: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro na busca"
                )
            }
        }
    }

    private fun filterMedications(options: MedicationFilterOptions) {
        viewModelScope.launch {
            println("Aplicando filtros nos medicamentos")
            _uiState.value = _uiState.value.copy(
                filterOptions = options,
                isLoading = true
            )

            try {
                medicationUseCases.getMedications.filterMedications(options).collect { filteredMedications ->
                    println("Filtros aplicados: ${filteredMedications.size} medicamentos encontrados")
                    _uiState.value = _uiState.value.copy(
                        filteredMedications = filteredMedications,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                println("Erro ao filtrar medicamentos: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro ao filtrar medicamentos"
                )
            }
        }
    }

    private fun deleteMedication(medicationId: String) {
        viewModelScope.launch {
            println("Excluindo medicamento: $medicationId")
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                medicationUseCases.deleteMedication(medicationId).fold(
                    onSuccess = {
                        println("Medicamento excluído com sucesso")
                        loadMedications() 
                    },
                    onFailure = { error ->
                        println("Erro ao excluir medicamento: ${error.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao excluir medicamento"
                        )
                    }
                )
            } catch (e: Exception) {
                println("Exceção ao excluir medicamento: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    private fun markAsCompleted(medicationId: String) {
        viewModelScope.launch {
            println("Marcando medicamento como concluído: $medicationId")

            try {
                medicationUseCases.markAsCompleted(medicationId).fold(
                    onSuccess = { updatedMedication ->
                        println("Medicamento marcado como concluído com sucesso")
                        loadMedications() 
                    },
                    onFailure = { error ->
                        println("Erro ao marcar medicamento como concluído: ${error.message}")
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message ?: "Erro ao marcar como concluído"
                        )
                    }
                )
            } catch (e: Exception) {
                println("Exceção ao marcar medicamento como concluído: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    private fun pauseMedication(medicationId: String) {
        viewModelScope.launch {
            println("Pausando medicamento: $medicationId")

            try {
                medicationUseCases.pauseMedication(medicationId).fold(
                    onSuccess = { updatedMedication ->
                        println("Medicamento pausado com sucesso")
                        loadMedications() 
                    },
                    onFailure = { error ->
                        println("Erro ao pausar medicamento: ${error.message}")
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message ?: "Erro ao pausar medicamento"
                        )
                    }
                )
            } catch (e: Exception) {
                println("Exceção ao pausar medicamento: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    private fun resumeMedication(medicationId: String) {
        viewModelScope.launch {
            println("Retomando medicamento: $medicationId")

            try {
                medicationUseCases.resumeMedication(medicationId).fold(
                    onSuccess = { updatedMedication ->
                        println("Medicamento retomado com sucesso")
                        loadMedications() 
                    },
                    onFailure = { error ->
                        println("Erro ao retomar medicamento: ${error.message}")
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.message ?: "Erro ao retomar medicamento"
                        )
                    }
                )
            } catch (e: Exception) {
                println("Exceção ao retomar medicamento: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    private fun selectMedication(medication: Medication?) {
        _uiState.value = _uiState.value.copy(selectedMedication = medication)
    }

    private fun showAddMedicationDialog() {
        _uiState.value = _uiState.value.copy(showAddMedicationDialog = true)
    }

    private fun hideAddMedicationDialog() {
        _uiState.value = _uiState.value.copy(showAddMedicationDialog = false)
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}