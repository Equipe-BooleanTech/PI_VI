package edu.fatec.petwise.features.hygiene.data.datasource

import edu.fatec.petwise.features.hygiene.domain.models.HygieneProduct

interface RemoteHygieneDataSource {
    suspend fun getAllHygieneProducts(): List<HygieneProduct>
    suspend fun getHygieneProductById(id: String): HygieneProduct?
    suspend fun createHygieneProduct(product: HygieneProduct): HygieneProduct
    suspend fun updateHygieneProduct(product: HygieneProduct): HygieneProduct
    suspend fun deleteHygieneProduct(id: String)
    suspend fun searchHygieneProducts(query: String): List<HygieneProduct>
    suspend fun getHygieneProductsByCategory(category: String): List<HygieneProduct>
}
