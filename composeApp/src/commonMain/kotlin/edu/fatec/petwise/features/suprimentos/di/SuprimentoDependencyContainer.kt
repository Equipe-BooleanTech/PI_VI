package edu.fatec.petwise.features.suprimentos.di

import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.suprimentos.data.datasource.RemoteSuprimentoDataSourceImpl
import edu.fatec.petwise.features.suprimentos.data.repository.SuprimentoRepositoryImpl
import edu.fatec.petwise.features.suprimentos.domain.repository.SuprimentoRepository
import edu.fatec.petwise.features.suprimentos.domain.usecases.*
import edu.fatec.petwise.features.suprimentos.presentation.viewmodel.*
import kotlinx.coroutines.cancel

object SuprimentoDependencyContainer {

    private var remoteDataSource: RemoteSuprimentoDataSourceImpl? = null
    private var repository: SuprimentoRepository? = null
    private var suprimentosViewModel: SuprimentosViewModel? = null
    private var addSuprimentoViewModel: AddSuprimentoViewModel? = null
    private var updateSuprimentoViewModel: UpdateSuprimentoViewModel? = null

    private fun getRemoteDataSource(): RemoteSuprimentoDataSourceImpl {
        val existing = remoteDataSource
        if (existing != null) return existing
        val created = RemoteSuprimentoDataSourceImpl(NetworkModule.suprimentoApiService)
        remoteDataSource = created
        return created
    }

    private fun getRepository(): SuprimentoRepository {
        val existing = repository
        if (existing != null) return existing
        val created = SuprimentoRepositoryImpl(getRemoteDataSource())
        repository = created
        return created
    }

    private fun buildSuprimentosViewModel(): SuprimentosViewModel {
        val repo = getRepository()
        return SuprimentosViewModel(
            getAllSuprimentosUseCase = GetAllSuprimentosUseCase(repo),
            getSuprimentosByPetUseCase = GetSuprimentosByPetUseCase(repo),
            getSuprimentosByCategoryUseCase = GetSuprimentosByCategoryUseCase(repo),
            searchSuprimentosUseCase = SearchSuprimentosUseCase(repo),
            filterSuprimentosUseCase = FilterSuprimentosUseCase(repo),
            deleteSuprimentoUseCase = DeleteSuprimentoUseCase(repo),
            getRecentSuprimentosUseCase = GetRecentSuprimentosUseCase(repo)
        )
    }

    private fun buildAddSuprimentoViewModel(): AddSuprimentoViewModel {
        val repo = getRepository()
        return AddSuprimentoViewModel(
            addSuprimentoUseCase = AddSuprimentoUseCase(repo)
        )
    }

    private fun buildUpdateSuprimentoViewModel(): UpdateSuprimentoViewModel {
        val repo = getRepository()
        return UpdateSuprimentoViewModel(
            updateSuprimentoUseCase = UpdateSuprimentoUseCase(repo),
            getSuprimentoByIdUseCase = GetSuprimentoByIdUseCase(repo)
        )
    }

    fun provideSuprimentosViewModel(): SuprimentosViewModel {
        val existing = suprimentosViewModel
        if (existing != null) return existing
        val created = buildSuprimentosViewModel()
        suprimentosViewModel = created
        return created
    }

    fun provideAddSuprimentoViewModel(): AddSuprimentoViewModel {
        val existing = addSuprimentoViewModel
        if (existing != null) return existing
        val created = buildAddSuprimentoViewModel()
        addSuprimentoViewModel = created
        return created
    }

    fun provideUpdateSuprimentoViewModel(): UpdateSuprimentoViewModel {
        val existing = updateSuprimentoViewModel
        if (existing != null) return existing
        val created = buildUpdateSuprimentoViewModel()
        updateSuprimentoViewModel = created
        return created
    }

    fun provideSuprimentoUseCases(): SuprimentoUseCases {
        val repo = getRepository()
        return SuprimentoUseCases(
            getAllSuprimentos = GetAllSuprimentosUseCase(repo),
            getSuprimentoById = GetSuprimentoByIdUseCase(repo),
            getSuprimentosByPet = GetSuprimentosByPetUseCase(repo),
            getSuprimentosByCategory = GetSuprimentosByCategoryUseCase(repo),
            searchSuprimentos = SearchSuprimentosUseCase(repo),
            filterSuprimentos = FilterSuprimentosUseCase(repo),
            addSuprimento = AddSuprimentoUseCase(repo),
            updateSuprimento = UpdateSuprimentoUseCase(repo),
            deleteSuprimento = DeleteSuprimentoUseCase(repo),
            getRecentSuprimentos = GetRecentSuprimentosUseCase(repo),
            getSuprimentosByPriceRange = GetSuprimentosByPriceRangeUseCase(repo),
            getSuprimentosByShop = GetSuprimentosByShopUseCase(repo)
        )
    }

    fun reset() {
        suprimentosViewModel?.viewModelScope?.cancel()
        addSuprimentoViewModel?.viewModelScope?.cancel()
        updateSuprimentoViewModel?.viewModelScope?.cancel()

        suprimentosViewModel = null
        addSuprimentoViewModel = null
        updateSuprimentoViewModel = null

        repository = null
        remoteDataSource = null
    }
}