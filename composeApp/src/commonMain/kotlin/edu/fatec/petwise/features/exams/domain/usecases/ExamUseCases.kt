package edu.fatec.petwise.features.exams.domain.usecases

import edu.fatec.petwise.features.exams.domain.models.Exam
import edu.fatec.petwise.features.exams.domain.repository.ExamRepository
import kotlinx.coroutines.flow.Flow

class GetExamsUseCase(
    private val repository: ExamRepository
) {
    operator fun invoke(): Flow<List<Exam>> = repository.getAllExams()

    fun searchExams(query: String): Flow<List<Exam>> = repository.searchExams(query)

    fun getExamsByPetId(petId: String): Flow<List<Exam>> = repository.getExamsByPetId(petId)

    fun getExamsByVeterinaryId(veterinaryId: String): Flow<List<Exam>> = repository.getExamsByVeterinaryId(veterinaryId)
}

class GetExamByIdUseCase(
    private val repository: ExamRepository
) {
    operator fun invoke(id: String): Flow<Exam?> = repository.getExamById(id)
}

class AddExamUseCase(
    private val repository: ExamRepository
) {
    suspend operator fun invoke(exam: Exam): Result<Exam> {
        return if (validateExam(exam)) {
            repository.addExam(exam)
        } else {
            Result.failure(IllegalArgumentException("Exam data is invalid"))
        }
    }

    private fun validateExam(exam: Exam): Boolean {
        return exam.examType.isNotBlank() &&
               exam.petId.isNotBlank() &&
               exam.veterinaryId.isNotBlank() &&
               exam.examDate.isNotBlank()
    }
}

class UpdateExamUseCase(
    private val repository: ExamRepository
) {
    suspend operator fun invoke(exam: Exam): Result<Exam> = repository.updateExam(exam)
}

class DeleteExamUseCase(
    private val repository: ExamRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteExam(id)
}
