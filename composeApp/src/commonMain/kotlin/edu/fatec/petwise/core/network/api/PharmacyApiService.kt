package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*

interface PharmacyApiService {
    suspend fun getAllPharmacies(): NetworkResult<PharmacyListResponse>
    suspend fun getPharmacyById(id: String): NetworkResult<UserProfileDto>
}

class PharmacyApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : PharmacyApiService {

    override suspend fun getAllPharmacies(): NetworkResult<PharmacyListResponse> {
        return networkHandler.get<PharmacyListResponse>(ApiEndpoints.PHARMACIES)
    }

    override suspend fun getPharmacyById(id: String): NetworkResult<UserProfileDto> {
        return networkHandler.get<UserProfileDto>(ApiEndpoints.getPharmacy(id))
    }
}
