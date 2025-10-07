package edu.fatec.petwise.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.features.auth.domain.usecases.RequestPasswordResetUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ForgotPasswordViewModel(
    private val requestPasswordResetUseCase: RequestPasswordResetUseCase = RequestPasswordResetUseCase()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()
    
    fun requestPasswordReset(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )
            
            val result = requestPasswordResetUseCase.execute(email)
            
            result.fold(
                onSuccess = { message ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = message,
                        requestSent = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Erro ao solicitar recuperação de senha"
                    )
                }
            )
        }
    }
    
    /**
     * Clears error messages
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Resets the UI state
     */
    fun reset() {
        _uiState.value = ForgotPasswordUiState()
    }
}

/**
 * UI state for Forgot Password screen
 */
data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val requestSent: Boolean = false
)
