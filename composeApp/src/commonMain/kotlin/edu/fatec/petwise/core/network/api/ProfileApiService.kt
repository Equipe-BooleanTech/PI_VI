package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.UpdateProfileRequest
import edu.fatec.petwise.core.network.dto.UpdateProfileResponse
import edu.fatec.petwise.core.network.dto.UserProfileDto

interface ProfileApiService {
    suspend fun updateProfile(request: UpdateProfileRequest): NetworkResult<UpdateProfileResponse>
    suspend fun getProfile(): NetworkResult<UserProfileDto>
    suspend fun deleteProfile(): NetworkResult<Unit>
}

class ProfileApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : ProfileApiService {

    override suspend fun updateProfile(request: UpdateProfileRequest): NetworkResult<UpdateProfileResponse> {
        return networkHandler.put<UpdateProfileResponse, UpdateProfileRequest>(
            urlString = ApiEndpoints.USER_PROFILE,
            body = request
        )
    }

    override suspend fun getProfile(): NetworkResult<UserProfileDto> {
        return networkHandler.get<UserProfileDto>(ApiEndpoints.USER_PROFILE)
    }

    override suspend fun deleteProfile(): NetworkResult<Unit> {
        return networkHandler.delete<Unit>(ApiEndpoints.USER_PROFILE)
    }
}
