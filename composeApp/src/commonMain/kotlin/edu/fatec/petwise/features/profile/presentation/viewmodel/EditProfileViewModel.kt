package edu.fatec.petwise.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.network.dto.UpdateProfileRequest
import edu.fatec.petwise.core.network.dto.UserProfileDto
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase
import edu.fatec.petwise.features.profile.domain.usecases.UpdateProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val userProfile: UserProfileDto? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class EditProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            getUserProfileUseCase.execute().fold(
                onSuccess = { profile ->
                    println("EditProfileViewModel: User profile loaded successfully - ${profile.fullName}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userProfile = profile,
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    println("EditProfileViewModel: Error loading profile - ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Erro ao carregar perfil"
                    )
                }
            )
        }
    }

    fun updateProfile(updateRequest: UpdateProfileRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            
            updateProfileUseCase.execute(updateRequest).fold(
                onSuccess = { updatedProfile ->
                    println("EditProfileViewModel: Profile updated successfully")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userProfile = updatedProfile,
                        successMessage = "Perfil atualizado com sucesso!",
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    println("EditProfileViewModel: Error updating profile - ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Erro ao atualizar perfil"
                    )
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
    }
}
