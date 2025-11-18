package edu.fatec.petwise.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.core.data.DataRefreshEvent
import edu.fatec.petwise.core.network.dto.UpdateProfileRequest
import edu.fatec.petwise.core.network.dto.UpdateProfileResponse
import edu.fatec.petwise.core.network.dto.UserProfileDto
import edu.fatec.petwise.features.auth.di.AuthDependencyContainer
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase
import edu.fatec.petwise.features.auth.domain.usecases.LogoutUseCase
import edu.fatec.petwise.features.profile.domain.usecases.DeleteProfileUseCase
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
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
        observeDataRefresh()
    }

    private fun observeDataRefresh() {
        viewModelScope.launch {
            DataRefreshManager.refreshEvents.collect { event ->
                when (event) {
                    is DataRefreshEvent.UserLoggedIn -> loadUserProfile()
                    else -> {}
                }
            }
        }
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
                onSuccess = { updateResponse ->
                    println("EditProfileViewModel: Profile updated successfully")
                    
                    if (updateResponse.requiresLogout) {
                        println("EditProfileViewModel: Email changed, triggering logout")
                        AuthDependencyContainer.provideAuthViewModel().logout()
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = "Perfil atualizado com sucesso! Você será desconectado.",
                            errorMessage = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            userProfile = UserProfileDto(
                                id = updateResponse.user.id,
                                email = updateResponse.user.email,
                                fullName = updateResponse.user.fullName,
                                userType = updateResponse.user.userType,
                                phone = updateResponse.user.phone,
                                profileImageUrl = null,
                                verified = updateResponse.user.active,
                                createdAt = updateResponse.user.createdAt,
                                updatedAt = updateResponse.user.updatedAt
                            ),
                            successMessage = "Perfil atualizado com sucesso!",
                            errorMessage = null
                        )
                    }
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

    fun deleteProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            
            deleteProfileUseCase.execute().fold(
                onSuccess = {
                    println("EditProfileViewModel: Profile deleted successfully")
                    AuthDependencyContainer.provideAuthViewModel().logout()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Conta excluída com sucesso!",
                        errorMessage = null
                    )
                },
                onFailure = { exception ->
                    println("EditProfileViewModel: Error deleting profile - ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Erro ao excluir conta"
                    )
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
    }
}
