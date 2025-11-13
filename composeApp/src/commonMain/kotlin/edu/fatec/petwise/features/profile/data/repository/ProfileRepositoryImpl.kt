package edu.fatec.petwise.features.profile.data.repository

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.ProfileApiService
import edu.fatec.petwise.core.network.dto.UpdateProfileRequest
import edu.fatec.petwise.core.network.dto.UserProfileDto
import edu.fatec.petwise.features.profile.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val profileApiService: ProfileApiService
) : ProfileRepository {

    override suspend fun updateProfile(updateRequest: UpdateProfileRequest): Result<UserProfileDto> {
        return try {
            println("ProfileRepository: Updating profile via API")
            when (val result = profileApiService.updateProfile(updateRequest)) {
                is NetworkResult.Success -> {
                    println("ProfileRepository: Profile updated successfully")
                    Result.success(result.data)
                }
                is NetworkResult.Error -> {
                    println("ProfileRepository: Error updating profile - ${result.exception.message}")
                    Result.failure(Exception(result.exception.message ?: "Error updating profile"))
                }
                is NetworkResult.Loading -> {
                    println("ProfileRepository: Update in progress...")
                    Result.failure(Exception("Update in progress"))
                }
            }
        } catch (e: Exception) {
            println("ProfileRepository: Unexpected error updating profile - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getProfile(): Result<UserProfileDto> {
        return try {
            println("ProfileRepository: Fetching profile via API")
            when (val result = profileApiService.getProfile()) {
                is NetworkResult.Success -> {
                    println("ProfileRepository: Profile fetched successfully")
                    Result.success(result.data)
                }
                is NetworkResult.Error -> {
                    println("ProfileRepository: Error fetching profile - ${result.exception.message}")
                    Result.failure(Exception(result.exception.message ?: "Error fetching profile"))
                }
                is NetworkResult.Loading -> {
                    println("ProfileRepository: Fetch in progress...")
                    Result.failure(Exception("Fetch in progress"))
                }
            }
        } catch (e: Exception) {
            println("ProfileRepository: Unexpected error fetching profile - ${e.message}")
            Result.failure(e)
        }
    }
}
