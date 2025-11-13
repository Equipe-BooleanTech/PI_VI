package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.UpdateProfileRequest
import edu.fatec.petwise.core.network.dto.UserProfileDto

interface ProfileApiService {
    suspend fun updateProfile(request: UpdateProfileRequest): NetworkResult<UserProfileDto>
    suspend fun getProfile(): NetworkResult<UserProfileDto>
}

class ProfileApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : ProfileApiService {

    override suspend fun updateProfile(request: UpdateProfileRequest): NetworkResult<UserProfileDto> {
        return networkHandler.put<UserProfileDto, UpdateProfileRequest>(
            urlString = ApiEndpoints.PROFILE,
            body = request
        )
    }

    override suspend fun getProfile(): NetworkResult<UserProfileDto> {
        return networkHandler.get<UserProfileDto>(ApiEndpoints.PROFILE)
    }
}
