package edu.fatec.petwise.features.profile.domain.usecases

import edu.fatec.petwise.features.profile.domain.repository.ProfileRepository

class DeleteProfileUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend fun execute(): Result<Unit> {
        return profileRepository.deleteProfile()
    }
}