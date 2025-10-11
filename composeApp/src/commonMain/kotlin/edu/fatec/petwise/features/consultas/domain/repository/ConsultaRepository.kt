package edu.fatec.petwise.features.consultas.domain.repository

import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.models.ConsultaFilterOptions
import edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus
import kotlinx.coroutines.flow.Flow

interface ConsultaRepository {
    fun getAllConsultas(): Flow<List<Consulta>>

    fun getConsultaById(id: String): Flow<Consulta?>

    fun getConsultasByPetId(petId: String): Flow<List<Consulta>>

    fun searchConsultas(query: String): Flow<List<Consulta>>

    fun filterConsultas(options: ConsultaFilterOptions): Flow<List<Consulta>>

    fun getUpcomingConsultas(): Flow<List<Consulta>>

    fun getConsultasByStatus(status: ConsultaStatus): Flow<List<Consulta>>

    suspend fun addConsulta(consulta: Consulta): Result<Consulta>

    suspend fun updateConsulta(consulta: Consulta): Result<Consulta>

    suspend fun deleteConsulta(id: String): Result<Unit>

    suspend fun updateConsultaStatus(id: String, status: ConsultaStatus): Result<Consulta>

    suspend fun markAsPaid(id: String): Result<Consulta>
}
