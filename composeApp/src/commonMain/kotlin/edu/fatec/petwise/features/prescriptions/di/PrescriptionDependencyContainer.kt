package edu.fatec.petwise.features.prescriptions.di

import edu.fatec.petwise.features.prescriptions.data.datasource.RemotePrescriptionDataSource
import edu.fatec.petwise.features.prescriptions.data.datasource.RemotePrescriptionDataSourceImpl
import edu.fatec.petwise.features.prescriptions.data.repository.PrescriptionRepositoryImpl
import edu.fatec.petwise.features.prescriptions.domain.repository.PrescriptionRepository
import edu.fatec.petwise.features.prescriptions.domain.usecases.*

object PrescriptionDependencyContainer {
    
    private val remoteDataSource: RemotePrescriptionDataSource by lazy {
        RemotePrescriptionDataSourceImpl()
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
}
