package edu.fatec.petwise.features.exams.data.datasource

import edu.fatec.petwise.features.exams.domain.models.Exam

class RemoteExamDataSourceImpl : RemoteExamDataSource {

    override suspend fun getAllExams(): List<Exam> {
        println("API: Buscando todos os exames")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun getExamById(id: String): Exam? {
        println("API: Buscando exame por ID: $id")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun createExam(exam: Exam): Exam {
        println("API: Criando novo exame - ${exam.examType}")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun updateExam(exam: Exam): Exam {
        println("API: Atualizando exame - ${exam.examType} (ID: ${exam.id})")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun deleteExam(id: String) {
        println("API: Excluindo exame com ID: $id")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun searchExams(query: String): List<Exam> {
        println("API: Buscando exames com query: '$query'")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun getExamsByPetId(petId: String): List<Exam> {
        println("API: Buscando exames do pet: $petId")
        throw NotImplementedError("API endpoint not implemented yet")
    }

    override suspend fun getExamsByVeterinaryId(veterinaryId: String): List<Exam> {
        println("API: Buscando exames do veterinário: $veterinaryId")
        throw NotImplementedError("API endpoint not implemented yet")
    }
}
