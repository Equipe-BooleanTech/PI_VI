package edu.fatec.petwise.features.veterinaries.domain.repository

import edu.fatec.petwise.features.veterinaries.domain.models.Veterinary
import edu.fatec.petwise.features.veterinaries.domain.models.VeterinaryFilterOptions
import kotlinx.coroutines.flow.Flow

interface VeterinaryRepository {
    fun getAllVeterinaries(): Flow<List<Veterinary>>

    fun getVeterinaryById(id: String): Flow<Veterinary?>

    fun searchVeterinaries(query: String): Flow<List<Veterinary>>

    fun filterVeterinaries(options: VeterinaryFilterOptions): Flow<List<Veterinary>>

    fun getVerifiedVeterinaries(): Flow<List<Veterinary>>
}