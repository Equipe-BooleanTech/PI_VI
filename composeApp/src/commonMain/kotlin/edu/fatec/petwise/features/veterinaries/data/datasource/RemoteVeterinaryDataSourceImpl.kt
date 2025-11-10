package edu.fatec.petwise.features.veterinaries.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.VeterinaryApiService
import edu.fatec.petwise.core.network.dto.toVeterinary
import edu.fatec.petwise.features.veterinaries.domain.models.Veterinary

class RemoteVeterinaryDataSourceImpl(
    private val veterinaryApiService: VeterinaryApiService
) : VeterinaryDataSource {

    override suspend fun getAllVeterinaries(): NetworkResult<List<Veterinary>> {
        return when (val result = veterinaryApiService.getAllVeterinaries()) {
            is NetworkResult.Success -> {
                val veterinaries = result.data.veterinaries.map { it.toVeterinary() }
                NetworkResult.Success(veterinaries)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun getVeterinaryById(id: String): NetworkResult<Veterinary?> {
        return when (val result = veterinaryApiService.getVeterinaryById(id)) {
            is NetworkResult.Success -> {
                val veterinary = result.data.toVeterinary()
                NetworkResult.Success(veterinary)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}