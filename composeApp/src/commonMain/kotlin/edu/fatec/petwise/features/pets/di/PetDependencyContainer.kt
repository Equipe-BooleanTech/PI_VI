package edu.fatec.petwise.features.pets.di

import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.pets.data.datasource.RemotePetDataSourceImpl
import edu.fatec.petwise.features.pets.data.repository.PetRepositoryImpl
import edu.fatec.petwise.features.pets.domain.repository.PetRepository
import edu.fatec.petwise.features.pets.domain.usecases.AddPetUseCase
import edu.fatec.petwise.features.pets.domain.usecases.DeletePetUseCase
import edu.fatec.petwise.features.pets.domain.usecases.GetPetsUseCase
import edu.fatec.petwise.features.pets.domain.usecases.ToggleFavoriteUseCase
import edu.fatec.petwise.features.pets.domain.usecases.UpdateHealthStatusUseCase
import edu.fatec.petwise.features.pets.domain.usecases.UpdatePetUseCase
import edu.fatec.petwise.features.pets.presentation.viewmodel.AddPetViewModel
import edu.fatec.petwise.features.pets.presentation.viewmodel.PetsViewModel
import edu.fatec.petwise.features.pets.presentation.viewmodel.UpdatePetViewModel
import kotlinx.coroutines.cancel

object PetDependencyContainer {

    @Volatile
    private var remoteDataSource: RemotePetDataSourceImpl? = null

    @Volatile
    private var repository: PetRepository? = null

    @Volatile
    private var petsViewModel: PetsViewModel? = null

    @Volatile
    private var addPetViewModel: AddPetViewModel? = null

    @Volatile
    private var updatePetViewModel: UpdatePetViewModel? = null

    private fun getRemoteDataSource(): RemotePetDataSourceImpl {
        val existing = remoteDataSource
        if (existing != null) return existing
        val created = RemotePetDataSourceImpl(
            NetworkModule.petApiService,
            NetworkModule.authApiService
        )
        remoteDataSource = created
        return created
    }

    private fun getRepository(): PetRepository {
        val existing = repository
        if (existing != null) return existing
        val created = PetRepositoryImpl(getRemoteDataSource())
        repository = created
        return created
    }

    private fun buildPetsViewModel(): PetsViewModel {
        val repo = getRepository()
        return PetsViewModel(
            getPetsUseCase = GetPetsUseCase(repo),
            toggleFavoriteUseCase = ToggleFavoriteUseCase(repo),
            updateHealthStatusUseCase = UpdateHealthStatusUseCase(repo),
            deletePetUseCase = DeletePetUseCase(repo)
        )
    }

    private fun buildAddPetViewModel(): AddPetViewModel {
        val repo = getRepository()
        return AddPetViewModel(
            addPetUseCase = AddPetUseCase(repo)
        )
    }

    private fun buildUpdatePetViewModel(): UpdatePetViewModel {
        val repo = getRepository()
        return UpdatePetViewModel(
            updatePetUseCase = UpdatePetUseCase(repo)
        )
    }

    fun providePetsViewModel(): PetsViewModel {
        val existing = petsViewModel
        if (existing != null) return existing
        val created = buildPetsViewModel()
        petsViewModel = created
        return created
    }

    fun provideAddPetViewModel(): AddPetViewModel {
        val existing = addPetViewModel
        if (existing != null) return existing
        val created = buildAddPetViewModel()
        addPetViewModel = created
        return created
    }

    fun provideUpdatePetViewModel(): UpdatePetViewModel {
        val existing = updatePetViewModel
        if (existing != null) return existing
        val created = buildUpdatePetViewModel()
        updatePetViewModel = created
        return created
    }

    fun reset() {
        petsViewModel?.viewModelScope?.cancel()
        addPetViewModel?.viewModelScope?.cancel()
        updatePetViewModel?.viewModelScope?.cancel()

        petsViewModel = null
        addPetViewModel = null
        updatePetViewModel = null

        repository = null
        remoteDataSource = null
    }
}