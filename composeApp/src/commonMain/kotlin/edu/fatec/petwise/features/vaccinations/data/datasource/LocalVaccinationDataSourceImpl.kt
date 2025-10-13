package edu.fatec.petwise.features.vaccinations.data.datasource

import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class LocalVaccinationDataSourceImpl : LocalVaccinationDataSource {
    private val vaccinations = MutableStateFlow<List<Vaccination>>(emptyList())

    override fun getAllVaccinations(): Flow<List<Vaccination>> = vaccinations

    override fun getVaccinationById(id: String): Flow<Vaccination?> {
        return vaccinations.map { list -> list.firstOrNull { it.id == id } }
    }

    override fun getVaccinationsByPetId(petId: String): Flow<List<Vaccination>> {
        return vaccinations.map { list -> list.filter { it.petId == petId } }
    }

    override suspend fun insertVaccination(vaccination: Vaccination) {
        val current = vaccinations.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.id == vaccination.id }
        if (existingIndex != -1) {
            current[existingIndex] = vaccination
        } else {
            current.add(vaccination)
        }
        vaccinations.value = current
    }

    override suspend fun updateVaccination(vaccination: Vaccination) {
        val current = vaccinations.value.toMutableList()
        val index = current.indexOfFirst { it.id == vaccination.id }
        if (index != -1) {
            current[index] = vaccination
            vaccinations.value = current
        }
    }

    override suspend fun deleteVaccination(id: String) {
        vaccinations.value = vaccinations.value.filter { it.id != id }
    }

    override suspend fun deleteAllVaccinations() {
        vaccinations.value = emptyList()
    }
}
