package edu.fatec.petwise.core.network.dto

import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.models.ConsultaType
import edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus
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
    val ownerName: String,
    val ownerPhone: String,
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
    val ownerName: String,
    val ownerPhone: String,
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
    consultaType = ConsultaType.valueOf(consultaType),
    consultaDate = consultaDate,
    consultaTime = consultaTime,
    status = ConsultaStatus.valueOf(status),
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
