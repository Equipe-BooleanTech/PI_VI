package edu.fatec.petwise.core.network.dto

import edu.fatec.petwise.features.consultas.domain.models.Consulta
import kotlinx.serialization.SerialName
import edu.fatec.petwise.features.consultas.domain.models.ConsultaType
import edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus
import kotlinx.serialization.Serializable


@Serializable
data class ConsultaDto(
    val id: String,
    val petId: String,
    @SerialName("petName") val petName: String,
    @SerialName("veterinarianName") val veterinarianName: String,
    @SerialName("consultaType") val consultaType: String,
    @SerialName("consultaDate") val consultaDate: String,
    @SerialName("consultaTime") val consultaTime: String,
    val status: String,
    val symptoms: String = "",
    val diagnosis: String = "",
    val treatment: String = "",
    val prescriptions: String = "",
    val notes: String = "",
    val nextAppointment: String? = null,
    val price: Float = 0f,
    val isPaid: Boolean = false,
    val ownerName: String,
    val ownerPhone: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateConsultaRequest(
    @SerialName("petId") val petId: String,
    // keep for compatibility
    @SerialName("petName") val petName: String,
    @SerialName("veterinarianName") val veterinarianName: String,
    // backend expects a single datetime and a veterinaryId; expose optional fields
    @SerialName("appointmentDatetime") val appointmentDatetime: String? = null,
    @SerialName("veterinaryId") val veterinaryId: String? = null,
    @SerialName("consultaType") val consultaType: String,
    @SerialName("consultaDate") val consultaDate: String,
    @SerialName("consultaTime") val consultaTime: String,
    @SerialName("symptoms") val symptoms: String = "",
    @SerialName("ownerName") val ownerName: String,
    @SerialName("ownerPhone") val ownerPhone: String,
    @SerialName("notes") val notes: String = ""
)

@Serializable
data class UpdateConsultaRequest(
    @SerialName("veterinarianName") val veterinarianName: String? = null,
    @SerialName("consultaType") val consultaType: String? = null,
    @SerialName("consultaDate") val consultaDate: String? = null,
    @SerialName("consultaTime") val consultaTime: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("symptoms") val symptoms: String? = null,
    @SerialName("diagnosis") val diagnosis: String? = null,
    @SerialName("treatment") val treatment: String? = null,
    @SerialName("prescriptions") val prescriptions: String? = null,
    @SerialName("notes") val notes: String? = null,
    @SerialName("nextAppointment") val nextAppointment: String? = null,
    @SerialName("price") val price: Float? = null,
    @SerialName("isPaid") val isPaid: Boolean? = null
)

@Serializable
data class ConsultaListResponse(
    val consultas: List<ConsultaDto>,
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
    consultaDate = consultaDate,
    consultaTime = consultaTime,
    status = status.name,
    symptoms = symptoms,
    diagnosis = diagnosis,
    treatment = treatment,
    prescriptions = prescriptions,
    notes = notes,
    nextAppointment = nextAppointment,
    price = price,
    isPaid = isPaid,
    ownerName = ownerName,
    ownerPhone = ownerPhone,
    createdAt = createdAt,
    updatedAt = updatedAt
)

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
    ownerName = ownerName,
    ownerPhone = ownerPhone,
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
            "Odontologia" -> ConsultaType.DENTAL
            "Estética" -> ConsultaType.GROOMING
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
