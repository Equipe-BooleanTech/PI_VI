package edu.fatec.petwise.features.exams.data.datasource

import edu.fatec.petwise.features.exams.domain.models.Exam

interface RemoteExamDataSource {
    suspend fun getAllExams(): List<Exam>
    suspend fun getExamById(id: String): Exam?
    suspend fun createExam(exam: Exam): Exam
    suspend fun updateExam(exam: Exam): Exam
    suspend fun deleteExam(id: String)
    suspend fun searchExams(query: String): List<Exam>
    suspend fun getExamsByPetId(petId: String): List<Exam>
    suspend fun getExamsByVeterinaryId(veterinaryId: String): List<Exam>
}
