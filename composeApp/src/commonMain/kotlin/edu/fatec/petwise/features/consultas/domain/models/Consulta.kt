package edu.fatec.petwise.features.consultas.domain.models
import kotlinx.serialization.Serializable

@Serializable
data class Consulta(
    val id: String,
    val petId: String,
    val petName: String,
    val veterinarianName: String,
    val consultaType: ConsultaType,
    val consultaDate: kotlinx.datetime.LocalDateTime,
    val consultaTime: String,
    val status: ConsultaStatus,
    val symptoms: String = "",
    val diagnosis: String = "",
    val treatment: String = "",
    val prescriptions: String = "",
    val notes: String = "",
    val nextAppointment: kotlinx.datetime.LocalDateTime? = null,
    val price: Float = 0f,
    val isPaid: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)

enum class ConsultaType(val displayName: String) {
    ROUTINE("Consulta de Rotina"),
    EMERGENCY("Emergência"),
    FOLLOW_UP("Retorno"),
    VACCINATION("Vacinação"),
    SURGERY("Cirurgia"),
    EXAM("Exame"),
    OTHER("Outro")
}

enum class ConsultaStatus(val displayName: String, val color: String) {
    SCHEDULED("Agendada", "#2196F3"),
    IN_PROGRESS("Em Andamento", "#FF9800"),
    COMPLETED("Concluída", "#4CAF50"),
    CANCELLED("Cancelada", "#F44336"),
    RESCHEDULED("Remarcada", "#9C27B0")
}

data class ConsultaFilterOptions(
    val consultaType: ConsultaType? = null,
    val status: ConsultaStatus? = null,
    val petId: String? = null,
    val dateRange: DateRange? = null,
    val searchQuery: String = ""
)

data class DateRange(
    val startDate: kotlinx.datetime.LocalDateTime,
    val endDate: kotlinx.datetime.LocalDateTime
)
