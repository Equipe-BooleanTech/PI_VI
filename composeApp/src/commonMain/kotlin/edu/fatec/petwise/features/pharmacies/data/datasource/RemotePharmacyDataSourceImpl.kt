package edu.fatec.petwise.features.pharmacies.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.PharmacyApiService
import edu.fatec.petwise.core.network.dto.toPharmacy
import edu.fatec.petwise.features.pharmacies.domain.models.Pharmacy

class RemotePharmacyDataSourceImpl(
    private val apiService: PharmacyApiService
) : PharmacyDataSource {

    override suspend fun getAllPharmacies(): NetworkResult<List<Pharmacy>> {
        return when (val result = apiService.getAllPharmacies()) {
            is NetworkResult.Success -> {
                val pharmacies = result.data.pharmacies.map { it.toPharmacy() }
                NetworkResult.Success(pharmacies)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun getPharmacyById(id: String): NetworkResult<Pharmacy?> {
        return when (val result = apiService.getPharmacyById(id)) {
            is NetworkResult.Success -> {
                val pharmacy = result.data.toPharmacy()
                NetworkResult.Success(pharmacy)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}
