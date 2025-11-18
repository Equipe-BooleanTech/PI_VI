package edu.fatec.petwise.features.profile.di

import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.auth.di.AuthDependencyContainer
import edu.fatec.petwise.features.profile.data.repository.ProfileRepositoryImpl
import edu.fatec.petwise.features.profile.domain.repository.ProfileRepository
import edu.fatec.petwise.features.profile.domain.usecases.DeleteProfileUseCase
import edu.fatec.petwise.features.profile.domain.usecases.UpdateProfileUseCase
import edu.fatec.petwise.features.profile.presentation.viewmodel.EditProfileViewModel
import kotlinx.coroutines.cancel

object ProfileDependencyContainer {

    private var repository: ProfileRepository? = null
    private var editProfileViewModel: EditProfileViewModel? = null

    private fun getRepository(): ProfileRepository {
        val existing = repository
        if (existing != null) return existing
        val created = ProfileRepositoryImpl(NetworkModule.profileApiService)
        repository = created
        return created
    }

    private fun buildEditProfileViewModel(): EditProfileViewModel {
        val repo = getRepository()
        return EditProfileViewModel(
            getUserProfileUseCase = AuthDependencyContainer.provideGetUserProfileUseCase(),
            updateProfileUseCase = UpdateProfileUseCase(repo),
            logoutUseCase = AuthDependencyContainer.provideLogoutUseCase(),
            deleteProfileUseCase = DeleteProfileUseCase(repo)
        )
    }

    fun provideEditProfileViewModel(): EditProfileViewModel {
        val existing = editProfileViewModel
        if (existing != null) return existing
        val created = buildEditProfileViewModel()
        editProfileViewModel = created
        return created
    }

    fun reset() {
        editProfileViewModel?.viewModelScope?.cancel()
        editProfileViewModel = null
        repository = null
    }
}
