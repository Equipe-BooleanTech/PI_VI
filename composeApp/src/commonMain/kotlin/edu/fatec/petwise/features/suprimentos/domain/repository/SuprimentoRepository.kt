package edu.fatec.petwise.features.suprimentos.domain.repository

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.features.suprimentos.domain.models.*
import kotlinx.coroutines.flow.Flow

interface SuprimentoRepository {

    suspend fun getAllSuprimentos(): Flow<NetworkResult<List<Suprimento>>>

    suspend fun getSuprimentoById(id: String): Flow<NetworkResult<Suprimento>>

    suspend fun getSuprimentosByPetId(petId: String): Flow<NetworkResult<List<Suprimento>>>

    suspend fun getSuprimentosByCategory(category: SuprimentCategory): Flow<NetworkResult<List<Suprimento>>>

    suspend fun searchSuprimentos(criteria: SuprimentoSearchCriteria): Flow<NetworkResult<List<Suprimento>>>

    suspend fun filterSuprimentos(options: SuprimentoFilterOptions): Flow<NetworkResult<List<Suprimento>>>

    suspend fun addSuprimento(suprimento: Suprimento): Flow<NetworkResult<Suprimento>>

    suspend fun updateSuprimento(suprimento: Suprimento): Flow<NetworkResult<Suprimento>>

    suspend fun deleteSuprimento(id: String): Flow<NetworkResult<Unit>>

    suspend fun getRecentSuprimentos(limit: Int = 10): Flow<NetworkResult<List<Suprimento>>>

    suspend fun getSuprimentosByPriceRange(
        minPrice: Float,
        maxPrice: Float
    ): Flow<NetworkResult<List<Suprimento>>>

    suspend fun getSuprimentosByShop(shopName: String): Flow<NetworkResult<List<Suprimento>>>
}