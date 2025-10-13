package edu.fatec.petwise.features.vaccinations.data.repository

import edu.fatec.petwise.features.vaccinations.data.datasource.LocalVaccinationDataSource
import edu.fatec.petwise.features.vaccinations.data.datasource.RemoteVaccinationDataSource
import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.vaccinations.domain.models.VaccinationFilterOptions
import edu.fatec.petwise.features.vaccinations.domain.repository.VaccinationRepository
import edu.fatec.petwise.presentation.shared.form.currentTimeMs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class VaccinationRepositoryImpl(
    private val localDataSource: LocalVaccinationDataSource,
    private val remoteDataSource: RemoteVaccinationDataSource? = null,
    private val syncStrategy: SyncStrategy = SyncStrategy.CACHE_FIRST
) : VaccinationRepository {

    override fun getAllVaccinations(): Flow<List<Vaccination>> = flow {
        if (remoteDataSource == null) {
            localDataSource.getAllVaccinations().collect { emit(it) }
            return@flow
        }

        when (syncStrategy) {
            SyncStrategy.CACHE_FIRST -> {
                val cachedVaccinations = localDataSource.getAllVaccinations().first()
                if (cachedVaccinations.isNotEmpty()) {
                    emit(cachedVaccinations)
                }

                try {
                    val remoteVaccinations = remoteDataSource.getAllVaccinations()
                    remoteVaccinations.forEach { localDataSource.insertVaccination(it) }
                    emit(remoteVaccinations)
                } catch (e: Exception) {
                    if (cachedVaccinations.isEmpty()) throw e
                }
            }
            SyncStrategy.REMOTE_FIRST -> {
                try {
                    val remoteVaccinations = remoteDataSource.getAllVaccinations()
                    remoteVaccinations.forEach { localDataSource.insertVaccination(it) }
                    emit(remoteVaccinations)
                } catch (e: Exception) {
                    val cachedVaccinations = localDataSource.getAllVaccinations().first()
                    if (cachedVaccinations.isNotEmpty()) {
                        emit(cachedVaccinations)
                    } else {
                        throw e
                    }
                }
            }
            SyncStrategy.CACHE_ONLY -> {
                localDataSource.getAllVaccinations().collect { emit(it) }
            }
            SyncStrategy.REMOTE_ONLY -> {
                val remoteVaccinations = remoteDataSource.getAllVaccinations()
                emit(remoteVaccinations)
            }
        }
    }

    override fun getVaccinationById(id: String): Flow<Vaccination?> = flow {
        if (remoteDataSource == null) {
            localDataSource.getVaccinationById(id).collect { emit(it) }
            return@flow
        }

        when (syncStrategy) {
            SyncStrategy.CACHE_FIRST -> {
                val cachedVaccination = localDataSource.getVaccinationById(id).first()
                if (cachedVaccination != null) {
                    emit(cachedVaccination)
                }

                try {
                    val remoteVaccination = remoteDataSource.getVaccinationById(id)
                    if (remoteVaccination != null) {
                        localDataSource.insertVaccination(remoteVaccination)
                        emit(remoteVaccination)
                    }
                } catch (e: Exception) {
                    if (cachedVaccination == null) throw e
                }
            }
            SyncStrategy.REMOTE_FIRST -> {
                try {
                    val remoteVaccination = remoteDataSource.getVaccinationById(id)
                    if (remoteVaccination != null) {
                        localDataSource.insertVaccination(remoteVaccination)
                        emit(remoteVaccination)
                    } else {
                        emit(null)
                    }
                } catch (e: Exception) {
                    val cachedVaccination = localDataSource.getVaccinationById(id).first()
                    emit(cachedVaccination)
                }
            }
            SyncStrategy.CACHE_ONLY -> {
                localDataSource.getVaccinationById(id).collect { emit(it) }
            }
            SyncStrategy.REMOTE_ONLY -> {
                val remoteVaccination = remoteDataSource.getVaccinationById(id)
                emit(remoteVaccination)
            }
        }
    }

    override fun getVaccinationsByPetId(petId: String): Flow<List<Vaccination>> = flow {
        if (remoteDataSource == null) {
            localDataSource.getVaccinationsByPetId(petId).collect { emit(it) }
            return@flow
        }

        try {
            val remoteVaccinations = remoteDataSource.getVaccinationsByPetId(petId)
            remoteVaccinations.forEach { localDataSource.insertVaccination(it) }
            emit(remoteVaccinations)
        } catch (e: Exception) {
            localDataSource.getVaccinationsByPetId(petId).collect { emit(it) }
        }
    }

    override fun filterVaccinations(options: VaccinationFilterOptions): Flow<List<Vaccination>> {
        return localDataSource.getAllVaccinations().map { vaccinations ->
            vaccinations.filter { vaccination ->
                val petMatch = options.petId?.let { vaccination.petId == it } ?: true
                val typeMatch = options.vaccineType?.let { vaccination.vaccineType == it } ?: true
                val statusMatch = options.status?.let { vaccination.status == it } ?: true
                val searchMatch = if (options.searchQuery.isNotBlank()) {
                    vaccination.vaccineName.contains(options.searchQuery, ignoreCase = true) ||
                    vaccination.petName.contains(options.searchQuery, ignoreCase = true) ||
                    vaccination.veterinarianName.contains(options.searchQuery, ignoreCase = true)
                } else true

                petMatch && typeMatch && statusMatch && searchMatch
            }
        }
    }

    override fun getUpcomingVaccinations(days: Int): Flow<List<Vaccination>> = flow {
        if (remoteDataSource == null) {
            localDataSource.getAllVaccinations().collect { emit(it) }
            return@flow
        }

        try {
            val upcomingVaccinations = remoteDataSource.getUpcomingVaccinations(days)
            emit(upcomingVaccinations)
        } catch (e: Exception) {
            localDataSource.getAllVaccinations().collect { emit(it) }
        }
    }

    override fun getOverdueVaccinations(): Flow<List<Vaccination>> = flow {
        if (remoteDataSource == null) {
            localDataSource.getAllVaccinations().collect { emit(it) }
            return@flow
        }

        try {
            val overdueVaccinations = remoteDataSource.getOverdueVaccinations()
            emit(overdueVaccinations)
        } catch (e: Exception) {
            localDataSource.getAllVaccinations().collect { emit(it) }
        }
    }

    override suspend fun addVaccination(vaccination: Vaccination): Result<Vaccination> {
        if (remoteDataSource == null) {
            return try {
                val newVaccination = vaccination.copy(
                    id = generateId(),
                    createdAt = getCurrentTimestamp(),
                    updatedAt = getCurrentTimestamp()
                )
                localDataSource.insertVaccination(newVaccination)
                Result.success(newVaccination)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        return try {
            val createdVaccination = remoteDataSource.createVaccination(vaccination)
            localDataSource.insertVaccination(createdVaccination)
            Result.success(createdVaccination)
        } catch (e: Exception) {
            localDataSource.insertVaccination(vaccination)
            Result.failure(e)
        }
    }

    override suspend fun updateVaccination(vaccination: Vaccination): Result<Vaccination> {
        if (remoteDataSource == null) {
            return try {
                val updatedVaccination = vaccination.copy(updatedAt = getCurrentTimestamp())
                localDataSource.updateVaccination(updatedVaccination)
                Result.success(updatedVaccination)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        return try {
            val updatedVaccination = remoteDataSource.updateVaccination(vaccination)
            localDataSource.updateVaccination(updatedVaccination)
            Result.success(updatedVaccination)
        } catch (e: Exception) {
            localDataSource.updateVaccination(vaccination)
            Result.failure(e)
        }
    }

    override suspend fun deleteVaccination(id: String): Result<Unit> {
        if (remoteDataSource == null) {
            return try {
                localDataSource.deleteVaccination(id)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        return try {
            remoteDataSource.deleteVaccination(id)
            localDataSource.deleteVaccination(id)
            Result.success(Unit)
        } catch (e: Exception) {
            localDataSource.deleteVaccination(id)
            Result.failure(e)
        }
    }

    override suspend fun markAsApplied(id: String, observations: String): Result<Vaccination> {
        if (remoteDataSource == null) {
            return Result.failure(Exception("Operação não suportada offline"))
        }

        return try {
            val updatedVaccination = remoteDataSource.markAsApplied(
                id = id,
                observations = observations,
                sideEffects = "",
                applicationDate = getCurrentTimestamp()
            )
            localDataSource.updateVaccination(updatedVaccination)
            Result.success(updatedVaccination)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun scheduleNextDose(id: String, nextDoseDate: String): Result<Vaccination> {
        if (remoteDataSource == null) {
            return Result.failure(Exception("Operação não suportada offline"))
        }

        return try {
            val updatedVaccination = remoteDataSource.scheduleNextDose(id, nextDoseDate)
            localDataSource.updateVaccination(updatedVaccination)
            Result.success(updatedVaccination)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateId(): String = currentTimeMs().toString()

    private fun getCurrentTimestamp(): String = currentTimeMs().toString()
}

enum class SyncStrategy {
    CACHE_FIRST,
    REMOTE_FIRST,
    CACHE_ONLY,
    REMOTE_ONLY
}
