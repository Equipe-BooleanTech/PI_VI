package edu.fatec.petwise.features.medications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.medications.domain.models.Medication
import edu.fatec.petwise.features.medications.domain.usecases.GetMedicationByIdUseCase
import edu.fatec.petwise.features.medications.domain.usecases.UpdateMedicationUseCase
import edu.fatec.petwise.features.prescriptions.domain.usecases.GetPrescriptionsUseCase
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase
import edu.fatec.petwise.features.pets.domain.usecases.GetPetsUseCase
import edu.fatec.petwise.features.pets.domain.models.Pet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Clock

data class UpdateMedicationUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val medication: Medication? = null,
    val prescriptions: List<Prescription> = emptyList(),
    val prescriptionsLoading: Boolean = false,
    val currentUserName: String = "",
    val pets: List<Pet> = emptyList(),
    val petsLoading: Boolean = false
)

class UpdateMedicationViewModel(
    private val updateMedicationUseCase: UpdateMedicationUseCase,
    private val getMedicationByIdUseCase: GetMedicationByIdUseCase,
    private val getPrescriptionsUseCase: GetPrescriptionsUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getPetsUseCase: GetPetsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateMedicationUiState())
    val uiState: StateFlow<UpdateMedicationUiState> = _uiState.asStateFlow()

    init {
        loadPrescriptions()
        loadCurrentUserName()
        loadPets()
    }

    private fun loadCurrentUserName() {
        viewModelScope.launch {
            try {
                getUserProfileUseCase.execute().fold(
                    onSuccess = { profile ->
                        _uiState.value = _uiState.value.copy(currentUserName = profile.fullName ?: "")
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(errorMessage = "Erro ao carregar perfil: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Erro ao carregar perfil: ${e.message}")
            }
        }
    }

    private fun loadPets() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(petsLoading = true)
            try {
                getPetsUseCase().collect { pets ->
                    _uiState.value = _uiState.value.copy(
                        pets = pets,
                        petsLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    petsLoading = false,
                    errorMessage = "Erro ao carregar pets: ${e.message}"
                )
            }
        }
    }

    private fun loadPrescriptions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(prescriptionsLoading = true)
            try {
                getPrescriptionsUseCase().collect { prescriptions ->
                    _uiState.value = _uiState.value.copy(
                        prescriptions = prescriptions,
                        prescriptionsLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    prescriptionsLoading = false,
                    errorMessage = "Erro ao carregar prescrições: ${e.message}"
                )
            }
        }
    }

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

                val prescriptionIdRaw = formData["prescriptionId"] as? String ?: currentMedication.prescriptionId
                if (prescriptionIdRaw.isEmpty()) {
                    println("Erro: Prescrição não selecionada")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Selecione uma prescrição"
                    )
                    return@launch
                }

                val prescriptionId = prescriptionIdRaw.split(" | ").getOrNull(0) ?: currentMedication.prescriptionId
                if (prescriptionId.isEmpty()) {
                    println("Erro: ID da prescrição inválido")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Prescrição inválida"
                    )
                    return@launch
                }

                val medicationName = formData["medicationName"] as? String ?: currentMedication.medicationName
                val dosage = formData["dosage"] as? String ?: currentMedication.dosage
                val frequency = formData["frequency"] as? String ?: currentMedication.frequency
                val durationDaysStr = (formData["durationDays"] as? Int)?.toString() ?: (formData["durationDays"] as? String ?: currentMedication.durationDays.toString())
                val startDateRaw = formData["startDate"]
                val endDateRaw = formData["endDate"]
                val sideEffects = formData["sideEffects"] as? String ?: currentMedication.sideEffects

                
                val durationDays = durationDaysStr.toIntOrNull()
                if (durationDays == null || durationDays <= 0) {
                    println("Erro de validação: Duração inválida - $durationDaysStr")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Duração deve ser um número válido maior que 0"
                    )
                    return@launch
                }

                
                val startDate = when (startDateRaw) {
                    is String -> {
                        val fixed = startDateRaw.replace(Regex("T(\\d{2}:\\d{2})\\.(\\d{3})"), "T$1:00.$2")
                        LocalDateTime.parse(fixed)
                    }
                    else -> currentMedication.startDate
                }
                val endDate = when (endDateRaw) {
                    is String -> {
                        val fixed = endDateRaw.replace(Regex("T(\\d{2}:\\d{2})\\.(\\d{3})"), "T$1:00.$2")
                        LocalDateTime.parse(fixed)
                    }
                    else -> currentMedication.endDate
                }

                
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

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
        _uiState.value = UpdateMedicationUiState().copy(prescriptions = _uiState.value.prescriptions, currentUserName = _uiState.value.currentUserName, pets = _uiState.value.pets)
    }
}