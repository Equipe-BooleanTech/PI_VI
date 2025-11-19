package edu.fatec.petwise.features.consultas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.models.ConsultaType
import edu.fatec.petwise.features.consultas.domain.usecases.UpdateConsultaUseCase
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.core.data.DataRefreshManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UpdateConsultaUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class UpdateConsultaUiEvent {
    data class UpdateConsulta(
        val id: String,
        val petId: String,
        val petName: String,
        val veterinarianName: String,
        val consultaType: ConsultaType,
        val consultaDate: kotlinx.datetime.LocalDateTime,
        val consultaTime: String,
        val symptoms: String,
        val diagnosis: String,
        val treatment: String,
        val prescriptions: String,
        val notes: String,
        val nextAppointment: kotlinx.datetime.LocalDateTime?,
        val price: String,
    ) : UpdateConsultaUiEvent()
    object ClearState : UpdateConsultaUiEvent()
}

class UpdateConsultaViewModel(
    private val updateConsultaUseCase: UpdateConsultaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateConsultaUiState())
    val uiState: StateFlow<UpdateConsultaUiState> = _uiState.asStateFlow()

    init {
            observeLogout()
        }

    private fun observeLogout() {
        viewModelScope.launch {
            DataRefreshManager.refreshEvents.collect { event ->
                if (event is DataRefreshEvent.UserLoggedOut) {
                    println("UpdateConsultaViewModel: Usuário deslogou — limpando estado")
                    clearState()
                }
            }
        }
    } 
    fun onEvent(event: UpdateConsultaUiEvent) {
        when (event) {
            is UpdateConsultaUiEvent.UpdateConsulta -> updateConsulta(event)
            is UpdateConsultaUiEvent.ClearState -> clearState()
        }
    }

    private fun updateConsulta(event: UpdateConsultaUiEvent.UpdateConsulta) {
        viewModelScope.launch {
            _uiState.value = UpdateConsultaUiState(isLoading = true)

            try {
                val priceValue = event.price.toFloatOrNull()

                if (priceValue == null || priceValue < 0) {
                    _uiState.value = UpdateConsultaUiState(
                        isLoading = false,
                        errorMessage = "Preço deve ser um número válido maior ou igual a 0"
                    )
                    return@launch
                }

                val consulta = Consulta(
                    id = event.id,
                    petId = event.petId,
                    petName = event.petName,
                    veterinarianName = event.veterinarianName,
                    consultaType = event.consultaType,
                    consultaDate = event.consultaDate,
                    consultaTime = event.consultaTime,
                    status = edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus.SCHEDULED,
                    symptoms = event.symptoms,
                    diagnosis = event.diagnosis,
                    treatment = event.treatment,
                    prescriptions = event.prescriptions,
                    notes = event.notes,
                    nextAppointment = event.nextAppointment,
                    price = priceValue,
                    isPaid = false,
                    createdAt = "", 
                    updatedAt = "" 
                )

                updateConsultaUseCase(consulta).fold(
                    onSuccess = {
                        println("Consulta atualizada com sucesso")
                        _uiState.value = UpdateConsultaUiState(
                            isLoading = false,
                            isSuccess = true
                        )
                    },
                    onFailure = { error ->
                        println("Erro ao atualizar consulta: ${error.message}")
                        _uiState.value = UpdateConsultaUiState(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao atualizar consulta"
                        )
                    }
                )
            } catch (e: Exception) {
                println("Exceção ao atualizar consulta: ${e.message}")
                _uiState.value = UpdateConsultaUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido ao atualizar consulta"
                )
            }
        }
    }

    private fun clearState() {
        _uiState.value = UpdateConsultaUiState()
    }
}
