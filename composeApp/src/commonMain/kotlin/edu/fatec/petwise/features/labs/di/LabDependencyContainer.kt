package edu.fatec.petwise.features.labs.di

import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.labs.data.datasource.RemoteLabDataSource
import edu.fatec.petwise.features.labs.data.datasource.RemoteLabDataSourceImpl
import edu.fatec.petwise.features.labs.data.repository.LabRepositoryImpl
import edu.fatec.petwise.features.labs.domain.repository.LabRepository
import edu.fatec.petwise.features.labs.domain.usecases.*
import edu.fatec.petwise.features.labs.presentation.viewmodel.*

object LabDependencyContainer {

    private val remoteDataSource: RemoteLabDataSource by lazy {
        RemoteLabDataSourceImpl(NetworkModule.labApiService)
    }

    private val repository: LabRepository by lazy {
        LabRepositoryImpl(remoteDataSource)
    }

    val getLabResultsUseCase: GetLabResultsUseCase by lazy {
        GetLabResultsUseCase(repository)
    }

    val getLabResultByIdUseCase: GetLabResultByIdUseCase by lazy {
        GetLabResultByIdUseCase(repository)
    }

    val addLabResultUseCase: AddLabResultUseCase by lazy {
        AddLabResultUseCase(repository)
    }

    val updateLabResultUseCase: UpdateLabResultUseCase by lazy {
        UpdateLabResultUseCase(repository)
    }

    val deleteLabResultUseCase: DeleteLabResultUseCase by lazy {
        DeleteLabResultUseCase(repository)
    }

    // ViewModels
    val labsViewModel: LabsViewModel by lazy {
        LabsViewModel(getLabResultsUseCase, deleteLabResultUseCase)
    }

    val addLabResultViewModel: AddLabResultViewModel by lazy {
        AddLabResultViewModel(addLabResultUseCase)
    }

    val updateLabResultViewModel: UpdateLabResultViewModel by lazy {
        UpdateLabResultViewModel(updateLabResultUseCase)
    }
}
