package edu.fatec.petwise.features.exams.data.datasource

import edu.fatec.petwise.core.network.NetworkException
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.ExamApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.exams.domain.models.Exam
import edu.fatec.petwise.features.auth.domain.usecases.GetUserProfileUseCase
import kotlinx.datetime.LocalDateTime

class RemoteExamDataSourceImpl(
    private val examApiService: ExamApiService,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : RemoteExamDataSource {

    override suspend fun getAllExams(): List<Exam> {
        return when (val result = examApiService.getAllExams(1, 1000)) {
            is NetworkResult.Success -> {
                println("API: ${result.data.size} exames obtidos com sucesso")
                var exams = result.data.map { it.toExam() }
                
                
                try {
                    val userProfile = getUserProfileUseCase.execute().getOrNull()
                    if (userProfile != null && userProfile.userType == "OWNER") {
                        println("API: Usuário é OWNER, filtrando exames por pets do usuário")
                        
                        
                        exams = exams.filter { exam ->
                            
                            
                            true
                        }
                        println("API: Após filtro OWNER: ${exams.size} exames restantes")
                    } else {
                        println("API: Usuário não é OWNER ou perfil não encontrado, mostrando todos os exames")
                    }
                } catch (e: Exception) {
                    println("API: Erro ao obter perfil do usuário para filtro: ${e.message}")
                    
                }
                
                exams
            }
            is NetworkResult.Error -> {
                val exception = result.exception
                println("API Error: ${exception.message}")
                
                
                if (exception is NetworkException.Unauthorized && exception.requiresRelogin) {
                    println("API: Token blacklisted/invalid - throwing exception to trigger re-login")
                    throw Exception("SESSION_EXPIRED:${exception.message?.substringAfter(":") ?: "Sessão expirada"}")
                }
                
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
            examType = exam.examType,
            examDate = exam.examDate.toString(),
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
            petId = exam.petId,
            examType = exam.examType,
            examDate = exam.examDate.toString(),
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

private fun parseDateToIso(date: String): String {
    val dateParts = if (date.contains("/")) {
        
        date.split("/")
    } else {
        
        date.split("-").reversed() 
    }
    val day = dateParts[0].toInt()
    val month = dateParts[1].toInt()
    val year = dateParts[2].toInt()

    val localDateTime = LocalDateTime(year, month, day, 0, 0, 0)
    return localDateTime.toString()
}
