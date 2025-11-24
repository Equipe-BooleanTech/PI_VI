package edu.fatec.petwise.features.vaccinations.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Vaccination(
    val id: String,
    val petId: String,
    val veterinarianId: String,
    val vaccineType: VaccineType,
    val vaccinationDate: kotlinx.datetime.LocalDateTime,
    val nextDoseDate: kotlinx.datetime.LocalDateTime?,
    val totalDoses: Int,
    val manufacturer: String?,
    val observations: String = "",
    val status: VaccinationStatus,
    val createdAt: String,
    val updatedAt: String
)

enum class VaccineType {
    V8, V10, ANTIRABICA, GRIPE_CANINA, GIARDIA, LEPTOSPIROSE,
    TRIPLE_FELINA, QUADRUPLA_FELINA, LEUCEMIA_FELINA, RAIVA_FELINA,
    OUTRAS;

    fun getDisplayName(): String = when (this) {
        V8 -> "Vacina V8"
        V10 -> "Vacina V10"
        ANTIRABICA -> "Antirrábica"
        GRIPE_CANINA -> "Gripe Canina"
        GIARDIA -> "Giárdia"
        LEPTOSPIROSE -> "Leptospirose"
        TRIPLE_FELINA -> "Tríplice Felina"
        QUADRUPLA_FELINA -> "Quádrupla Felina"
        LEUCEMIA_FELINA -> "Leucemia Felina"
        RAIVA_FELINA -> "Raiva Felina"
        OUTRAS -> "Outras"
    }
}

enum class VaccinationStatus {
    AGENDADA, APLICADA, ATRASADA, CANCELADA;

    fun getDisplayName(): String = when (this) {
        AGENDADA -> "Agendada"
        APLICADA -> "Aplicada"
        ATRASADA -> "Atrasada"
        CANCELADA -> "Cancelada"
    }
}

data class VaccinationFilterOptions(
    val petId: String? = null,
    val vaccineType: VaccineType? = null,
    val status: VaccinationStatus? = null,
    val startDate: kotlinx.datetime.LocalDateTime? = null,
    val endDate: kotlinx.datetime.LocalDateTime? = null,
    val searchQuery: String = ""
)
