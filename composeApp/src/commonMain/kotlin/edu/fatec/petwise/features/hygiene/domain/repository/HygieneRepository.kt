package edu.fatec.petwise.features.hygiene.domain.repository

import edu.fatec.petwise.features.hygiene.domain.models.HygieneProduct
import kotlinx.coroutines.flow.Flow

interface HygieneRepository {
    fun getAllHygieneProducts(): Flow<List<HygieneProduct>>
    fun getHygieneProductById(id: String): Flow<HygieneProduct?>
    fun searchHygieneProducts(query: String): Flow<List<HygieneProduct>>
    fun getHygieneProductsByCategory(category: String): Flow<List<HygieneProduct>>
    suspend fun addHygieneProduct(product: HygieneProduct): Result<HygieneProduct>
    suspend fun updateHygieneProduct(product: HygieneProduct): Result<HygieneProduct>
    suspend fun deleteHygieneProduct(id: String): Result<Unit>
}
