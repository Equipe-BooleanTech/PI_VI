package edu.fatec.petwise.features.suprimentos.domain.usecases

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.features.suprimentos.domain.models.*
import edu.fatec.petwise.features.suprimentos.domain.repository.SuprimentoRepository
import kotlinx.coroutines.flow.Flow

data class SuprimentoUseCases(
    val getAllSuprimentos: GetAllSuprimentosUseCase,
    val getSuprimentoById: GetSuprimentoByIdUseCase,
    val getSuprimentosByPet: GetSuprimentosByPetUseCase,
    val getSuprimentosByCategory: GetSuprimentosByCategoryUseCase,
    val searchSuprimentos: SearchSuprimentosUseCase,
    val filterSuprimentos: FilterSuprimentosUseCase,
    val addSuprimento: AddSuprimentoUseCase,
    val updateSuprimento: UpdateSuprimentoUseCase,
    val deleteSuprimento: DeleteSuprimentoUseCase,
    val getRecentSuprimentos: GetRecentSuprimentosUseCase,
    val getSuprimentosByPriceRange: GetSuprimentosByPriceRangeUseCase,
    val getSuprimentosByShop: GetSuprimentosByShopUseCase
)

class GetAllSuprimentosUseCase(
    private val repository: SuprimentoRepository
) {
    suspend operator fun invoke(): Flow<NetworkResult<List<Suprimento>>> {
        return repository.getAllSuprimentos()
    }
}

class GetSuprimentoByIdUseCase(
    private val repository: SuprimentoRepository
) {
    suspend operator fun invoke(id: String): Flow<NetworkResult<Suprimento>> {
        return repository.getSuprimentoById(id)
    }
}

class GetSuprimentosByPetUseCase(
    private val repository: SuprimentoRepository
) {
    suspend operator fun invoke(petId: String): Flow<NetworkResult<List<Suprimento>>> {
        return repository.getSuprimentosByPetId(petId)
    }
}

class GetSuprimentosByCategoryUseCase(
    private val repository: SuprimentoRepository
) {
    suspend operator fun invoke(category: SuprimentCategory): Flow<NetworkResult<List<Suprimento>>> {
        return repository.getSuprimentosByCategory(category)
    }
}

class SearchSuprimentosUseCase(
    private val repository: SuprimentoRepository
) {
    suspend operator fun invoke(criteria: SuprimentoSearchCriteria): Flow<NetworkResult<List<Suprimento>>> {
        return repository.searchSuprimentos(criteria)
    }
}

class FilterSuprimentosUseCase(
    private val repository: SuprimentoRepository
) {
    suspend operator fun invoke(options: SuprimentoFilterOptions): Flow<NetworkResult<List<Suprimento>>> {
        return repository.filterSuprimentos(options)
    }
}

class AddSuprimentoUseCase(
    private val repository: SuprimentoRepository
) {
    suspend operator fun invoke(suprimento: Suprimento): Flow<NetworkResult<Suprimento>> {
        return repository.addSuprimento(suprimento)
    }
}

class UpdateSuprimentoUseCase(
    private val repository: SuprimentoRepository
) {
    suspend operator fun invoke(suprimento: Suprimento): Flow<NetworkResult<Suprimento>> {
        return repository.updateSuprimento(suprimento)
    }
}

class DeleteSuprimentoUseCase(
    private val repository: SuprimentoRepository
) {
    suspend operator fun invoke(id: String): Flow<NetworkResult<Unit>> {
        return repository.deleteSuprimento(id)
    }
}

class GetRecentSuprimentosUseCase(
    private val repository: SuprimentoRepository
) {
    suspend operator fun invoke(limit: Int = 10): Flow<NetworkResult<List<Suprimento>>> {
        return repository.getRecentSuprimentos(limit)
    }
}

class GetSuprimentosByPriceRangeUseCase(
    private val repository: SuprimentoRepository
) {
    suspend operator fun invoke(
        minPrice: Float,
        maxPrice: Float
    ): Flow<NetworkResult<List<Suprimento>>> {
        return repository.getSuprimentosByPriceRange(minPrice, maxPrice)
    }
}

class GetSuprimentosByShopUseCase(
    private val repository: SuprimentoRepository
) {
    suspend operator fun invoke(shopName: String): Flow<NetworkResult<List<Suprimento>>> {
        return repository.getSuprimentosByShop(shopName)
    }
}