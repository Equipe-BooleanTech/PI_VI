package edu.fatec.petwise.features.auth.domain.usecases

import edu.fatec.petwise.features.auth.domain.repository.AuthRepository
import edu.fatec.petwise.core.network.dto.UserProfileDto

class GetUserProfileUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(): Result<UserProfileDto> {
        return authRepository.getUserProfile()
    }
}