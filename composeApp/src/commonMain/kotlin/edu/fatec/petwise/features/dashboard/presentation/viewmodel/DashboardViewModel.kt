package edu.fatec.petwise.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.dashboard.domain.usecases.GetCardsStatisticsUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetUpcomingConsultasUseCase
import edu.fatec.petwise.features.dashboard.domain.usecases.GetUserNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val userName: String = "",
    val petCount: Int = 0,
    val consultasCount: Int = 0,
    val vacinasCount: Int = 0,
    val medicamentosCount: Int = 0,
    val upcomingConsultas: List<Consulta> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class DashboardUiEvent {
    object LoadDashboard : DashboardUiEvent()
    object RefreshDashboard : DashboardUiEvent()
}

class DashboardViewModel(
    private val getCardsStatisticsUseCase: GetCardsStatisticsUseCase,
    private val getUpcomingConsultasUseCase: GetUpcomingConsultasUseCase,
    private val getUserNameUseCase: GetUserNameUseCase
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
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            println("Carregando dados do dashboard...")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val userNameResult = getUserNameUseCase()
                val userName = userNameResult.getOrNull() ?: ""
                
                val statistics = getCardsStatisticsUseCase()
                val petCount = statistics.getOrElse(0) { 0 }
                val consultasCount = statistics.getOrElse(1) { 0 }
                val vacinasCount = statistics.getOrElse(2) { 0 }
                val medicamentosCount = statistics.getOrElse(3) { 0 }

                println("Estatísticas carregadas: pets=$petCount, consultas=$consultasCount, vacinas=$vacinasCount, medicamentos=$medicamentosCount, userName=$userName")

                val consultas = getUpcomingConsultasUseCase()
                println("Consultas próximas carregadas: ${consultas.size}")
                
                _uiState.value = _uiState.value.copy(
                    userName = userName,
                    petCount = petCount,
                    consultasCount = consultasCount,
                    vacinasCount = vacinasCount,
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
}