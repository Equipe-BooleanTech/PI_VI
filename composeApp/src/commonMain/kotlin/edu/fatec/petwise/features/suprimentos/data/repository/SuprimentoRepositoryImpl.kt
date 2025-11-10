package edu.fatec.petwise.features.suprimentos.data.repository

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.features.suprimentos.data.datasource.SuprimentoDataSource
import edu.fatec.petwise.features.suprimentos.domain.models.*
import edu.fatec.petwise.features.suprimentos.domain.repository.SuprimentoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository implementation for Suprimento operations
 */
class SuprimentoRepositoryImpl(
    private val remoteDataSource: SuprimentoDataSource
) : SuprimentoRepository {

    override suspend fun getAllSuprimentos(): Flow<NetworkResult<List<Suprimento>>> = flow {
        emit(NetworkResult.Loading())
        emit(remoteDataSource.getAllSuprimentos())
    }

    override suspend fun getSuprimentoById(id: String): Flow<NetworkResult<Suprimento>> = flow {
        emit(NetworkResult.Loading())
        emit(remoteDataSource.getSuprimentoById(id))
    }

    override suspend fun getSuprimentosByPetId(petId: String): Flow<NetworkResult<List<Suprimento>>> = flow {
        emit(NetworkResult.Loading())
        emit(remoteDataSource.getSuprimentosByPetId(petId))
    }

    override suspend fun getSuprimentosByCategory(category: SuprimentCategory): Flow<NetworkResult<List<Suprimento>>> = flow {
        emit(NetworkResult.Loading())
        emit(remoteDataSource.getSuprimentosByCategory(category))
    }

    override suspend fun searchSuprimentos(criteria: SuprimentoSearchCriteria): Flow<NetworkResult<List<Suprimento>>> = flow {
        emit(NetworkResult.Loading())
        emit(remoteDataSource.searchSuprimentos(criteria))
    }

    override suspend fun filterSuprimentos(options: SuprimentoFilterOptions): Flow<NetworkResult<List<Suprimento>>> = flow {
        emit(NetworkResult.Loading())
        emit(remoteDataSource.filterSuprimentos(options))
    }

    override suspend fun addSuprimento(suprimento: Suprimento): Flow<NetworkResult<Suprimento>> = flow {
        emit(NetworkResult.Loading())
        emit(remoteDataSource.addSuprimento(suprimento))
    }

    override suspend fun updateSuprimento(suprimento: Suprimento): Flow<NetworkResult<Suprimento>> = flow {
        emit(NetworkResult.Loading())
        emit(remoteDataSource.updateSuprimento(suprimento))
    }

    override suspend fun deleteSuprimento(id: String): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        emit(remoteDataSource.deleteSuprimento(id))
    }

    override suspend fun getRecentSuprimentos(limit: Int): Flow<NetworkResult<List<Suprimento>>> = flow {
        emit(NetworkResult.Loading())
        emit(remoteDataSource.getRecentSuprimentos(limit))
    }

    override suspend fun getSuprimentosByPriceRange(
        minPrice: Float,
        maxPrice: Float
    ): Flow<NetworkResult<List<Suprimento>>> = flow {
        emit(NetworkResult.Loading())
        emit(remoteDataSource.getSuprimentosByPriceRange(minPrice, maxPrice))
    }

    override suspend fun getSuprimentosByShop(shopName: String): Flow<NetworkResult<List<Suprimento>>> = flow {
        emit(NetworkResult.Loading())
        emit(remoteDataSource.getSuprimentosByShop(shopName))
    }
}