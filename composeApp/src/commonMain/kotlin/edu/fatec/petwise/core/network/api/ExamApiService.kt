package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json

interface ExamApiService {
    suspend fun getAllExams(page: Int = 1, pageSize: Int = 20): NetworkResult<List<ExamDto>>
    suspend fun getExamById(id: String): NetworkResult<ExamDto>
    suspend fun getExamsByPetId(petId: String): NetworkResult<List<ExamDto>>
    suspend fun getExamsByVeterinaryId(veterinaryId: String): NetworkResult<List<ExamDto>>
    suspend fun createExam(petId: String, request: CreateExamRequest): NetworkResult<ExamDto>
    suspend fun updateExam(id: String, request: UpdateExamRequest): NetworkResult<ExamDto>
    suspend fun deleteExam(id: String): NetworkResult<Unit>
}

class ExamApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : ExamApiService {

    override suspend fun getAllExams(page: Int, pageSize: Int): NetworkResult<List<ExamDto>> {
        return networkHandler.getWithCustomDeserializer(
            urlString = ApiEndpoints.EXAMS,
            deserializer = { jsonString ->
                val json = Json { ignoreUnknownKeys = true }
                try {
                    // Try to parse as direct array first
                    json.decodeFromString<List<ExamDto>>(jsonString)
                } catch (e: Exception) {
                    // Fallback to wrapped object
                    val wrapped = json.decodeFromString<ExamListResponse>(jsonString)
                    wrapped.exams ?: emptyList()
                }
            }
        ) {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
    }

    override suspend fun getExamById(id: String): NetworkResult<ExamDto> {
        return networkHandler.get<ExamDto>(ApiEndpoints.getExam(id))
    }

    override suspend fun getExamsByPetId(petId: String): NetworkResult<List<ExamDto>> {
        return networkHandler.get<List<ExamDto>>(ApiEndpoints.getExamsByPet(petId))
    }

    override suspend fun getExamsByVeterinaryId(veterinaryId: String): NetworkResult<List<ExamDto>> {
        return networkHandler.get<List<ExamDto>>(ApiEndpoints.getExamsByVeterinary(veterinaryId))
    }

    override suspend fun createExam(petId: String, request: CreateExamRequest): NetworkResult<ExamDto> {
        return networkHandler.post<ExamDto, CreateExamRequest>(
            urlString = ApiEndpoints.EXAMS,
            body = request
        ) {
            parameter("petId", petId)
        }
    }

    override suspend fun updateExam(id: String, request: UpdateExamRequest): NetworkResult<ExamDto> {
        return networkHandler.put<ExamDto, UpdateExamRequest>(
            urlString = ApiEndpoints.getExam(id),
            body = request
        )
    }

    override suspend fun deleteExam(id: String): NetworkResult<Unit> {
        return networkHandler.delete<Unit>(ApiEndpoints.getExam(id))
    }
}
