package edu.fatec.petwise.features.consultas.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.ConsultaApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus
import kotlinx.datetime.LocalDateTime

class RemoteConsultaDataSourceImpl(
    private val consultaApiService: ConsultaApiService
) : RemoteConsultaDataSource {

    override suspend fun getAllConsultas(): List<Consulta> {
        return when (val result = consultaApiService.getAllConsultas()) {
            is NetworkResult.Success -> result.data.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getConsultaById(id: String): Consulta? {
        return when (val result = consultaApiService.getConsultaById(id)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> {
                if (result.exception is edu.fatec.petwise.core.network.NetworkException.NotFound) {
                    null
                } else {
                    throw result.exception
                }
            }
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun createConsulta(consulta: Consulta): Consulta {
        val request = CreateConsultaRequest(
            petId = consulta.petId,
            petName = consulta.petName,
            veterinarianName = consulta.veterinarianName,
            consultaType = consulta.consultaType.name,
            consultaDate = parseDateTimeToIso(consulta.consultaDate, consulta.consultaTime),
            consultaTime = consulta.consultaTime,
            symptoms = consulta.symptoms,
            notes = consulta.notes
        )

        return when (val result = consultaApiService.createConsulta(request)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun updateConsulta(consulta: Consulta): Consulta {
        val request = UpdateConsultaRequest(
            veterinarianName = consulta.veterinarianName,
            consultaType = consulta.consultaType.name,
            consultaDate = consulta.consultaDate?.let { parseDateTimeToIso(it, consulta.consultaTime ?: "") },
            consultaTime = consulta.consultaTime,
            status = consulta.status.name,
            symptoms = consulta.symptoms,
            diagnosis = consulta.diagnosis,
            treatment = consulta.treatment,
            prescriptions = consulta.prescriptions,
            notes = consulta.notes,
            nextAppointment = consulta.nextAppointment,
            price = consulta.price,
            isPaid = consulta.isPaid
        )

        return when (val result = consultaApiService.updateConsulta(consulta.id, request)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun deleteConsulta(id: String) {
        when (val result = consultaApiService.deleteConsulta(id)) {
            is NetworkResult.Success -> {
                val cancelledConsulta = result.data
                println("Consulta ${id} cancelada com sucesso via API DELETE. Novo status: ${cancelledConsulta.status}")
            }
            is NetworkResult.Error -> {
                val errorMessage = result.exception.message ?: "Erro ao excluir consulta"
                println("Erro ao deletar consulta ${id}: $errorMessage")
                throw Exception(errorMessage)
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    suspend fun updateConsultaStatus(id: String, status: ConsultaStatus, notes: String? = null): Consulta {
        val request = UpdateConsultaStatusRequest(
            status = status.name,
            notes = notes
        )

        return when (val result = consultaApiService.updateConsultaStatus(id, request)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    suspend fun cancelConsulta(id: String, reason: String? = null): Consulta {
        val request = CancelConsultaRequest(reason = reason)

        return when (val result = consultaApiService.cancelConsulta(id, request)) {
            is NetworkResult.Success -> {
                getConsultaById(id) ?: throw Exception("Consulta não encontrada após cancelamento")
            }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    suspend fun searchConsultas(query: String): List<Consulta> {
        return when (val result = consultaApiService.searchConsultas(query)) {
            is NetworkResult.Success -> result.data.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }

    suspend fun getUpcomingConsultas(): List<Consulta> {
        return when (val result = consultaApiService.getUpcomingConsultas()) {
            is NetworkResult.Success -> result.data.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }

    suspend fun getConsultasByPet(petId: String): List<Consulta> {
        return when (val result = consultaApiService.getConsultasByPet(petId)) {
            is NetworkResult.Success -> result.data.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }
}

private fun parseDateTimeToIso(date: String, time: String): String {
    val dateParts = if (date.contains("/")) {
        // DD/MM/YYYY format
        date.split("/")
    } else {
        // YYYY-MM-DD format
        date.split("-").reversed() // Reverse to DD/MM/YYYY
    }
    val day = dateParts[0].toInt()
    val month = dateParts[1].toInt()
    val year = dateParts[2].toInt()

    val timeParts = time.split(":")
    val hour = timeParts[0].toInt()
    val minute = timeParts[1].toInt()

    val localDateTime = LocalDateTime(year, month, day, hour, minute)
    return localDateTime.toString()
}
