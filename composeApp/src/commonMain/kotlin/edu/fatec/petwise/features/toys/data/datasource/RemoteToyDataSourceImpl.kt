package edu.fatec.petwise.features.toys.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.ToyApiService
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.toys.domain.models.Toy

class RemoteToyDataSourceImpl(
    private val toyApiService: ToyApiService
) : RemoteToyDataSource {

    override suspend fun getAllToys(): List<Toy> {
        return when (val result = toyApiService.getAllToys()) {
            is NetworkResult.Success -> result.data.toys.map { it.toToy() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getToyById(id: String): Toy? {
        return when (val result = toyApiService.getToyById(id)) {
            is NetworkResult.Success -> result.data.toToy()
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                null
            }
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun createToy(toy: Toy): Toy {
        val request = CreateToyRequest(
            name = toy.name,
            brand = toy.brand,
            category = toy.category,
            description = toy.description,
            price = toy.price,
            stock = toy.stock,
            unit = toy.unit,
            material = toy.material,
            ageRecommendation = toy.ageRecommendation,
            imageUrl = toy.imageUrl,
            active = toy.active
        )
        return when (val result = toyApiService.createToy(request)) {
            is NetworkResult.Success -> result.data.toToy()
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun updateToy(toy: Toy): Toy {
        val request = UpdateToyRequest(
            name = toy.name,
            brand = toy.brand,
            category = toy.category,
            description = toy.description,
            price = toy.price,
            stock = toy.stock,
            unit = toy.unit,
            material = toy.material,
            ageRecommendation = toy.ageRecommendation,
            imageUrl = toy.imageUrl,
            active = toy.active
        )
        return when (val result = toyApiService.updateToy(toy.id, request)) {
            is NetworkResult.Success -> result.data.toToy()
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun deleteToy(id: String) {
        when (val result = toyApiService.deleteToy(id)) {
            is NetworkResult.Success -> Unit
            is NetworkResult.Error -> throw Exception(result.exception.message)
            is NetworkResult.Loading -> throw Exception("Request in progress")
        }
    }

    override suspend fun searchToys(query: String): List<Toy> {
        return when (val result = toyApiService.searchToys(query)) {
            is NetworkResult.Success -> result.data.map { it.toToy() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun getToysByCategory(category: String): List<Toy> {
        return when (val result = toyApiService.getToysByCategory(category)) {
            is NetworkResult.Success -> result.data.map { it.toToy() }
            is NetworkResult.Error -> {
                println("API Error: ${result.exception.message}")
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }
}
