package edu.fatec.petwise.features.prescriptions.di

import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.auth.di.AuthDependencyContainer
import edu.fatec.petwise.features.pets.di.PetDependencyContainer
import edu.fatec.petwise.features.prescriptions.data.datasource.RemotePrescriptionDataSource
import edu.fatec.petwise.features.prescriptions.data.datasource.RemotePrescriptionDataSourceImpl
import edu.fatec.petwise.features.prescriptions.data.repository.PrescriptionRepositoryImpl
import edu.fatec.petwise.features.prescriptions.domain.repository.PrescriptionRepository
import edu.fatec.petwise.features.prescriptions.domain.usecases.AddPrescriptionUseCase
import edu.fatec.petwise.features.prescriptions.domain.usecases.DeletePrescriptionUseCase
import edu.fatec.petwise.features.prescriptions.domain.usecases.GetPrescriptionByIdUseCase
import edu.fatec.petwise.features.prescriptions.domain.usecases.GetPrescriptionsUseCase
import edu.fatec.petwise.features.prescriptions.domain.usecases.UpdatePrescriptionUseCase
import edu.fatec.petwise.features.prescriptions.presentation.viewmodel.AddPrescriptionViewModel
import edu.fatec.petwise.features.prescriptions.presentation.viewmodel.PrescriptionsViewModel
import edu.fatec.petwise.features.prescriptions.presentation.viewmodel.UpdatePrescriptionViewModel

object PrescriptionDependencyContainer {
    
    private val remoteDataSource: RemotePrescriptionDataSource by lazy {
        RemotePrescriptionDataSourceImpl(
            NetworkModule.prescriptionApiService,
            AuthDependencyContainer.provideGetUserProfileUseCase()
        )
    }

    private val repository: PrescriptionRepository by lazy {
        PrescriptionRepositoryImpl(remoteDataSource)
    }

    val getPrescriptionsUseCase: GetPrescriptionsUseCase by lazy {
        GetPrescriptionsUseCase(repository)
    }

    val getPrescriptionByIdUseCase: GetPrescriptionByIdUseCase by lazy {
        GetPrescriptionByIdUseCase(repository)
    }

    val addPrescriptionUseCase: AddPrescriptionUseCase by lazy {
        AddPrescriptionUseCase(repository)
    }

    val updatePrescriptionUseCase: UpdatePrescriptionUseCase by lazy {
        UpdatePrescriptionUseCase(repository)
    }

    val deletePrescriptionUseCase: DeletePrescriptionUseCase by lazy {
        DeletePrescriptionUseCase(repository)
    }

    val addPrescriptionViewModel: AddPrescriptionViewModel by lazy {
        AddPrescriptionViewModel(addPrescriptionUseCase)
    }

    val prescriptionsViewModel: PrescriptionsViewModel by lazy {
        PrescriptionsViewModel(
            getPrescriptionsUseCase,
            deletePrescriptionUseCase,
            PetDependencyContainer.provideGetPetsUseCase(),
            AuthDependencyContainer.provideGetUserProfileUseCase()
        )
    }

    val updatePrescriptionViewModel: UpdatePrescriptionViewModel by lazy {
        UpdatePrescriptionViewModel(updatePrescriptionUseCase, getPrescriptionByIdUseCase, AuthDependencyContainer.provideGetUserProfileUseCase())
    }

    fun provideGetPrescriptionsUseCase(): GetPrescriptionsUseCase {
        return getPrescriptionsUseCase
    }

    fun providePrescriptionRepository(): PrescriptionRepository {
        return repository
    }
}
