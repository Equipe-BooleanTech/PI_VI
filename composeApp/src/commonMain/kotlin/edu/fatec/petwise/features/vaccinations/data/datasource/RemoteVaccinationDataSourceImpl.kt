package edu.fatec.petwise.features.vaccinations.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.VaccinationApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.core.network.NetworkException.NotFound
import kotlinx.datetime.LocalDateTime

class RemoteVaccinationDataSourceImpl(
    private val vaccinationApiService: VaccinationApiService
) : RemoteVaccinationDataSource {

    override suspend fun getAllVaccinations(): List<Vaccination> {
        return when (val result = vaccinationApiService.getAllVaccinations(1, 1000)) {
            is NetworkResult.Success -> result.data.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getVaccinationById(id: String): Vaccination? {
        return when (val result = vaccinationApiService.getVaccinationById(id)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> {
                if (result.exception is NotFound) {
                    null
                } else {
                    throw result.exception
                }
            }
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun getVaccinationsByPetId(petId: String): List<Vaccination> {
        return when (val result = vaccinationApiService.getVaccinationsByPetId(petId, 1, 1000)) {
            is NetworkResult.Success -> result.data.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun createVaccination(vaccination: Vaccination): Vaccination {
        val request = CreateVaccinationRequest(
            petId = vaccination.petId,
            veterinarianId = vaccination.veterinarianId,
            vaccineType = vaccination.vaccineType.name,
            vaccinationDate = parseDateToIso(vaccination.vaccinationDate),
            nextDoseDate = vaccination.nextDoseDate?.let { parseDateToIso(it) },
            manufacturer = vaccination.manufacturer ?: "",
            observations = vaccination.observations ?: "",
            totalDoses = vaccination.totalDoses,
            status = vaccination.status.name
        )

        return when (val result = vaccinationApiService.createVaccination(request)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun updateVaccination(vaccination: Vaccination): Vaccination {
        val request = UpdateVaccinationRequest(
            petId = vaccination.petId,
            veterinarianId = vaccination.veterinarianId,
            vaccineType = vaccination.vaccineType.name,
            vaccinationDate = parseDateToIso(vaccination.vaccinationDate),
            nextDoseDate = vaccination.nextDoseDate?.let { parseDateToIso(it) },
            manufacturer = vaccination.manufacturer ?: "",
            observations = vaccination.observations,
            totalDoses = vaccination.totalDoses,
            status = vaccination.status.name
        )

        return when (val result = vaccinationApiService.updateVaccination(vaccination.id, request)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun deleteVaccination(id: String) {
        when (val result = vaccinationApiService.deleteVaccination(id)) {
            is NetworkResult.Success -> Unit
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun markAsApplied(
        id: String,
        observations: String,
        vaccinationDate: String
    ): Vaccination {
        val request = MarkAsAppliedRequest(
            observations = observations,
            vaccinationDate = parseDateToIso(vaccinationDate)
        )

        return when (val result = vaccinationApiService.markAsApplied(id, request)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun scheduleNextDose(id: String, nextDoseDate: String): Vaccination {
        val request = ScheduleNextDoseRequest(nextDoseDate = parseDateToIso(nextDoseDate))

        return when (val result = vaccinationApiService.scheduleNextDose(id, request)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun getUpcomingVaccinations(days: Int): List<Vaccination> {
        return when (val result = vaccinationApiService.getUpcomingVaccinations(days)) {
            is NetworkResult.Success -> result.data.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getOverdueVaccinations(): List<Vaccination> {
        return when (val result = vaccinationApiService.getOverdueVaccinations()) {
            is NetworkResult.Success -> result.data.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }
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

    val localDateTime = LocalDateTime(year, month, day, 0, 0, 0)
    return localDateTime.toString()
}
