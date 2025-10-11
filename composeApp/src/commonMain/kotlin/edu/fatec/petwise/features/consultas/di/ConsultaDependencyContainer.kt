package edu.fatec.petwise.features.consultas.di

import edu.fatec.petwise.features.consultas.data.datasource.LocalConsultaDataSource
import edu.fatec.petwise.features.consultas.data.datasource.LocalConsultaDataSourceImpl
import edu.fatec.petwise.features.consultas.data.repository.ConsultaRepositoryImpl
import edu.fatec.petwise.features.consultas.domain.repository.ConsultaRepository
import edu.fatec.petwise.features.consultas.domain.usecases.*
import edu.fatec.petwise.features.consultas.presentation.viewmodel.AddConsultaViewModel
import edu.fatec.petwise.features.consultas.presentation.viewmodel.ConsultasViewModel

object ConsultaDependencyContainer {

    private val localDataSource: LocalConsultaDataSource by lazy {
        LocalConsultaDataSourceImpl()
    }

    private val repository: ConsultaRepository by lazy {
        ConsultaRepositoryImpl(localDataSource)
    }

    private val getConsultasUseCase: GetConsultasUseCase by lazy {
        GetConsultasUseCase(repository)
    }

    private val getConsultaByIdUseCase: GetConsultaByIdUseCase by lazy {
        GetConsultaByIdUseCase(repository)
    }

    private val getConsultasByPetIdUseCase: GetConsultasByPetIdUseCase by lazy {
        GetConsultasByPetIdUseCase(repository)
    }

    private val addConsultaUseCase: AddConsultaUseCase by lazy {
        AddConsultaUseCase(repository)
    }

    private val updateConsultaUseCase: UpdateConsultaUseCase by lazy {
        UpdateConsultaUseCase(repository)
    }

    private val deleteConsultaUseCase: DeleteConsultaUseCase by lazy {
        DeleteConsultaUseCase(repository)
    }

    private val updateConsultaStatusUseCase: UpdateConsultaStatusUseCase by lazy {
        UpdateConsultaStatusUseCase(repository)
    }

    private val markConsultaAsPaidUseCase: MarkConsultaAsPaidUseCase by lazy {
        MarkConsultaAsPaidUseCase(repository)
    }

    private val consultasViewModel: ConsultasViewModel by lazy {
        ConsultasViewModel(
            getConsultasUseCase = getConsultasUseCase,
            updateConsultaStatusUseCase = updateConsultaStatusUseCase,
            deleteConsultaUseCase = deleteConsultaUseCase,
            markConsultaAsPaidUseCase = markConsultaAsPaidUseCase
        )
    }

    private val addConsultaViewModel: AddConsultaViewModel by lazy {
        AddConsultaViewModel(
            addConsultaUseCase = addConsultaUseCase
        )
    }

    fun provideConsultasViewModel(): ConsultasViewModel = consultasViewModel

    fun provideAddConsultaViewModel(): AddConsultaViewModel = addConsultaViewModel
}
