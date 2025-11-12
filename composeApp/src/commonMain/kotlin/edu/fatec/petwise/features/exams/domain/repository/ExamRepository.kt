package edu.fatec.petwise.features.exams.domain.repository

import edu.fatec.petwise.features.exams.domain.models.Exam
import kotlinx.coroutines.flow.Flow

interface ExamRepository {
    fun getAllExams(): Flow<List<Exam>>
    fun getExamById(id: String): Flow<Exam?>
    fun searchExams(query: String): Flow<List<Exam>>
    fun getExamsByPetId(petId: String): Flow<List<Exam>>
    fun getExamsByVeterinaryId(veterinaryId: String): Flow<List<Exam>>
    suspend fun addExam(exam: Exam): Result<Exam>
    suspend fun updateExam(exam: Exam): Result<Exam>
    suspend fun deleteExam(id: String): Result<Unit>
}
