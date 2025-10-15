package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*

interface VaccinationApiService {
    suspend fun getAllVaccinations(page: Int = 1, pageSize: Int = 20): NetworkResult<VaccinationListResponse>
    suspend fun getVaccinationById(id: String): NetworkResult<VaccinationDto>
    suspend fun getVaccinationsByPetId(petId: String, page: Int = 1, pageSize: Int = 20): NetworkResult<VaccinationListResponse>
    suspend fun createVaccination(request: CreateVaccinationRequest): NetworkResult<VaccinationDto>
    suspend fun updateVaccination(id: String, request: UpdateVaccinationRequest): NetworkResult<VaccinationDto>
    suspend fun deleteVaccination(id: String): NetworkResult<Unit>
    suspend fun markAsApplied(id: String, request: MarkAsAppliedRequest): NetworkResult<VaccinationDto>
    suspend fun scheduleNextDose(id: String, request: ScheduleNextDoseRequest): NetworkResult<VaccinationDto>
    suspend fun getUpcomingVaccinations(days: Int = 30): NetworkResult<VaccinationListResponse>
    suspend fun getOverdueVaccinations(): NetworkResult<VaccinationListResponse>
}

class VaccinationApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : VaccinationApiService {

    override suspend fun getAllVaccinations(page: Int, pageSize: Int): NetworkResult<VaccinationListResponse> {
        return networkHandler.get<VaccinationListResponse>(ApiEndpoints.VACCINATIONS) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getVaccinationById(id: String): NetworkResult<VaccinationDto> {
        return networkHandler.get<VaccinationDto>(ApiEndpoints.getVaccination(id))
    }

    override suspend fun getVaccinationsByPetId(petId: String, page: Int, pageSize: Int): NetworkResult<VaccinationListResponse> {
        return networkHandler.get<VaccinationListResponse>(ApiEndpoints.getVaccinationsByPet(petId)) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun createVaccination(request: CreateVaccinationRequest): NetworkResult<VaccinationDto> {
        return networkHandler.post<VaccinationDto, CreateVaccinationRequest>(
            urlString = ApiEndpoints.VACCINATIONS,
            body = request
        )
    }

    override suspend fun updateVaccination(id: String, request: UpdateVaccinationRequest): NetworkResult<VaccinationDto> {
        return networkHandler.put<VaccinationDto, UpdateVaccinationRequest>(
            urlString = ApiEndpoints.getVaccination(id),
            body = request
        )
    }

    override suspend fun deleteVaccination(id: String): NetworkResult<Unit> {
        return networkHandler.delete<Unit>(ApiEndpoints.getVaccination(id))
    }

    override suspend fun markAsApplied(id: String, request: MarkAsAppliedRequest): NetworkResult<VaccinationDto> {
        return networkHandler.post<VaccinationDto, MarkAsAppliedRequest>(
            urlString = ApiEndpoints.markVaccinationAsApplied(id),
            body = request
        )
    }

    override suspend fun scheduleNextDose(id: String, request: ScheduleNextDoseRequest): NetworkResult<VaccinationDto> {
        return networkHandler.post<VaccinationDto, ScheduleNextDoseRequest>(
            urlString = ApiEndpoints.scheduleVaccinationNextDose(id),
            body = request
        )
    }

    override suspend fun getUpcomingVaccinations(days: Int): NetworkResult<VaccinationListResponse> {
        return networkHandler.get<VaccinationListResponse>(ApiEndpoints.VACCINATIONS_UPCOMING) {
            parameter("days", days)
        }
    }

    override suspend fun getOverdueVaccinations(): NetworkResult<VaccinationListResponse> {
        return networkHandler.get<VaccinationListResponse>(ApiEndpoints.VACCINATIONS_OVERDUE)
    }
}
