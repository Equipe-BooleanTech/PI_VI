package edu.fatec.petwise.features.pharmacies.domain.usecases

import edu.fatec.petwise.features.pharmacies.domain.models.Pharmacy
import edu.fatec.petwise.features.pharmacies.domain.models.PharmacyFilterOptions
import edu.fatec.petwise.features.pharmacies.domain.repository.PharmacyRepository
import kotlinx.coroutines.flow.Flow

class GetAllPharmaciesUseCase(
    private val pharmacyRepository: PharmacyRepository
) {
    operator fun invoke(): Flow<List<Pharmacy>> {
        return pharmacyRepository.getAllPharmacies()
    }
}

class GetPharmacyByIdUseCase(
    private val pharmacyRepository: PharmacyRepository
) {
    operator fun invoke(id: String): Flow<Pharmacy?> {
        return pharmacyRepository.getPharmacyById(id)
    }
}

class FilterPharmaciesUseCase(
    private val pharmacyRepository: PharmacyRepository
) {
    operator fun invoke(options: PharmacyFilterOptions): Flow<List<Pharmacy>> {
        return pharmacyRepository.filterPharmacies(options)
    }
}

class GetVerifiedPharmaciesUseCase(
    private val pharmacyRepository: PharmacyRepository
) {
    operator fun invoke(): Flow<List<Pharmacy>> {
        return pharmacyRepository.getVerifiedPharmacies()
    }
}

data class PharmacyUseCases(
    val getAllPharmacies: GetAllPharmaciesUseCase,
    val getPharmacyById: GetPharmacyByIdUseCase,
    val filterPharmacies: FilterPharmaciesUseCase,
    val getVerifiedPharmacies: GetVerifiedPharmaciesUseCase
)
