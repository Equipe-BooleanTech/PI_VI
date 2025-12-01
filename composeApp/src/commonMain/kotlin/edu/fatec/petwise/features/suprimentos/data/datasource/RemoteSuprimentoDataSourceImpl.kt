package edu.fatec.petwise.features.suprimentos.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.SuprimentoApiService
import edu.fatec.petwise.core.network.dto.toDomain
import edu.fatec.petwise.core.network.dto.toDto
import edu.fatec.petwise.features.suprimentos.domain.models.*


class RemoteSuprimentoDataSourceImpl(
    private val apiService: SuprimentoApiService
) : SuprimentoDataSource {

    override suspend fun getAllSuprimentos(): NetworkResult<List<Suprimento>> {
        return when (val result = apiService.getAllSuprimentos()) {
            is NetworkResult.Success -> NetworkResult.Success(
                data = result.data.map { it.toDomain() },
                metadata = result.metadata
            )
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun getSuprimentoById(id: String): NetworkResult<Suprimento> {
        return when (val result = apiService.getSuprimentoById(id)) {
            is NetworkResult.Success -> NetworkResult.Success(
                data = result.data.toDomain(),
                metadata = result.metadata
            )
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun getSuprimentosByPetId(petId: String): NetworkResult<List<Suprimento>> {
        return when (val result = apiService.getSuprimentosByPetId(petId)) {
            is NetworkResult.Success -> NetworkResult.Success(
                data = result.data.map { it.toDomain() },
                metadata = result.metadata
            )
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun getSuprimentosByCategory(category: SuprimentCategory): NetworkResult<List<Suprimento>> {
        return when (val result = apiService.getSuprimentosByCategory(category.name)) {
            is NetworkResult.Success -> NetworkResult.Success(
                data = result.data.map { it.toDomain() },
                metadata = result.metadata
            )
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun searchSuprimentos(criteria: SuprimentoSearchCriteria): NetworkResult<List<Suprimento>> {
        return when (val result = apiService.searchSuprimentos(criteria.query)) {
            is NetworkResult.Success -> NetworkResult.Success(
                data = result.data.map { it.toDomain() },
                metadata = result.metadata
            )
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun filterSuprimentos(options: SuprimentoFilterOptions): NetworkResult<List<Suprimento>> {
        return when (val result = apiService.filterSuprimentos(options)) {
            is NetworkResult.Success -> NetworkResult.Success(
                data = result.data.map { it.toDomain() },
                metadata = result.metadata
            )
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun addSuprimento(suprimento: Suprimento): NetworkResult<Suprimento> {
        return when (val result = apiService.addSuprimento(suprimento.toDto())) {
            is NetworkResult.Success -> NetworkResult.Success(
                data = result.data.toDomain(),
                metadata = result.metadata
            )
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun updateSuprimento(suprimento: Suprimento): NetworkResult<Suprimento> {
        return when (val result = apiService.updateSuprimento(suprimento.id, suprimento.toDto())) {
            is NetworkResult.Success -> NetworkResult.Success(
                data = result.data.toDomain(),
                metadata = result.metadata
            )
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun deleteSuprimento(id: String): NetworkResult<Unit> {
        return apiService.deleteSuprimento(id)
    }

    override suspend fun getRecentSuprimentos(limit: Int): NetworkResult<List<Suprimento>> {
        return when (val result = apiService.getRecentSuprimentos(limit)) {
            is NetworkResult.Success -> NetworkResult.Success(
                data = result.data.map { it.toDomain() },
                metadata = result.metadata
            )
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun getSuprimentosByPriceRange(
        minPrice: Float,
        maxPrice: Float
    ): NetworkResult<List<Suprimento>> {
        return when (val result = apiService.getSuprimentosByPriceRange(minPrice, maxPrice)) {
            is NetworkResult.Success -> NetworkResult.Success(
                data = result.data.map { it.toDomain() },
                metadata = result.metadata
            )
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }

    override suspend fun getSuprimentosByShop(shopName: String): NetworkResult<List<Suprimento>> {
        return when (val result = apiService.getSuprimentosByShop(shopName)) {
            is NetworkResult.Success -> NetworkResult.Success(
                data = result.data.map { it.toDomain() },
                metadata = result.metadata
            )
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}