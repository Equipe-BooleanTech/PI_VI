package edu.fatec.petwise.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.auth.domain.usecases.LoginUseCase
import edu.fatec.petwise.features.auth.domain.usecases.LogoutUseCase
import edu.fatec.petwise.features.auth.domain.usecases.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = loginUseCase.execute(email, password)

            result.fold(
                onSuccess = { userId ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        userId = userId
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Erro ao fazer login"
                    )
                }
            )
        }
    }

    fun register(registerRequest: edu.fatec.petwise.core.network.dto.RegisterRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = registerUseCase.execute(registerRequest)

            result.fold(
                onSuccess = { userId ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        userId = userId
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Erro ao registrar"
                    )
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            println("AuthViewModel: Iniciando logout")
            
            println("AuthViewModel: Cancelando todas as operações de rede em andamento")
            NetworkModule.cancelAllOperations()
            
            try {
                logoutUseCase.execute()
                println("AuthViewModel: Logout executado com sucesso")
            } catch (e: Exception) {
                println("AuthViewModel: Erro durante logout, mas continuando - ${e.message}")
            }
            
            DataRefreshManager.notifyAllDataUpdated()
            _uiState.value = AuthUiState()
            println("AuthViewModel: Estado de autenticação limpo")
            
        }
    }

    fun handleSessionExpired(message: String = "Sessão expirada. Faça login novamente.") {
        println("AuthViewModel: Sessão expirada detectada - redirecionando para login")
        
        println("AuthViewModel: Cancelando operações de rede antes do logout automático")
        NetworkModule.cancelAllOperations()
        
        viewModelScope.launch {
            try {
                logoutUseCase.execute()
            } catch (e: Exception) {
                println("AuthViewModel: Erro durante logout automático - ${e.message}")
            }
            
            _uiState.value = AuthUiState(
                errorMessage = message
            )
            println("AuthViewModel: Usuário redirecionado para tela de login")
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val userId: String? = null,
    val errorMessage: String? = null
)
