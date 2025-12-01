package edu.fatec.petwise.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.core.session.SessionResetManager
import edu.fatec.petwise.features.auth.di.AuthDependencyContainer
import edu.fatec.petwise.features.auth.domain.usecases.LoginUseCase
import edu.fatec.petwise.features.auth.domain.usecases.LogoutUseCase
import edu.fatec.petwise.features.auth.domain.usecases.RegisterUseCase
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    
    private val supervisorScope = viewModelScope

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState
    
    var onLogoutComplete: (() -> Unit)? = null

    fun login(email: String, password: String) {
        supervisorScope.launch {
            println("AuthViewModel: Iniciando login para $email")
            
            println("AuthViewModel: Limpando dados do usuário anterior antes do login")
            DataRefreshManager.notifyAllDataUpdated()
            
            
            println("AuthViewModel: Resetando dependências de autenticação para login fresco")
            AuthDependencyContainer.reset()
            
            
            println("AuthViewModel: Resetando estado de autenticação para login fresco")
            _uiState.value = AuthUiState()
            
            delay(100)
            
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            val result = loginUseCase.execute(email, password)

            result.fold(
                onSuccess = { userId ->
                    println("AuthViewModel: Login bem-sucedido para userId: $userId")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        userId = userId,
                        errorMessage = null,
                        successMessage = null
                    )

                    
                    DataRefreshManager.notifyPetsUpdated()
                    DataRefreshManager.notifyConsultasUpdated()
                    DataRefreshManager.notifyVaccinationsUpdated()
                    DataRefreshManager.notifyUserLoggedIn()
                    println("AuthViewModel: Eventos de recarregamento emitidos após login")
                },
                onFailure = { error ->
                    println("AuthViewModel: Falha no login - ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        userId = null,
                        errorMessage = error.message ?: "Erro ao fazer login",
                        successMessage = null
                    )
                }
            )
        }
    }

    fun register(registerRequest: edu.fatec.petwise.core.network.dto.RegisterRequest) {
        
        supervisorScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )

            val result = registerUseCase.execute(registerRequest)

            result.fold(
                onSuccess = { userId ->
                    println("AuthViewModel: Registro concluído - usuário deve fazer login")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        userId = null,
                        errorMessage = null,
                        successMessage = "Cadastro realizado com sucesso! Faça login para continuar."
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Erro ao registrar",
                        successMessage = null
                    )
                }
            )
        }
    }

    fun logout() {
        
        supervisorScope.launch {
            try {
                val result = logoutUseCase.execute()
                if (result.isSuccess) {
                    println("AuthViewModel: Logout realizado com sucesso via API")
                } else {
                    println("AuthViewModel: Erro durante logout - ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                println("AuthViewModel: Erro durante logout - ${e.message}")
            }
            
            DataRefreshManager.notifyUserLoggedOut()
            DataRefreshManager.notifyAllDataUpdated()
            println("AuthViewModel: Notificação de limpeza de dados enviada")
            delay(100)

            SessionResetManager.resetFeatureContainers()
            println("AuthViewModel: Dependências de sessão resetadas")

            _uiState.value = AuthUiState(
                isLoading = false,
                isAuthenticated = false,
                userId = null,
                errorMessage = null,
                successMessage = "Logout realizado com sucesso!"
            )

            println("AuthViewModel: Estado de autenticação completamente limpo")

            onLogoutComplete?.invoke()
            println("AuthViewModel: Navegação pós-logout acionada")
        }
    }
    
    fun handleSessionExpired(message: String = "Sessão expirada. Faça login novamente.") {
        println("AuthViewModel: Sessão expirada detectada - executando limpeza completa")
        
        
        supervisorScope.launch {
            try {
                val result = logoutUseCase.execute()
                if (result.isSuccess) {
                    println("AuthViewModel: Logout automático executado com sucesso")
                } else {
                    println("AuthViewModel: Problemas no logout automático - ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                println("AuthViewModel: Erro durante logout automático - ${e.message}")
            }
            
            DataRefreshManager.notifyAllDataUpdated()
            delay(100)
            SessionResetManager.resetFeatureContainers()
            
            _uiState.value = AuthUiState(
                isLoading = false,
                isAuthenticated = false,
                userId = null,
                errorMessage = message
            )
            
            println("AuthViewModel: Estado limpo após expiração de sessão")
            
            onLogoutComplete?.invoke()
        }
    }

    fun clearError() {
        println("AuthViewModel: Limpando mensagens de erro e sucesso")
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
    
    fun resetAuthState() {
        println("AuthViewModel: Resetando completamente o estado de autenticação")
        _uiState.value = AuthUiState()
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val userId: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
