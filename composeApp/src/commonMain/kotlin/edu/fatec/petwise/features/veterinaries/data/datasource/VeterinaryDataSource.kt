package edu.fatec.petwise.features.veterinaries.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.features.veterinaries.domain.models.Veterinary

interface VeterinaryDataSource {
    suspend fun getAllVeterinaries(): NetworkResult<List<Veterinary>>
    suspend fun getVeterinaryById(id: String): NetworkResult<Veterinary?>
}