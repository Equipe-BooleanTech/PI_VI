package edu.fatec.petwise.features.hygiene.di

import edu.fatec.petwise.features.hygiene.data.datasource.RemoteHygieneDataSource
import edu.fatec.petwise.features.hygiene.data.datasource.RemoteHygieneDataSourceImpl
import edu.fatec.petwise.features.hygiene.data.repository.HygieneRepositoryImpl
import edu.fatec.petwise.features.hygiene.domain.repository.HygieneRepository
import edu.fatec.petwise.features.hygiene.domain.usecases.*

object HygieneDependencyContainer {
    
    private val remoteDataSource: RemoteHygieneDataSource by lazy {
        RemoteHygieneDataSourceImpl()
    }

    private val repository: HygieneRepository by lazy {
        HygieneRepositoryImpl(remoteDataSource)
    }

    val getHygieneProductsUseCase: GetHygieneProductsUseCase by lazy {
        GetHygieneProductsUseCase(repository)
    }

    val getHygieneProductByIdUseCase: GetHygieneProductByIdUseCase by lazy {
        GetHygieneProductByIdUseCase(repository)
    }

    val addHygieneProductUseCase: AddHygieneProductUseCase by lazy {
        AddHygieneProductUseCase(repository)
    }

    val updateHygieneProductUseCase: UpdateHygieneProductUseCase by lazy {
        UpdateHygieneProductUseCase(repository)
    }

    val deleteHygieneProductUseCase: DeleteHygieneProductUseCase by lazy {
        DeleteHygieneProductUseCase(repository)
    }
}
