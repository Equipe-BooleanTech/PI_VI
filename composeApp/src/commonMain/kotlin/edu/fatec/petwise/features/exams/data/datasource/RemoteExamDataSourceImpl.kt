package edu.fatec.petwise.features.exams.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.ExamApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.exams.domain.models.Exam

class RemoteExamDataSourceImpl(
    private val examApiService: ExamApiService
) : RemoteExamDataSource {

    override suspend fun getAllExams(): List<Exam> {
        return when (val result = examApiService.getAllExams()) {
            is NetworkResult.Success -> result.data.exams.map { it.toExam() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getExamById(id: String): Exam? {
        return when (val result = examApiService.getExamById(id)) {
            is NetworkResult.Success -> result.data.toExam()
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                null
            }
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun createExam(exam: Exam): Exam {
        val request = CreateExamRequest(
            petId = exam.petId,
            veterinaryId = exam.veterinaryId,
            examType = exam.examType,
            examDate = exam.examDate,
            results = exam.results,
            status = exam.status,
            notes = exam.notes,
            attachmentUrl = exam.attachmentUrl
        )
        return when (val result = examApiService.createExam(request)) {
            is NetworkResult.Success -> result.data.toExam()
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun updateExam(exam: Exam): Exam {
        val request = UpdateExamRequest(
            examType = exam.examType,
            examDate = exam.examDate,
            results = exam.results,
            status = exam.status,
            notes = exam.notes,
            attachmentUrl = exam.attachmentUrl
        )
        return when (val result = examApiService.updateExam(exam.id, request)) {
            is NetworkResult.Success -> result.data.toExam()
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun deleteExam(id: String) {
        when (val result = examApiService.deleteExam(id)) {
            is NetworkResult.Success -> Unit
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun searchExams(query: String): List<Exam> {
        return getAllExams().filter { 
            it.examType.contains(query, ignoreCase = true) ||
            it.notes?.contains(query, ignoreCase = true) == true
        }
    }

    override suspend fun getExamsByPetId(petId: String): List<Exam> {
        return when (val result = examApiService.getExamsByPetId(petId)) {
            is NetworkResult.Success -> result.data.map { it.toExam() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getExamsByVeterinaryId(veterinaryId: String): List<Exam> {
        return when (val result = examApiService.getExamsByVeterinaryId(veterinaryId)) {
            is NetworkResult.Success -> result.data.map { it.toExam() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }
}
