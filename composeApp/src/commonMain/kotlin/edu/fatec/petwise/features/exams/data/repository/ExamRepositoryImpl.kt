package edu.fatec.petwise.features.exams.data.repository

import edu.fatec.petwise.features.exams.data.datasource.RemoteExamDataSource
import edu.fatec.petwise.features.exams.domain.models.Exam
import edu.fatec.petwise.features.exams.domain.repository.ExamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExamRepositoryImpl(
    private val remoteDataSource: RemoteExamDataSource
) : ExamRepository {

    override fun getAllExams(): Flow<List<Exam>> = flow {
        try {
            println("Repositório: Buscando todos os exames via API")
            val exams = remoteDataSource.getAllExams()
            println("Repositório: ${exams.size} exames carregados com sucesso da API")
            emit(exams)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar exames da API - ${e.message}")
            emit(emptyList())
        }
    }

    override fun getExamById(id: String): Flow<Exam?> = flow {
        try {
            println("Repositório: Buscando exame por ID '$id' via API")
            val exam = remoteDataSource.getExamById(id)
            if (exam != null) {
                println("Repositório: Exame '${exam.examType}' encontrado com sucesso")
            } else {
                println("Repositório: Exame com ID '$id' não encontrado")
            }
            emit(exam)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar exame por ID '$id' - ${e.message}")
            emit(null)
        }
    }

    override fun searchExams(query: String): Flow<List<Exam>> = flow {
        try {
            println("Repositório: Iniciando busca de exames com consulta '$query'")
            val exams = remoteDataSource.searchExams(query)
            println("Repositório: Busca concluída - ${exams.size} exames encontrados")
            emit(exams)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar exames na API - ${e.message}")
            emit(emptyList())
        }
    }

    override fun getExamsByPetId(petId: String): Flow<List<Exam>> = flow {
        try {
            println("Repositório: Buscando exames do pet '$petId' via API")
            val exams = remoteDataSource.getExamsByPetId(petId)
            println("Repositório: ${exams.size} exames encontrados")
            emit(exams)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar exames do pet - ${e.message}")
            emit(emptyList())
        }
    }

    override fun getExamsByVeterinaryId(veterinaryId: String): Flow<List<Exam>> = flow {
        try {
            println("Repositório: Buscando exames do veterinário '$veterinaryId' via API")
            val exams = remoteDataSource.getExamsByVeterinaryId(veterinaryId)
            println("Repositório: ${exams.size} exames encontrados")
            emit(exams)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar exames do veterinário - ${e.message}")
            emit(emptyList())
        }
    }

    override suspend fun addExam(exam: Exam): Result<Exam> {
        return try {
            println("Repositório: Adicionando novo exame '${exam.examType}' via API")
            val createdExam = remoteDataSource.createExam(exam)
            println("Repositório: Exame '${createdExam.examType}' criado com sucesso - ID: ${createdExam.id}")
            Result.success(createdExam)
        } catch (e: Exception) {
            println("Repositório: Erro ao criar exame '${exam.examType}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateExam(exam: Exam): Result<Exam> {
        return try {
            println("Repositório: Atualizando exame '${exam.examType}' (ID: ${exam.id}) via API")
            val updatedExam = remoteDataSource.updateExam(exam)
            println("Repositório: Exame '${updatedExam.examType}' atualizado com sucesso")
            Result.success(updatedExam)
        } catch (e: Exception) {
            println("Repositório: Erro ao atualizar exame '${exam.examType}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteExam(id: String): Result<Unit> {
        return try {
            println("Repositório: Excluindo exame com ID '$id' via API")
            remoteDataSource.deleteExam(id)
            println("Repositório: Exame excluído com sucesso")
            Result.success(Unit)
        } catch (e: Exception) {
            println("Repositório: Erro ao excluir exame com ID '$id' - ${e.message}")
            Result.failure(e)
        }
    }
}
