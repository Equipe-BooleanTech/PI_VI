package edu.fatec.petwise.features.consultas.data.repository

import edu.fatec.petwise.features.consultas.data.datasource.LocalConsultaDataSource
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.models.ConsultaFilterOptions
import edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus
import edu.fatec.petwise.features.consultas.domain.repository.ConsultaRepository
import edu.fatec.petwise.presentation.shared.form.currentTimeMs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ConsultaRepositoryImpl(
    private val localDataSource: LocalConsultaDataSource
) : ConsultaRepository {

    override fun getAllConsultas(): Flow<List<Consulta>> = localDataSource.getAllConsultas()

    override fun getConsultaById(id: String): Flow<Consulta?> = localDataSource.getConsultaById(id)

    override fun getConsultasByPetId(petId: String): Flow<List<Consulta>> {
        return localDataSource.getAllConsultas().map { consultas ->
            consultas.filter { it.petId == petId }
        }
    }

    override fun searchConsultas(query: String): Flow<List<Consulta>> {
        return localDataSource.getAllConsultas().map { consultas ->
            consultas.filter { consulta ->
                consulta.petName.contains(query, ignoreCase = true) ||
                consulta.veterinarianName.contains(query, ignoreCase = true) ||
                consulta.ownerName.contains(query, ignoreCase = true) ||
                consulta.diagnosis.contains(query, ignoreCase = true) ||
                consulta.symptoms.contains(query, ignoreCase = true)
            }
        }
    }

    override fun filterConsultas(options: ConsultaFilterOptions): Flow<List<Consulta>> {
        return localDataSource.getAllConsultas().map { consultas ->
            consultas.filter { consulta ->
                val typeMatch = options.consultaType?.let { consulta.consultaType == it } ?: true
                val statusMatch = options.status?.let { consulta.status == it } ?: true
                val petMatch = options.petId?.let { consulta.petId == it } ?: true
                val searchMatch = if (options.searchQuery.isNotBlank()) {
                    consulta.petName.contains(options.searchQuery, ignoreCase = true) ||
                    consulta.veterinarianName.contains(options.searchQuery, ignoreCase = true) ||
                    consulta.ownerName.contains(options.searchQuery, ignoreCase = true)
                } else true

                typeMatch && statusMatch && petMatch && searchMatch
            }
        }
    }

    override fun getUpcomingConsultas(): Flow<List<Consulta>> {
        return localDataSource.getAllConsultas().map { consultas ->
            consultas.filter { it.status == ConsultaStatus.SCHEDULED }
                .sortedBy { it.consultaDate }
        }
    }

    override fun getConsultasByStatus(status: ConsultaStatus): Flow<List<Consulta>> {
        return localDataSource.getAllConsultas().map { consultas ->
            consultas.filter { it.status == status }
        }
    }

    override suspend fun addConsulta(consulta: Consulta): Result<Consulta> {
        return try {
            val newConsulta = consulta.copy(
                id = generateId(),
                createdAt = getCurrentTimestamp(),
                updatedAt = getCurrentTimestamp()
            )
            localDataSource.insertConsulta(newConsulta)
            Result.success(newConsulta)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateConsulta(consulta: Consulta): Result<Consulta> {
        return try {
            val updatedConsulta = consulta.copy(updatedAt = getCurrentTimestamp())
            localDataSource.updateConsulta(updatedConsulta)
            Result.success(updatedConsulta)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteConsulta(id: String): Result<Unit> {
        return try {
            localDataSource.deleteConsulta(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateConsultaStatus(id: String, status: ConsultaStatus): Result<Consulta> {
        return try {
            val currentConsulta = localDataSource.getConsultaById(id).first()

            currentConsulta?.let { consulta ->
                val updatedConsulta = consulta.copy(
                    status = status,
                    updatedAt = getCurrentTimestamp()
                )
                localDataSource.updateConsulta(updatedConsulta)
                Result.success(updatedConsulta)
            } ?: Result.failure(IllegalArgumentException("Consulta not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsPaid(id: String): Result<Consulta> {
        return try {
            val currentConsulta = localDataSource.getConsultaById(id).first()

            currentConsulta?.let { consulta ->
                val updatedConsulta = consulta.copy(
                    isPaid = true,
                    updatedAt = getCurrentTimestamp()
                )
                localDataSource.updateConsulta(updatedConsulta)
                Result.success(updatedConsulta)
            } ?: Result.failure(IllegalArgumentException("Consulta not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateId(): String = currentTimeMs().toString()

    private fun getCurrentTimestamp(): String {
        return currentTimeMs().toString()
    }
}
