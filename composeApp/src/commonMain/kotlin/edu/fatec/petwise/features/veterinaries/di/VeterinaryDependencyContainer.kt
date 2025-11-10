package edu.fatec.petwise.features.veterinaries.di

import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.core.network.api.VeterinaryApiService
import edu.fatec.petwise.core.network.api.VeterinaryApiServiceImpl
import edu.fatec.petwise.features.veterinaries.data.datasource.VeterinaryDataSource
import edu.fatec.petwise.features.veterinaries.data.datasource.RemoteVeterinaryDataSourceImpl
import edu.fatec.petwise.features.veterinaries.data.repository.VeterinaryRepositoryImpl
import edu.fatec.petwise.features.veterinaries.domain.repository.VeterinaryRepository
import edu.fatec.petwise.features.veterinaries.domain.usecases.*

object VeterinaryDependencyContainer {

    private val veterinaryApiService: VeterinaryApiService by lazy {
        NetworkModule.veterinaryApiService
    }

    private val remoteDataSource: VeterinaryDataSource by lazy {
        RemoteVeterinaryDataSourceImpl(veterinaryApiService)
    }

    private val veterinaryRepository: VeterinaryRepository by lazy {
        VeterinaryRepositoryImpl(remoteDataSource)
    }

    fun provideVeterinaryUseCases(): VeterinaryUseCases {
        return VeterinaryUseCases(
            getAllVeterinaries = GetAllVeterinariesUseCase(veterinaryRepository),
            getVeterinaryById = GetVeterinaryByIdUseCase(veterinaryRepository),
            searchVeterinaries = SearchVeterinariesUseCase(veterinaryRepository),
            filterVeterinaries = FilterVeterinariesUseCase(veterinaryRepository),
            getVerifiedVeterinaries = GetVerifiedVeterinariesUseCase(veterinaryRepository)
        )
    }

    fun provideGetAllVeterinariesUseCase(): GetAllVeterinariesUseCase {
        return GetAllVeterinariesUseCase(veterinaryRepository)
    }

    fun provideGetVeterinaryByIdUseCase(): GetVeterinaryByIdUseCase {
        return GetVeterinaryByIdUseCase(veterinaryRepository)
    }

    fun provideSearchVeterinariesUseCase(): SearchVeterinariesUseCase {
        return SearchVeterinariesUseCase(veterinaryRepository)
    }

    fun provideFilterVeterinariesUseCase(): FilterVeterinariesUseCase {
        return FilterVeterinariesUseCase(veterinaryRepository)
    }

    fun provideGetVerifiedVeterinariesUseCase(): GetVerifiedVeterinariesUseCase {
        return GetVerifiedVeterinariesUseCase(veterinaryRepository)
    }
}