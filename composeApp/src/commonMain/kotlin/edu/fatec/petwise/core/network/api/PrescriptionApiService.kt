package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*

interface PrescriptionApiService {
    suspend fun getAllPrescriptions(page: Int = 1, pageSize: Int = 20): NetworkResult<PrescriptionListResponse>
    suspend fun getPrescriptionById(id: String): NetworkResult<PrescriptionDto>
    suspend fun getPrescriptionsByPetId(petId: String): NetworkResult<List<PrescriptionDto>>
    suspend fun getPrescriptionsByVeterinaryId(veterinaryId: String): NetworkResult<List<PrescriptionDto>>
    suspend fun createPrescription(request: CreatePrescriptionRequest): NetworkResult<PrescriptionDto>
    suspend fun updatePrescription(id: String, request: UpdatePrescriptionRequest): NetworkResult<PrescriptionDto>
    suspend fun deletePrescription(id: String): NetworkResult<Unit>
}

class PrescriptionApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : PrescriptionApiService {

    override suspend fun getAllPrescriptions(page: Int, pageSize: Int): NetworkResult<PrescriptionListResponse> {
        return networkHandler.get<PrescriptionListResponse>(ApiEndpoints.PRESCRIPTIONS) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getPrescriptionById(id: String): NetworkResult<PrescriptionDto> {
        return networkHandler.get<PrescriptionDto>(ApiEndpoints.getPrescription(id))
    }

    override suspend fun getPrescriptionsByPetId(petId: String): NetworkResult<List<PrescriptionDto>> {
        return networkHandler.get<List<PrescriptionDto>>(ApiEndpoints.getPrescriptionsByPet(petId))
    }

    override suspend fun getPrescriptionsByVeterinaryId(veterinaryId: String): NetworkResult<List<PrescriptionDto>> {
        return networkHandler.get<List<PrescriptionDto>>(ApiEndpoints.getPrescriptionsByVeterinary(veterinaryId))
    }

    override suspend fun createPrescription(request: CreatePrescriptionRequest): NetworkResult<PrescriptionDto> {
        return networkHandler.post<PrescriptionDto, CreatePrescriptionRequest>(
            urlString = ApiEndpoints.PRESCRIPTIONS,
            body = request
        )
    }

    override suspend fun updatePrescription(id: String, request: UpdatePrescriptionRequest): NetworkResult<PrescriptionDto> {
        return networkHandler.put<PrescriptionDto, UpdatePrescriptionRequest>(
            urlString = ApiEndpoints.getPrescription(id),
            body = request
        )
    }

    override suspend fun deletePrescription(id: String): NetworkResult<Unit> {
        return networkHandler.delete<Unit>(ApiEndpoints.getPrescription(id))
    }
}
