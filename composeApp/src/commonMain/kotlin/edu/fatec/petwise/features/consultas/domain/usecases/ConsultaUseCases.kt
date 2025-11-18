package edu.fatec.petwise.features.consultas.domain.usecases

import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.models.ConsultaFilterOptions
import edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus
import edu.fatec.petwise.features.consultas.domain.repository.ConsultaRepository
import kotlinx.coroutines.flow.Flow

class GetConsultasUseCase(
    private val repository: ConsultaRepository
) {
    operator fun invoke(): Flow<List<Consulta>> = repository.getAllConsultas()

    fun filterConsultas(options: ConsultaFilterOptions): Flow<List<Consulta>> = 
        repository.filterConsultas(options)

    fun searchConsultas(query: String): Flow<List<Consulta>> = 
        repository.searchConsultas(query)

    fun getUpcoming(): Flow<List<Consulta>> = 
        repository.getUpcomingConsultas()

    fun getByStatus(status: ConsultaStatus): Flow<List<Consulta>> = 
        repository.getConsultasByStatus(status)
}

class GetConsultaByIdUseCase(
    private val repository: ConsultaRepository
) {
    operator fun invoke(id: String): Flow<Consulta?> = repository.getConsultaById(id)
}

class GetConsultasByPetIdUseCase(
    private val repository: ConsultaRepository
) {
    operator fun invoke(petId: String): Flow<List<Consulta>> = 
        repository.getConsultasByPetId(petId)
}

class AddConsultaUseCase(
    private val repository: ConsultaRepository
) {
    suspend operator fun invoke(consulta: Consulta): Result<Consulta> {
        return if (validateConsulta(consulta)) {
            repository.addConsulta(consulta)
        } else {
            Result.failure(Exception("Dados da consulta inválidos"))
        }
    }

    private fun validateConsulta(consulta: Consulta): Boolean {
        return consulta.consultaTime.isNotBlank() &&
               consulta.price >= 0
    }
}

class UpdateConsultaUseCase(
    private val repository: ConsultaRepository
) {
    suspend operator fun invoke(consulta: Consulta): Result<Consulta> = 
        repository.updateConsulta(consulta)
}

class DeleteConsultaUseCase(
    private val repository: ConsultaRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = 
        repository.deleteConsulta(id)
}

class UpdateConsultaStatusUseCase(
    private val repository: ConsultaRepository
) {
    suspend operator fun invoke(id: String, status: ConsultaStatus): Result<Consulta> {
        return repository.updateConsultaStatus(id, status)
    }
}

class MarkConsultaAsPaidUseCase(
    private val repository: ConsultaRepository
) {
    suspend operator fun invoke(id: String): Result<Consulta> = 
        repository.markAsPaid(id)
}
