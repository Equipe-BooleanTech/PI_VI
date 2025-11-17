package edu.fatec.petwise.core.network.dto

import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.models.ConsultaType
import edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable


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
    val consultaDate: String? = null,
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
    consultaDate = parseDateTimeToIso(consultaDate, consultaTime),
    consultaTime = consultaTime,
    status = status.name,
    symptoms = symptoms,
    diagnosis = diagnosis,
    treatment = treatment,
    prescriptions = prescriptions,
    notes = notes,
    nextAppointment = nextAppointment?.let { parseDateToIso(it) },
    price = price,
    isPaid = isPaid,
    createdAt = createdAt,
    updatedAt = updatedAt
)

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

private fun parseDateToIso(date: String): String {
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

    val localDateTime = LocalDateTime(year, month, day, 0, 0)
    return localDateTime.toString()
}

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
    consultaDate = consultaDate,
    consultaTime = consultaTime,
    status = mapStringToConsultaStatus(status),
    symptoms = symptoms,
    diagnosis = diagnosis,
    treatment = treatment,
    prescriptions = prescriptions,
    notes = notes,
    nextAppointment = nextAppointment,
    price = price,
    isPaid = isPaid,
    createdAt = createdAt,
    updatedAt = updatedAt
)
