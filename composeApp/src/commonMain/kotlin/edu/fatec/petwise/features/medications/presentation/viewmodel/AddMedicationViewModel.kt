package edu.fatec.petwise.features.medications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.medications.domain.models.Medication
import edu.fatec.petwise.features.medications.domain.usecases.AddMedicationUseCase
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

data class AddMedicationUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val prescriptions: List<Prescription> = emptyList(),
    val prescriptionsLoading: Boolean = false,
    val currentUserName: String = "",
    val currentUserId: String = "",
    val pets: List<Pet> = emptyList(),
    val petsLoading: Boolean = false
)

class AddMedicationViewModel(
    private val addMedicationUseCase: AddMedicationUseCase,
    private val getPrescriptionsUseCase: GetPrescriptionsUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getPetsUseCase: GetPetsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddMedicationUiState())
    val uiState: StateFlow<AddMedicationUiState> = _uiState.asStateFlow()

    init {
        loadPrescriptions()
        loadCurrentUserName()
        loadPets()
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

    private fun loadCurrentUserName() {
        viewModelScope.launch {
            try {
                getUserProfileUseCase.execute().fold(
                    onSuccess = { profile ->
                        _uiState.value = _uiState.value.copy(
                            currentUserName = profile.fullName ?: "",
                            currentUserId = profile.id ?: ""
                        )
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

    fun addMedication(formData: Map<String, Any>) {
        viewModelScope.launch {
            println("Iniciando adição de novo medicamento")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val prescriptionIdRaw = formData["prescriptionId"] as? String ?: ""
            if (prescriptionIdRaw.isEmpty()) {
                println("Erro: Prescrição não selecionada")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Selecione uma prescrição"
                )
                return@launch
            }

            val prescriptionId = prescriptionIdRaw.split(" | ").getOrNull(0) ?: ""
            if (prescriptionId.isEmpty()) {
                println("Erro: ID da prescrição inválido")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Prescrição inválida"
                )
                return@launch
            }

            try {
                val medicationName = formData["medicationName"] as? String ?: ""
                val dosage = formData["dosage"] as? String ?: ""
                val frequency = formData["frequency"] as? String ?: ""
                val durationDaysStr = (formData["durationDays"] as? Int)?.toString() ?: (formData["durationDays"] as? String ?: "0")
                val startDateStr = formData["startDate"] as? String ?: ""
                val endDateStr = formData["endDate"] as? String ?: ""
                val sideEffects = formData["sideEffects"] as? String ?: ""

                val durationDays = durationDaysStr.toInt()

                // Fix date strings by adding seconds if missing
                val fixedStartDateStr = startDateStr.replace(Regex("T(\\d{2}:\\d{2})\\.(\\d{3})"), "T$1:00.$2")
                val fixedEndDateStr = endDateStr.replace(Regex("T(\\d{2}:\\d{2})\\.(\\d{3})"), "T$1:00.$2")

                // Parse to LocalDateTime
                val startDate = LocalDateTime.parse(fixedStartDateStr)
                val endDate = LocalDateTime.parse(fixedEndDateStr)

                // Generate current timestamp
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

                val medication = Medication(
                    id = "", // Will be generated by the API
                    userId = _uiState.value.currentUserId,
                    prescriptionId = prescriptionId,
                    medicationName = medicationName,
                    dosage = dosage,
                    frequency = frequency,
                    durationDays = durationDays,
                    startDate = startDate,
                    endDate = endDate,
                    sideEffects = sideEffects,
                    createdAt = now,
                    updatedAt = now
                )

                println("Salvando novo medicamento: nome=${medication.medicationName}, dosagem=${medication.dosage}")

                addMedicationUseCase(medication).fold(
                    onSuccess = { addedMedication ->
                        println("Medicamento adicionado com sucesso: ${addedMedication.medicationName} (ID: ${addedMedication.id})")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        println("Erro ao adicionar medicamento: ${error.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao adicionar medicamento"
                        )
                    }
                )
            } catch (e: Exception) {
                println("Exceção durante adição do medicamento: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    fun clearState() {
        println("Limpando estado do AddMedicationViewModel")
        _uiState.value = AddMedicationUiState().copy(prescriptions = _uiState.value.prescriptions, currentUserName = _uiState.value.currentUserName, currentUserId = _uiState.value.currentUserId, pets = _uiState.value.pets)
    }
}