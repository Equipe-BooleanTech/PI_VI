package edu.fatec.petwise.features.pharmacies.domain.repository

import edu.fatec.petwise.features.pharmacies.domain.models.Pharmacy
import edu.fatec.petwise.features.pharmacies.domain.models.PharmacyFilterOptions
import kotlinx.coroutines.flow.Flow

interface PharmacyRepository {
    fun getAllPharmacies(): Flow<List<Pharmacy>>

    fun getPharmacyById(id: String): Flow<Pharmacy?>

    fun filterPharmacies(options: PharmacyFilterOptions): Flow<List<Pharmacy>>

    fun getVerifiedPharmacies(): Flow<List<Pharmacy>>
}
