package edu.fatec.petwise.features.profile.domain.repository

import edu.fatec.petwise.core.network.dto.UpdateProfileRequest
import edu.fatec.petwise.core.network.dto.UpdateProfileResponse
import edu.fatec.petwise.core.network.dto.UserProfileDto

interface ProfileRepository {
    suspend fun updateProfile(updateRequest: UpdateProfileRequest): Result<UpdateProfileResponse>
    suspend fun getProfile(): Result<UserProfileDto>
    suspend fun deleteProfile(): Result<Unit>
}
