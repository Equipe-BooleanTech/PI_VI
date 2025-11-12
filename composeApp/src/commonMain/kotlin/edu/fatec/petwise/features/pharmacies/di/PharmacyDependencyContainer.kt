package edu.fatec.petwise.features.pharmacies.di

import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.core.network.api.PharmacyApiService
import edu.fatec.petwise.core.network.api.PharmacyApiServiceImpl
import edu.fatec.petwise.features.pharmacies.data.datasource.PharmacyDataSource
import edu.fatec.petwise.features.pharmacies.data.datasource.RemotePharmacyDataSourceImpl
import edu.fatec.petwise.features.pharmacies.data.repository.PharmacyRepositoryImpl
import edu.fatec.petwise.features.pharmacies.domain.repository.PharmacyRepository
import edu.fatec.petwise.features.pharmacies.domain.usecases.*

object PharmacyDependencyContainer {

    private val pharmacyApiService: PharmacyApiService by lazy {
        NetworkModule.pharmacyApiService
    }

    private val remoteDataSource: PharmacyDataSource by lazy {
        RemotePharmacyDataSourceImpl(pharmacyApiService)
    }

    private val pharmacyRepository: PharmacyRepository by lazy {
        PharmacyRepositoryImpl(remoteDataSource)
    }

    fun providePharmacyUseCases(): PharmacyUseCases {
        return PharmacyUseCases(
            getAllPharmacies = GetAllPharmaciesUseCase(pharmacyRepository),
            getPharmacyById = GetPharmacyByIdUseCase(pharmacyRepository),
            filterPharmacies = FilterPharmaciesUseCase(pharmacyRepository),
            getVerifiedPharmacies = GetVerifiedPharmaciesUseCase(pharmacyRepository)
        )
    }

    fun provideGetAllPharmaciesUseCase(): GetAllPharmaciesUseCase {
        return GetAllPharmaciesUseCase(pharmacyRepository)
    }

    fun provideGetPharmacyByIdUseCase(): GetPharmacyByIdUseCase {
        return GetPharmacyByIdUseCase(pharmacyRepository)
    }

    fun provideFilterPharmaciesUseCase(): FilterPharmaciesUseCase {
        return FilterPharmaciesUseCase(pharmacyRepository)
    }

    fun provideGetVerifiedPharmaciesUseCase(): GetVerifiedPharmaciesUseCase {
        return GetVerifiedPharmaciesUseCase(pharmacyRepository)
    }
}
