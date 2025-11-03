package edu.fatec.petwise.features.consultas.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus
import edu.fatec.petwise.features.consultas.domain.models.ConsultaType
import edu.fatec.petwise.features.consultas.domain.usecases.AddConsultaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddConsultaUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class AddConsultaUiEvent {
    data class AddConsulta(
        val petId: String,
        val petName: String,
        val consultaType: ConsultaType,
        val consultaDate: String,
        val consultaTime: String,
        val symptoms: String,
        val notes: String,
        val price: String
    ) : AddConsultaUiEvent()
    object ClearState : AddConsultaUiEvent()
}

class AddConsultaViewModel(
    private val addConsultaUseCase: AddConsultaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddConsultaUiState())
    val uiState: StateFlow<AddConsultaUiState> = _uiState.asStateFlow()

    fun onEvent(event: AddConsultaUiEvent) {
        when (event) {
            is AddConsultaUiEvent.AddConsulta -> addConsulta(event)
            is AddConsultaUiEvent.ClearState -> clearState()
        }
    }

    private fun addConsulta(event: AddConsultaUiEvent.AddConsulta) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val priceValue = event.price.toFloatOrNull()

                if (priceValue == null || priceValue < 0) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Preço deve ser um número válido maior ou igual a 0"
                    )
                    return@launch
                }

                val consulta = Consulta(
                    id = "",
                    petId = event.petId,
                    petName = event.petName,
                    veterinarianName = "",
                    consultaType = event.consultaType,
                    consultaDate = event.consultaDate,
                    consultaTime = event.consultaTime,
                    status = ConsultaStatus.SCHEDULED,
                    symptoms = event.symptoms,
                    diagnosis = "",
                    treatment = "",
                    prescriptions = "",
                    notes = event.notes,
                    nextAppointment = null,
                    price = priceValue,
                    isPaid = false,
                    createdAt = "",
                    updatedAt = ""
                )

                addConsultaUseCase(consulta).fold(
                    onSuccess = { addedConsulta ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            errorMessage = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao adicionar consulta"
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
        _uiState.value = AddConsultaUiState()
    }
}
