package edu.fatec.petwise.features.pets.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.medications.domain.models.Medication
import edu.fatec.petwise.features.exams.domain.models.Exam
import edu.fatec.petwise.features.prescriptions.domain.models.Prescription
import edu.fatec.petwise.features.pets.domain.usecases.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PetDetailsUiState(
    val consultations: List<Consulta> = emptyList(),
    val vaccinations: List<Vaccination> = emptyList(),
    val medications: List<Medication> = emptyList(),
    val exams: List<Exam> = emptyList(),
    val prescriptions: List<Prescription> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class PetDetailsUiEvent {
    data class LoadPetDetails(val petId: String) : PetDetailsUiEvent()
    object ClearError : PetDetailsUiEvent()
}

class PetDetailsViewModel(
    private val getConsultasByPetUseCase: GetConsultasByPetUseCase,
    private val getVaccinationsByPetUseCase: GetVaccinationsByPetUseCase,
    private val getMedicationsByPetUseCase: GetMedicationsByPetUseCase,
    private val getExamsByPetUseCase: GetExamsByPetUseCase,
    private val getPrescriptionsByPetUseCase: GetPrescriptionsByPetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetDetailsUiState())
    val uiState: StateFlow<PetDetailsUiState> = _uiState.asStateFlow()

    fun onEvent(event: PetDetailsUiEvent) {
        when (event) {
            is PetDetailsUiEvent.LoadPetDetails -> loadPetDetails(event.petId)
            is PetDetailsUiEvent.ClearError -> clearError()
        }
    }

    private fun loadPetDetails(petId: String) {
        viewModelScope.launch {
            println("Carregando detalhes do pet: $petId")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                coroutineScope {
                    // Load all pet-related data in parallel
                    val consultationsDeferred = async { getConsultasByPetUseCase(petId) }
                    val vaccinationsDeferred = async { getVaccinationsByPetUseCase(petId) }
                    val medicationsDeferred = async { getMedicationsByPetUseCase(petId) }
                    val examsDeferred = async { getExamsByPetUseCase(petId) }
                    val prescriptionsDeferred = async { getPrescriptionsByPetUseCase(petId) }

                    // Wait for all to complete
                    val consultations = consultationsDeferred.await()
                    val vaccinations = vaccinationsDeferred.await()
                    val medications = medicationsDeferred.await()
                    val exams = examsDeferred.await()
                    val prescriptions = prescriptionsDeferred.await()

                    println("Dados do pet carregados: consultas=${consultations.size}, vacinas=${vaccinations.size}, medicações=${medications.size}, exames=${exams.size}, prescrições=${prescriptions.size}")

                    _uiState.value = _uiState.value.copy(
                        consultations = consultations,
                        vaccinations = vaccinations,
                        medications = medications,
                        exams = exams,
                        prescriptions = prescriptions,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                println("Erro ao carregar detalhes do pet: ${e.message}")

                val errorMessage = when {
                    e.message?.contains("Token expirado") == true ||
                    e.message?.contains("Sessão expirada") == true ||
                    e.message?.contains("deve estar logado") == true -> {
                        println("PetDetailsViewModel: Erro de autenticação detectado - ${e.message}")
                        "Sua sessão expirou. Faça login novamente."
                    }
                    else -> e.message ?: "Erro ao carregar dados do pet"
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage
                )
            }
        }
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}