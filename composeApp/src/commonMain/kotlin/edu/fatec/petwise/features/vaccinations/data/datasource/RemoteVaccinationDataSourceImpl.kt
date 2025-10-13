package edu.fatec.petwise.features.vaccinations.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.VaccinationApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination

class RemoteVaccinationDataSourceImpl(
    private val vaccinationApiService: VaccinationApiService
) : RemoteVaccinationDataSource {

    override suspend fun getAllVaccinations(): List<Vaccination> {
        return when (val result = vaccinationApiService.getAllVaccinations()) {
            is NetworkResult.Success -> result.data.vaccinations.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getVaccinationById(id: String): Vaccination? {
        return when (val result = vaccinationApiService.getVaccinationById(id)) {
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

    override suspend fun getVaccinationsByPetId(petId: String): List<Vaccination> {
        return when (val result = vaccinationApiService.getVaccinationsByPetId(petId)) {
            is NetworkResult.Success -> result.data.vaccinations.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun createVaccination(vaccination: Vaccination): Vaccination {
        val request = CreateVaccinationRequest(
            petId = vaccination.petId,
            vaccineName = vaccination.vaccineName,
            vaccineType = vaccination.vaccineType.name,
            applicationDate = vaccination.applicationDate,
            nextDoseDate = vaccination.nextDoseDate,
            doseNumber = vaccination.doseNumber,
            totalDoses = vaccination.totalDoses,
            veterinarianName = vaccination.veterinarianName,
            veterinarianCrmv = vaccination.veterinarianCrmv,
            clinicName = vaccination.clinicName,
            batchNumber = vaccination.batchNumber,
            manufacturer = vaccination.manufacturer,
            observations = vaccination.observations,
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
            vaccineName = vaccination.vaccineName,
            applicationDate = vaccination.applicationDate,
            nextDoseDate = vaccination.nextDoseDate,
            veterinarianName = vaccination.veterinarianName,
            veterinarianCrmv = vaccination.veterinarianCrmv,
            clinicName = vaccination.clinicName,
            batchNumber = vaccination.batchNumber,
            manufacturer = vaccination.manufacturer,
            observations = vaccination.observations,
            sideEffects = vaccination.sideEffects,
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
        sideEffects: String,
        applicationDate: String
    ): Vaccination {
        val request = MarkAsAppliedRequest(
            observations = observations,
            sideEffects = sideEffects,
            applicationDate = applicationDate
        )

        return when (val result = vaccinationApiService.markAsApplied(id, request)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun scheduleNextDose(id: String, nextDoseDate: String): Vaccination {
        val request = ScheduleNextDoseRequest(nextDoseDate = nextDoseDate)

        return when (val result = vaccinationApiService.scheduleNextDose(id, request)) {
            is NetworkResult.Success -> result.data.toDomain()
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun getUpcomingVaccinations(days: Int): List<Vaccination> {
        return when (val result = vaccinationApiService.getUpcomingVaccinations(days)) {
            is NetworkResult.Success -> result.data.vaccinations.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getOverdueVaccinations(): List<Vaccination> {
        return when (val result = vaccinationApiService.getOverdueVaccinations()) {
            is NetworkResult.Success -> result.data.vaccinations.map { it.toDomain() }
            is NetworkResult.Error -> throw result.exception
            is NetworkResult.Loading -> emptyList()
        }
    }
}
