package edu.fatec.petwise.features.consultas.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.ConsultaApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus

class RemoteConsultaDataSourceImpl(
    private val consultaApiService: ConsultaApiService
) : RemoteConsultaDataSource {

    override suspend fun getAllConsultas(): List<Consulta> {
        return when (val result = consultaApiService.getAllConsultas()) {
            is NetworkResult.Success -> result.data.consultas.map { it.toDomain() }
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
            consultaDate = consulta.consultaDate,
            consultaTime = consulta.consultaTime,
            symptoms = consulta.symptoms,
            ownerName = consulta.ownerName,
            ownerPhone = consulta.ownerPhone,
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
            consultaDate = consulta.consultaDate,
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
            is NetworkResult.Success -> Unit
            is NetworkResult.Error -> throw result.exception
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
            is NetworkResult.Success -> result.data.consultas.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }

    suspend fun getUpcomingConsultas(): List<Consulta> {
        return when (val result = consultaApiService.getUpcomingConsultas()) {
            is NetworkResult.Success -> result.data.consultas.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }

    suspend fun getConsultasByPet(petId: String): List<Consulta> {
        return when (val result = consultaApiService.getConsultasByPet(petId)) {
            is NetworkResult.Success -> result.data.consultas.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }
}
