package edu.fatec.petwise.features.labs.di

import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.labs.data.datasource.RemoteLabDataSource
import edu.fatec.petwise.features.labs.data.datasource.RemoteLabDataSourceImpl
import edu.fatec.petwise.features.labs.data.repository.LabRepositoryImpl
import edu.fatec.petwise.features.labs.domain.repository.LabRepository
import edu.fatec.petwise.features.labs.domain.usecases.*

object LabDependencyContainer {
    
    private val remoteDataSource: RemoteLabDataSource by lazy {
        RemoteLabDataSourceImpl(NetworkModule.labApiService)
    }

    private val repository: LabRepository by lazy {
        LabRepositoryImpl(remoteDataSource)
    }

    val getLabsUseCase: GetLabsUseCase by lazy {
        GetLabsUseCase(repository)
    }

    val getLabByIdUseCase: GetLabByIdUseCase by lazy {
        GetLabByIdUseCase(repository)
    }

    val addLabUseCase: AddLabUseCase by lazy {
        AddLabUseCase(repository)
    }

    val updateLabUseCase: UpdateLabUseCase by lazy {
        UpdateLabUseCase(repository)
    }

    val deleteLabUseCase: DeleteLabUseCase by lazy {
        DeleteLabUseCase(repository)
    }
}
