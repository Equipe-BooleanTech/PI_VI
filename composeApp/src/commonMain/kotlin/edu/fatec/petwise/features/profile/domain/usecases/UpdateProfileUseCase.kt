package edu.fatec.petwise.features.profile.domain.usecases

import edu.fatec.petwise.core.network.dto.UpdateProfileRequest
import edu.fatec.petwise.core.network.dto.UserProfileDto
import edu.fatec.petwise.features.profile.domain.repository.ProfileRepository

class UpdateProfileUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend fun execute(updateRequest: UpdateProfileRequest): Result<UserProfileDto> {
        return profileRepository.updateProfile(updateRequest)
    }
}
