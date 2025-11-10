package edu.fatec.petwise.features.suprimentos.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.features.suprimentos.domain.models.*

interface SuprimentoDataSource {

    suspend fun getAllSuprimentos(): NetworkResult<List<Suprimento>>

    suspend fun getSuprimentoById(id: String): NetworkResult<Suprimento>

    suspend fun getSuprimentosByPetId(petId: String): NetworkResult<List<Suprimento>>

    suspend fun getSuprimentosByCategory(category: SuprimentCategory): NetworkResult<List<Suprimento>>

    suspend fun searchSuprimentos(criteria: SuprimentoSearchCriteria): NetworkResult<List<Suprimento>>

    suspend fun filterSuprimentos(options: SuprimentoFilterOptions): NetworkResult<List<Suprimento>>

    suspend fun addSuprimento(suprimento: Suprimento): NetworkResult<Suprimento>

    suspend fun updateSuprimento(suprimento: Suprimento): NetworkResult<Suprimento>

    suspend fun deleteSuprimento(id: String): NetworkResult<Unit>

    suspend fun getRecentSuprimentos(limit: Int): NetworkResult<List<Suprimento>>

    suspend fun getSuprimentosByPriceRange(
        minPrice: Float,
        maxPrice: Float
    ): NetworkResult<List<Suprimento>>

    suspend fun getSuprimentosByShop(shopName: String): NetworkResult<List<Suprimento>>
}