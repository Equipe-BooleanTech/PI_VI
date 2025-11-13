package edu.fatec.petwise.features.profile.domain.repository

import edu.fatec.petwise.core.network.dto.UpdateProfileRequest
import edu.fatec.petwise.core.network.dto.UserProfileDto

interface ProfileRepository {
    suspend fun updateProfile(updateRequest: UpdateProfileRequest): Result<UserProfileDto>
    suspend fun getProfile(): Result<UserProfileDto>
}
