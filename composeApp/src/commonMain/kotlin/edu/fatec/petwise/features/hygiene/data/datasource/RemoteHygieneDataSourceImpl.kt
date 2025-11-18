package edu.fatec.petwise.features.hygiene.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.HygieneApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.hygiene.domain.models.HygieneProduct

class RemoteHygieneDataSourceImpl(
    private val hygieneApiService: HygieneApiService
) : RemoteHygieneDataSource {

    override suspend fun getAllHygieneProducts(): List<HygieneProduct> {
        return when (val result = hygieneApiService.getAllHygieneProducts(1, 1000)) {
            is NetworkResult.Success -> result.data.map { it.toHygieneProduct() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getHygieneProductById(id: String): HygieneProduct? {
        return when (val result = hygieneApiService.getHygieneProductById(id)) {
            is NetworkResult.Success -> result.data.toHygieneProduct()
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                null
            }
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun createHygieneProduct(product: HygieneProduct): HygieneProduct {
        val request = CreateHygieneRequest(
            name = product.name,
            brand = product.brand,
            category = product.category,
            description = product.description,
            price = product.price,
            stock = product.stock,
            unit = product.unit,
            expiryDate = product.expiryDate,
            imageUrl = product.imageUrl,
            active = product.active
        )
        return when (val result = hygieneApiService.createHygieneProduct(request)) {
            is NetworkResult.Success -> result.data.toHygieneProduct()
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun updateHygieneProduct(product: HygieneProduct): HygieneProduct {
        val request = UpdateHygieneRequest(
            name = product.name,
            brand = product.brand,
            category = product.category,
            description = product.description,
            price = product.price,
            stock = product.stock,
            unit = product.unit,
            expiryDate = product.expiryDate,
            imageUrl = product.imageUrl,
            active = product.active
        )
        return when (val result = hygieneApiService.updateHygieneProduct(product.id, request)) {
            is NetworkResult.Success -> result.data.toHygieneProduct()
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun deleteHygieneProduct(id: String) {
        when (val result = hygieneApiService.deleteHygieneProduct(id)) {
            is NetworkResult.Success -> Unit
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun searchHygieneProducts(query: String): List<HygieneProduct> {
        return when (val result = hygieneApiService.searchHygieneProducts(query)) {
            is NetworkResult.Success -> result.data.map { it.toHygieneProduct() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getHygieneProductsByCategory(category: String): List<HygieneProduct> {
        return when (val result = hygieneApiService.getHygieneProductsByCategory(category)) {
            is NetworkResult.Success -> result.data.map { it.toHygieneProduct() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }
}
