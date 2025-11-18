package edu.fatec.petwise.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus
import edu.fatec.petwise.features.consultas.domain.usecases.UpdateConsultaStatusUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetCardsStatisticsUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetUpcomingConsultasUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetUserNameUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetUserTypeUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetPrescriptionsCountUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetExamsCountUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetLabsCountUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetFoodCountUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetHygieneCountUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetToysCountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val userName: String = "",
    val userType: String = "OWNER",
    val petCount: Int = 0,
    val consultasCount: Int = 0,
    val vacinasCount: Int = 0,
    val medicamentosCount: Int = 0,
    val prescriptionsCount: Int = 0,
    val examsCount: Int = 0,
    val labsCount: Int = 0,
    val foodCount: Int = 0,
    val hygieneCount: Int = 0,
    val toysCount: Int = 0,
    val upcomingConsultas: List<Consulta> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class DashboardUiEvent {
    object LoadDashboard : DashboardUiEvent()
    object RefreshDashboard : DashboardUiEvent()
    data class CancelConsulta(val consultaId: String) : DashboardUiEvent()
}

class DashboardViewModel(
    private val getCardsStatisticsUseCase: GetCardsStatisticsUseCase,
    private val getUpcomingConsultasUseCase: GetUpcomingConsultasUseCase,
    private val getUserNameUseCase: GetUserNameUseCase,
    private val getUserTypeUseCase: GetUserTypeUseCase,
    private val getPrescriptionsCountUseCase: GetPrescriptionsCountUseCase,
    private val getExamsCountUseCase: GetExamsCountUseCase,
    private val getLabsCountUseCase: GetLabsCountUseCase,
    private val getFoodCountUseCase: GetFoodCountUseCase,
    private val getHygieneCountUseCase: GetHygieneCountUseCase,
    private val getToysCountUseCase: GetToysCountUseCase,
    private val updateConsultaStatusUseCase: UpdateConsultaStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeDataRefresh()
    }

    private fun observeDataRefresh() {
        viewModelScope.launch {
            DataRefreshManager.refreshEvents.collect { event ->
                when (event) {
                    is DataRefreshEvent.PetsUpdated,
                    is DataRefreshEvent.ConsultasUpdated,
                    is DataRefreshEvent.VaccinationsUpdated -> loadDashboard()
                    is DataRefreshEvent.AllDataUpdated -> {
                        _uiState.value = DashboardUiState()
                        println("DashboardViewModel: Estado limpo após logout")
                    }
                    else -> {}
                }
            }
        }
    }

    fun onEvent(event: DashboardUiEvent) {
        when (event) {
            is DashboardUiEvent.LoadDashboard -> loadDashboard()
            is DashboardUiEvent.RefreshDashboard -> refreshDashboard()
            is DashboardUiEvent.CancelConsulta -> cancelConsulta(event.consultaId)
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            println("Carregando dados do dashboard...")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val userNameResult = getUserNameUseCase()
                val userName = userNameResult.getOrNull() ?: ""
                
                val userTypeResult = getUserTypeUseCase()
                val rawUserType = userTypeResult.getOrNull() ?: "OWNER"
                
                // Normalize user type the same way as HomeScreen
                val userType = when (rawUserType.uppercase()) {
                    "VETERINARY", "VETERINARIAN", "VET" -> "VETERINARY"
                    "PETSHOP" -> "PETSHOP"
                    "PHARMACY" -> "PHARMACY"
                    else -> "OWNER"
                }
                
                println("DashboardViewModel - getUserTypeUseCase result: success=${userTypeResult.isSuccess}, rawUserType=$rawUserType, normalizedUserType=$userType")
                
                val statistics = getCardsStatisticsUseCase(userType)
                val petCount = statistics.getOrElse(0) { 0 }
                val consultasCount = statistics.getOrElse(1) { 0 }
                val vacinasCount = statistics.getOrElse(2) { 0 }
                val medicamentosCount = statistics.getOrElse(3) { 0 }

                // Get additional counts based on user type
                val prescriptionsCount = when (userType) {
                    "VETERINARY" -> getPrescriptionsCountUseCase()
                    else -> 0
                }
                val examsCount = when (userType) {
                    "VETERINARY" -> getExamsCountUseCase()
                    else -> 0
                }
                val labsCount = when (userType) {
                    "VETERINARY" -> getLabsCountUseCase()
                    else -> 0
                }
                val foodCount = when (userType) {
                    "PETSHOP" -> getFoodCountUseCase()
                    else -> 0
                }
                val hygieneCount = when (userType) {
                    "PETSHOP" -> getHygieneCountUseCase()
                    else -> 0
                }
                val toysCount = when (userType) {
                    "PETSHOP" -> getToysCountUseCase()
                    else -> 0
                }

                println("Estatísticas carregadas: pets=$petCount, consultas=$consultasCount, vacinas=$vacinasCount, medicamentos=$medicamentosCount, prescriptions=$prescriptionsCount, exams=$examsCount, labs=$labsCount, food=$foodCount, hygiene=$hygieneCount, toys=$toysCount, userName=$userName, userType=$userType")

                val consultas = getUpcomingConsultasUseCase(userType)
                println("Consultas próximas carregadas: ${consultas.size}")
                
                _uiState.value = _uiState.value.copy(
                    userName = userName,
                    userType = userType,
                    petCount = petCount,
                    consultasCount = consultasCount,
                    vacinasCount = vacinasCount,
                    medicamentosCount = medicamentosCount,
                    prescriptionsCount = prescriptionsCount,
                    examsCount = examsCount,
                    labsCount = labsCount,
                    foodCount = foodCount,
                    hygieneCount = hygieneCount,
                    toysCount = toysCount,
                    upcomingConsultas = consultas,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                println("Erro ao carregar dashboard: ${e.message}")

                val errorMessage = when {
                    e.message?.contains("Token expirado") == true ||
                            e.message?.contains("Sessão expirada") == true ||
                            e.message?.contains("deve estar logado") == true -> {
                        println("DashboardViewModel: Erro de autenticação detectado - ${e.message}")
                        "Sua sessão expirou. Faça login novamente."
                    }
                    else -> e.message ?: "Erro ao carregar dados do dashboard"
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMessage
                )
            }
        }
    }

    private fun refreshDashboard() {
        println("Atualizando dados do dashboard...")
        loadDashboard()
    }

    private fun cancelConsulta(consultaId: String) {
        viewModelScope.launch {
            println("Cancelando consulta: $consultaId")
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val result = updateConsultaStatusUseCase(consultaId, ConsultaStatus.CANCELLED)
                result.fold(
                    onSuccess = { consulta ->
                        println("Consulta cancelada com sucesso: ${consulta.id}")
                        // Refresh dashboard to update the data
                        loadDashboard()
                    },
                    onFailure = { error ->
                        println("Erro ao cancelar consulta: ${error.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Erro ao cancelar consulta: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                println("Erro ao cancelar consulta: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao cancelar consulta: ${e.message}"
                )
            }
        }
    }
}