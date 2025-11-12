package edu.fatec.petwise.features.toys.di

import edu.fatec.petwise.features.toys.data.datasource.RemoteToyDataSource
import edu.fatec.petwise.features.toys.data.datasource.RemoteToyDataSourceImpl
import edu.fatec.petwise.features.toys.data.repository.ToyRepositoryImpl
import edu.fatec.petwise.features.toys.domain.repository.ToyRepository
import edu.fatec.petwise.features.toys.domain.usecases.*

object ToyDependencyContainer {
    
    private val remoteDataSource: RemoteToyDataSource by lazy {
        RemoteToyDataSourceImpl()
    }

    private val repository: ToyRepository by lazy {
        ToyRepositoryImpl(remoteDataSource)
    }

    val getToysUseCase: GetToysUseCase by lazy {
        GetToysUseCase(repository)
    }

    val getToyByIdUseCase: GetToyByIdUseCase by lazy {
        GetToyByIdUseCase(repository)
    }

    val addToyUseCase: AddToyUseCase by lazy {
        AddToyUseCase(repository)
    }

    val updateToyUseCase: UpdateToyUseCase by lazy {
        UpdateToyUseCase(repository)
    }

    val deleteToyUseCase: DeleteToyUseCase by lazy {
        DeleteToyUseCase(repository)
    }
}
