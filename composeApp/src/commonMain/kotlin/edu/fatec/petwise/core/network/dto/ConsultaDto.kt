package edu.fatec.petwise.core.network.dto

import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.models.ConsultaType
import edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable


@Serializable
data class MessageResponse(
    val message: String
)

@Serializable
data class ConsultaDto(
    val id: String,
    val petId: String,
    val petName: String,
    val veterinarianName: String,
    val consultaType: String,
    val consultaDate: String,
    val consultaTime: String,
    val status: String,
    val symptoms: String = "",
    val diagnosis: String = "",
    val treatment: String = "",
    val prescriptions: String = "",
    val notes: String = "",
    val nextAppointment: String? = null,
    val price: Float = 0f,
    val isPaid: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateConsultaRequest(
    val petId: String,
    val petName: String,
    val veterinarianName: String,
    val consultaType: String,
    val consultaDate: String,
    val consultaTime: String,
    val symptoms: String = "",
    val notes: String = ""
)

@Serializable
data class UpdateConsultaRequest(
    val veterinarianName: String? = null,
    val consultaType: String? = null,
    val consultaDate: String,
    val consultaTime: String? = null,
    val status: String? = null,
    val symptoms: String? = null,
    val diagnosis: String? = null,
    val treatment: String? = null,
    val prescriptions: String? = null,
    val notes: String? = null,
    val nextAppointment: String? = null,
    val price: Float? = null,
    val isPaid: Boolean? = null
)

@Serializable
data class ConsultaListResponse(
    val consultas: List<ConsultaDto>? = null,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

@Serializable
data class UpdateConsultaStatusRequest(
    val status: String,
    val notes: String? = null
)

@Serializable
data class CancelConsultaRequest(
    val reason: String? = null
)

@Serializable
data class CancelConsultaResponse(
    val consultaId: String,
    val status: String,
    val message: String
)

fun Consulta.toDto(): ConsultaDto = ConsultaDto(
    id = id,
    petId = petId,
    petName = petName,
    veterinarianName = veterinarianName,
    consultaType = consultaType.name,
    consultaDate = consultaDate.toString(),
    consultaTime = consultaTime,
    status = status.name,
    symptoms = symptoms,
    diagnosis = diagnosis,
    treatment = treatment,
    prescriptions = prescriptions,
    notes = notes,
    nextAppointment = nextAppointment?.toString(),
    price = price,
    isPaid = isPaid,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun mapStringToConsultaType(value: String): ConsultaType {
    return try {
        ConsultaType.valueOf(value.uppercase())
    } catch (e: IllegalArgumentException) {
        when (value) {
            "Consulta de Rotina" -> ConsultaType.ROUTINE
            "Emergência" -> ConsultaType.EMERGENCY
            "Retorno" -> ConsultaType.FOLLOW_UP
            "Vacinação" -> ConsultaType.VACCINATION
            "Cirurgia" -> ConsultaType.SURGERY
            "Exame" -> ConsultaType.EXAM
            "Outro" -> ConsultaType.OTHER
            else -> ConsultaType.OTHER
        }
    }
}

private fun mapStringToConsultaStatus(value: String): ConsultaStatus {
    return try {
        ConsultaStatus.valueOf(value.uppercase())
    } catch (e: IllegalArgumentException) {
        when (value) {
            "Agendada" -> ConsultaStatus.SCHEDULED
            "Em Andamento" -> ConsultaStatus.IN_PROGRESS
            "Concluída" -> ConsultaStatus.COMPLETED
            "Cancelada" -> ConsultaStatus.CANCELLED
            "Remarcada" -> ConsultaStatus.RESCHEDULED
            else -> ConsultaStatus.SCHEDULED
        }
    }
}

fun ConsultaDto.toDomain(): Consulta = Consulta(
    id = id,
    petId = petId,
    petName = petName,
    veterinarianName = veterinarianName,
    consultaType = mapStringToConsultaType(consultaType),
    consultaDate = LocalDateTime.parse(consultaDate),
    consultaTime = consultaTime,
    status = mapStringToConsultaStatus(status),
    symptoms = symptoms,
    diagnosis = diagnosis,
    treatment = treatment,
    prescriptions = prescriptions,
    notes = notes,
    nextAppointment = nextAppointment?.let { LocalDateTime.parse(it) },
    price = price,
    isPaid = isPaid,
    createdAt = createdAt,
    updatedAt = updatedAt
)
