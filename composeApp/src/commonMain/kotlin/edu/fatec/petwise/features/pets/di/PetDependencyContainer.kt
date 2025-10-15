package edu.fatec.petwise.features.pets.di

import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.pets.data.datasource.RemotePetDataSourceImpl
import edu.fatec.petwise.features.pets.data.repository.PetRepositoryImpl
import edu.fatec.petwise.features.pets.domain.repository.PetRepository
import edu.fatec.petwise.features.pets.domain.usecases.*
import edu.fatec.petwise.features.pets.presentation.viewmodel.AddPetViewModel
import edu.fatec.petwise.features.pets.presentation.viewmodel.PetsViewModel
import edu.fatec.petwise.features.pets.presentation.viewmodel.UpdatePetViewModel

object PetDependencyContainer {

    private val remoteDataSource: RemotePetDataSourceImpl by lazy {
        RemotePetDataSourceImpl(NetworkModule.petApiService, NetworkModule.authApiService)
    }

    private val repository: PetRepository by lazy {
        PetRepositoryImpl(remoteDataSource)
    }

    private val getPetsUseCase: GetPetsUseCase by lazy {
        GetPetsUseCase(repository)
    }

    private val getPetByIdUseCase: GetPetByIdUseCase by lazy {
        GetPetByIdUseCase(repository)
    }

    private val addPetUseCase: AddPetUseCase by lazy {
        AddPetUseCase(repository)
    }

    private val updatePetUseCase: UpdatePetUseCase by lazy {
        UpdatePetUseCase(repository)
    }

    private val deletePetUseCase: DeletePetUseCase by lazy {
        DeletePetUseCase(repository)
    }

    private val toggleFavoriteUseCase: ToggleFavoriteUseCase by lazy {
        ToggleFavoriteUseCase(repository)
    }

    private val updateHealthStatusUseCase: UpdateHealthStatusUseCase by lazy {
        UpdateHealthStatusUseCase(repository)
    }

    private val petsViewModel: PetsViewModel by lazy {
        PetsViewModel(
            getPetsUseCase = getPetsUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            updateHealthStatusUseCase = updateHealthStatusUseCase,
            deletePetUseCase = deletePetUseCase
        )
    }

    private val addPetViewModel: AddPetViewModel by lazy {
        AddPetViewModel(
            addPetUseCase = addPetUseCase
        )
    }

    private val updatePetViewModel: UpdatePetViewModel by lazy {
        UpdatePetViewModel(
            updatePetUseCase = updatePetUseCase
        )
    }

    fun providePetsViewModel(): PetsViewModel = petsViewModel

    fun provideAddPetViewModel(): AddPetViewModel = addPetViewModel
    
    fun provideUpdatePetViewModel(): UpdatePetViewModel = updatePetViewModel


}