package edu.fatec.petwise.features.hygiene.domain.usecases

import edu.fatec.petwise.features.hygiene.domain.models.HygieneProduct
import edu.fatec.petwise.features.hygiene.domain.repository.HygieneRepository
import kotlinx.coroutines.flow.Flow

class GetHygieneProductsUseCase(
    private val repository: HygieneRepository
) {
    operator fun invoke(): Flow<List<HygieneProduct>> = repository.getAllHygieneProducts()

    fun searchProducts(query: String): Flow<List<HygieneProduct>> = repository.searchHygieneProducts(query)

    fun getProductsByCategory(category: String): Flow<List<HygieneProduct>> = repository.getHygieneProductsByCategory(category)
}

class GetHygieneProductByIdUseCase(
    private val repository: HygieneRepository
) {
    operator fun invoke(id: String): Flow<HygieneProduct?> = repository.getHygieneProductById(id)
}

class AddHygieneProductUseCase(
    private val repository: HygieneRepository
) {
    suspend operator fun invoke(product: HygieneProduct): Result<HygieneProduct> {
        return if (validateProduct(product)) {
            repository.addHygieneProduct(product)
        } else {
            Result.failure(IllegalArgumentException("Hygiene product data is invalid"))
        }
    }

    private fun validateProduct(product: HygieneProduct): Boolean {
        return product.name.isNotBlank() &&
               product.brand.isNotBlank() &&
               product.category.isNotBlank() &&
               product.price > 0 &&
               product.stock >= 0
    }
}

class UpdateHygieneProductUseCase(
    private val repository: HygieneRepository
) {
    suspend operator fun invoke(product: HygieneProduct): Result<HygieneProduct> = repository.updateHygieneProduct(product)
}

class DeleteHygieneProductUseCase(
    private val repository: HygieneRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteHygieneProduct(id)
}
