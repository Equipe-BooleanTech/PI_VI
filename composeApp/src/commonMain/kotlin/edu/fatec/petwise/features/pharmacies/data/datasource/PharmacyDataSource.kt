package edu.fatec.petwise.features.pharmacies.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.features.pharmacies.domain.models.Pharmacy

interface PharmacyDataSource {
    suspend fun getAllPharmacies(): NetworkResult<List<Pharmacy>>
    
    suspend fun getPharmacyById(id: String): NetworkResult<Pharmacy?>
}
