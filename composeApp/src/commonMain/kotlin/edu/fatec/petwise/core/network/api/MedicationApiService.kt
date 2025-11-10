package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*

interface MedicationApiService {
    suspend fun getAllMedications(page: Int = 1, pageSize: Int = 20): NetworkResult<MedicationListResponse>
    suspend fun getMedicationById(id: String): NetworkResult<MedicationDto>
    suspend fun getMedicationsByPetId(petId: String): NetworkResult<List<MedicationDto>>
    suspend fun getActiveMedicationsByPetId(petId: String): NetworkResult<List<MedicationDto>>
    suspend fun searchMedications(filter: MedicationFilterRequest, page: Int = 1, pageSize: Int = 20): NetworkResult<MedicationListResponse>
    suspend fun createMedication(request: CreateMedicationRequest): NetworkResult<MedicationDto>
    suspend fun updateMedication(id: String, request: UpdateMedicationRequest): NetworkResult<MedicationDto>
    suspend fun updateMedicationStatus(id: String, status: String): NetworkResult<MedicationDto>
    suspend fun completeMedication(id: String): NetworkResult<MedicationDto>
    suspend fun deleteMedication(id: String): NetworkResult<Unit>
    suspend fun getUpcomingMedications(): NetworkResult<List<MedicationDto>>
    suspend fun getExpiredMedications(): NetworkResult<List<MedicationDto>>
}

class MedicationApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : MedicationApiService {

    override suspend fun getAllMedications(page: Int, pageSize: Int): NetworkResult<MedicationListResponse> {
        return networkHandler.get<MedicationListResponse>(ApiEndpoints.MEDICATIONS) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getMedicationById(id: String): NetworkResult<MedicationDto> {
        return networkHandler.get<MedicationDto>(ApiEndpoints.getMedication(id))
    }

    override suspend fun getMedicationsByPetId(petId: String): NetworkResult<List<MedicationDto>> {
        return networkHandler.get<List<MedicationDto>>(ApiEndpoints.getMedicationsByPet(petId))
    }

    override suspend fun getActiveMedicationsByPetId(petId: String): NetworkResult<List<MedicationDto>> {
        return networkHandler.get<List<MedicationDto>>(ApiEndpoints.getActiveMedicationsByPet(petId))
    }

    override suspend fun searchMedications(filter: MedicationFilterRequest, page: Int, pageSize: Int): NetworkResult<MedicationListResponse> {
        return networkHandler.get<MedicationListResponse>(ApiEndpoints.MEDICATIONS_SEARCH) {
            parameter("page", page)
            parameter("pageSize", pageSize)
            filter.petId?.let { parameter("petId", it) }
            filter.veterinarianId?.let { parameter("veterinarianId", it) }
            filter.status?.let { parameter("status", it) }
            filter.medicationName?.let { parameter("medicationName", it) }
            if (filter.searchQuery.isNotBlank()) {
                parameter("searchQuery", filter.searchQuery)
            }
        }
    }

    override suspend fun createMedication(request: CreateMedicationRequest): NetworkResult<MedicationDto> {
        return networkHandler.post<MedicationDto, CreateMedicationRequest>(
            urlString = ApiEndpoints.MEDICATIONS,
            body = request
        )
    }

    override suspend fun updateMedication(id: String, request: UpdateMedicationRequest): NetworkResult<MedicationDto> {
        return networkHandler.put<MedicationDto, UpdateMedicationRequest>(
            urlString = ApiEndpoints.getMedication(id),
            body = request
        )
    }

    override suspend fun updateMedicationStatus(id: String, status: String): NetworkResult<MedicationDto> {
        val request = UpdateMedicationStatusRequest(status = status)
        return networkHandler.put<MedicationDto, UpdateMedicationStatusRequest>(
            urlString = ApiEndpoints.updateMedicationStatus(id),
            body = request
        )
    }

    override suspend fun completeMedication(id: String): NetworkResult<MedicationDto> {
        return networkHandler.put<MedicationDto, CompleteMedicationRequest>(
            urlString = ApiEndpoints.completeMedication(id),
            body = CompleteMedicationRequest(
                completionDate = kotlinx.datetime.Clock.System.now().toString(),
                observations = ""
            )
        )
    }

    override suspend fun deleteMedication(id: String): NetworkResult<Unit> {
        return networkHandler.delete<Unit>(ApiEndpoints.getMedication(id))
    }

    override suspend fun getUpcomingMedications(): NetworkResult<List<MedicationDto>> {
        return networkHandler.get<List<MedicationDto>>(ApiEndpoints.MEDICATIONS_UPCOMING)
    }

    override suspend fun getExpiredMedications(): NetworkResult<List<MedicationDto>> {
        return networkHandler.get<List<MedicationDto>>(ApiEndpoints.MEDICATIONS_EXPIRED)
    }
}