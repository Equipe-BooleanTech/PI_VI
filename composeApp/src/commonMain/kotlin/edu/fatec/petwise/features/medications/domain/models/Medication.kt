package edu.fatec.petwise.features.medications.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Medication(
    val id: String,
    val userId: String,
    val prescriptionId: String,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val durationDays: Int,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val sideEffects: String = "",
    val status: MedicationStatus = MedicationStatus.ACTIVE,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

enum class MedicationFrequency(val displayName: String) {
    ONCE_DAILY("1x ao dia"),
    TWICE_DAILY("2x ao dia"),
    THREE_TIMES_DAILY("3x ao dia"),
    FOUR_TIMES_DAILY("4x ao dia"),
    EVERY_8_HOURS("A cada 8 horas"),
    EVERY_12_HOURS("A cada 12 horas"),
    AS_NEEDED("Conforme necessário"),
    OTHER("Outro")
}

enum class MedicationStatus(val displayName: String, val color: String) {
    ACTIVE("Ativo", "#4CAF50"),
    COMPLETED("Concluído", "#2196F3"),
    PAUSED("Pausado", "#FF9800"),
    CANCELLED("Cancelado", "#F44336")
}

data class MedicationFilterOptions(
    val petId: String? = null,
    val status: MedicationStatus? = null,
    val medicationName: String? = null,
    val searchQuery: String = ""
)