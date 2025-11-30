package edu.fatec.petwise.features.pettags.di

import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.pets.di.PetDependencyContainer
import edu.fatec.petwise.features.pettags.data.datasource.RemotePetTagDataSource
import edu.fatec.petwise.features.pettags.data.datasource.RemotePetTagDataSourceImpl
import edu.fatec.petwise.features.pettags.data.repository.PetTagRepositoryImpl
import edu.fatec.petwise.features.pettags.domain.repository.PetTagRepository
import edu.fatec.petwise.features.pettags.domain.usecases.*
import edu.fatec.petwise.features.pettags.presentation.viewmodel.PetTagViewModel
import kotlinx.coroutines.cancel

object PetTagDependencyContainer {

    private var remoteDataSource: RemotePetTagDataSource? = null
    private var repository: PetTagRepository? = null
    private var petTagViewModel: PetTagViewModel? = null

    private fun getRemoteDataSource(): RemotePetTagDataSource {
        val existing = remoteDataSource
        if (existing != null) return existing
        val created = RemotePetTagDataSourceImpl(NetworkModule.iotApiService)
        remoteDataSource = created
        return created
    }

    private fun getRepository(): PetTagRepository {
        val existing = repository
        if (existing != null) return existing
        val created = PetTagRepositoryImpl(getRemoteDataSource())
        repository = created
        return created
    }

    private fun buildPetTagViewModel(): PetTagViewModel {
        val repo = getRepository()
        return PetTagViewModel(
            startPairingUseCase = StartPairingUseCase(repo),
            checkInUseCase = CheckInUseCase(repo),
            getPetByTagUseCase = GetPetByTagUseCase(repo),
            getLastReadUseCase = GetLastReadUseCase(repo),
            getPetsUseCase = PetDependencyContainer.provideGetPetsUseCase()
        )
    }

    fun providePetTagViewModel(): PetTagViewModel {
        val existing = petTagViewModel
        if (existing != null) return existing
        val created = buildPetTagViewModel()
        petTagViewModel = created
        return created
    }

    fun provideStartPairingUseCase(): StartPairingUseCase {
        return StartPairingUseCase(getRepository())
    }

    fun provideCheckInUseCase(): CheckInUseCase {
        return CheckInUseCase(getRepository())
    }

    fun provideGetPetByTagUseCase(): GetPetByTagUseCase {
        return GetPetByTagUseCase(getRepository())
    }

    fun provideGetLastReadUseCase(): GetLastReadUseCase {
        return GetLastReadUseCase(getRepository())
    }

    fun providePetTagRepository(): PetTagRepository {
        return getRepository()
    }

    fun reset() {
        petTagViewModel?.viewModelScope?.cancel()
        petTagViewModel = null
        repository = null
        remoteDataSource = null
    }
}
