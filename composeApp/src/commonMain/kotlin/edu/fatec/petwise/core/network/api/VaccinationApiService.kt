package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json

interface VaccinationApiService {
    suspend fun getAllVaccinations(page: Int = 1, pageSize: Int = 20): NetworkResult<List<VaccinationDto>>
    suspend fun getVaccinationById(id: String): NetworkResult<VaccinationDto>
    suspend fun getVaccinationsByPetId(petId: String, page: Int = 1, pageSize: Int = 20): NetworkResult<List<VaccinationDto>>
    suspend fun createVaccination(petId: String, request: CreateVaccinationRequest): NetworkResult<VaccinationDto>
    suspend fun updateVaccination(id: String, request: UpdateVaccinationRequest): NetworkResult<VaccinationDto>
    suspend fun deleteVaccination(id: String): NetworkResult<Unit>
    suspend fun markAsApplied(id: String, request: MarkAsAppliedRequest): NetworkResult<VaccinationDto>
    suspend fun scheduleNextDose(id: String, request: ScheduleNextDoseRequest): NetworkResult<VaccinationDto>
    suspend fun getUpcomingVaccinations(days: Int = 30): NetworkResult<List<VaccinationDto>>
    suspend fun getOverdueVaccinations(): NetworkResult<List<VaccinationDto>>
}

class VaccinationApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : VaccinationApiService {

    override suspend fun getAllVaccinations(page: Int, pageSize: Int): NetworkResult<List<VaccinationDto>> {
        return networkHandler.getWithCustomDeserializer(ApiEndpoints.VACCINATIONS, deserializer = { jsonString ->
            val json = Json { ignoreUnknownKeys = true }
            try {
                // Try to parse as direct array first
                json.decodeFromString<List<VaccinationDto>>(jsonString)
            } catch (e: Exception) {
                // Fallback to wrapped object
                val wrapped = json.decodeFromString<VaccinationListResponse>(jsonString)
                wrapped.vaccinations ?: emptyList()
            }
        }) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getVaccinationById(id: String): NetworkResult<VaccinationDto> {
        return networkHandler.get<VaccinationDto>(ApiEndpoints.getVaccination(id))
    }

    override suspend fun getVaccinationsByPetId(petId: String, page: Int, pageSize: Int): NetworkResult<List<VaccinationDto>> {
        return networkHandler.getWithCustomDeserializer(ApiEndpoints.getVaccinationsByPet(petId), deserializer = { jsonString ->
            val json = Json { ignoreUnknownKeys = true }
            try {
                // Try to parse as direct array first
                json.decodeFromString<List<VaccinationDto>>(jsonString)
            } catch (e: Exception) {
                // Fallback to wrapped object
                val wrapped = json.decodeFromString<VaccinationListResponse>(jsonString)
                wrapped.vaccinations ?: emptyList()
            }
        }) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun createVaccination(petId: String, request: CreateVaccinationRequest): NetworkResult<VaccinationDto> {
        return networkHandler.post<VaccinationDto, CreateVaccinationRequest>(
            urlString = ApiEndpoints.VACCINATIONS,
            body = request
        ) {
            parameter("petId", petId)
        }
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

    override suspend fun getUpcomingVaccinations(days: Int): NetworkResult<List<VaccinationDto>> {
        return networkHandler.getWithCustomDeserializer(ApiEndpoints.VACCINATIONS, deserializer = { jsonString ->
            val json = Json { ignoreUnknownKeys = true }
            try {
                // Try to parse as direct array first
                json.decodeFromString<List<VaccinationDto>>(jsonString)
            } catch (e: Exception) {
                // Fallback to wrapped object
                val wrapped = json.decodeFromString<VaccinationListResponse>(jsonString)
                wrapped.vaccinations ?: emptyList()
            }
        }) {
            parameter("validOnly", true)
        }
    }

    override suspend fun getOverdueVaccinations(): NetworkResult<List<VaccinationDto>> {
        return networkHandler.getWithCustomDeserializer(ApiEndpoints.VACCINATIONS, deserializer = { jsonString ->
            val json = Json { ignoreUnknownKeys = true }
            try {
                // Try to parse as direct array first
                json.decodeFromString<List<VaccinationDto>>(jsonString)
            } catch (e: Exception) {
                // Fallback to wrapped object
                val wrapped = json.decodeFromString<VaccinationListResponse>(jsonString)
                wrapped.vaccinations ?: emptyList()
            }
        }) {
            parameter("validOnly", false)
        }
    }
}
