package edu.fatec.petwise.features.consultas.di

import androidx.lifecycle.viewModelScope
import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.consultas.data.datasource.RemoteConsultaDataSourceImpl
import edu.fatec.petwise.features.consultas.data.repository.ConsultaRepositoryImpl
import edu.fatec.petwise.features.consultas.domain.repository.ConsultaRepository
import edu.fatec.petwise.features.consultas.domain.usecases.AddConsultaUseCase
import edu.fatec.petwise.features.consultas.domain.usecases.DeleteConsultaUseCase
import edu.fatec.petwise.features.consultas.domain.usecases.GetConsultasUseCase
import edu.fatec.petwise.features.consultas.domain.usecases.MarkConsultaAsPaidUseCase
import edu.fatec.petwise.features.consultas.domain.usecases.UpdateConsultaStatusUseCase
import edu.fatec.petwise.features.consultas.domain.usecases.UpdateConsultaUseCase
import edu.fatec.petwise.features.consultas.presentation.viewmodel.AddConsultaViewModel
import edu.fatec.petwise.features.consultas.presentation.viewmodel.ConsultasViewModel
import edu.fatec.petwise.features.consultas.presentation.viewmodel.UpdateConsultaViewModel
import edu.fatec.petwise.features.auth.di.AuthDependencyContainer
import kotlinx.coroutines.cancel

object ConsultaDependencyContainer {

    
    private var remoteDataSource: RemoteConsultaDataSourceImpl? = null

    
    private var repository: ConsultaRepository? = null

    
    private var consultasViewModel: ConsultasViewModel? = null

    
    private var addConsultaViewModel: AddConsultaViewModel? = null

    
    private var updateConsultaViewModel: UpdateConsultaViewModel? = null

    private fun getRemoteDataSource(): RemoteConsultaDataSourceImpl {
        val existing = remoteDataSource
        if (existing != null) return existing
        val created = RemoteConsultaDataSourceImpl(NetworkModule.consultaApiService)
        remoteDataSource = created
        return created
    }

    private fun getRepository(): ConsultaRepository {
        val existing = repository
        if (existing != null) return existing
        val created = ConsultaRepositoryImpl(getRemoteDataSource())
        repository = created
        return created
    }

    private fun buildConsultasViewModel(): ConsultasViewModel {
        val repo = getRepository()
        return ConsultasViewModel(
            getConsultasUseCase = GetConsultasUseCase(repo),
            updateConsultaStatusUseCase = UpdateConsultaStatusUseCase(repo),
            deleteConsultaUseCase = DeleteConsultaUseCase(repo),
            markConsultaAsPaidUseCase = MarkConsultaAsPaidUseCase(repo)
        )
    }

    private fun buildAddConsultaViewModel(): AddConsultaViewModel {
        val repo = getRepository()
        return AddConsultaViewModel(
            addConsultaUseCase = AddConsultaUseCase(repo),
            getUserProfileUseCase = AuthDependencyContainer.provideGetUserProfileUseCase()
        )
    }

    private fun buildUpdateConsultaViewModel(): UpdateConsultaViewModel {
        val repo = getRepository()
        return UpdateConsultaViewModel(
            updateConsultaUseCase = UpdateConsultaUseCase(repo)
        )
    }

    fun provideConsultasViewModel(): ConsultasViewModel {
        val existing = consultasViewModel
        if (existing != null) return existing
        val created = buildConsultasViewModel()
        consultasViewModel = created
        return created
    }

    fun provideAddConsultaViewModel(): AddConsultaViewModel {
        val existing = addConsultaViewModel
        if (existing != null) return existing
        val created = buildAddConsultaViewModel()
        addConsultaViewModel = created
        return created
    }

    fun provideUpdateConsultaViewModel(): UpdateConsultaViewModel {
        val existing = updateConsultaViewModel
        if (existing != null) return existing
        val created = buildUpdateConsultaViewModel()
        updateConsultaViewModel = created
        return created
    }

    fun reset() {
        consultasViewModel?.let { it.viewModelScope.cancel() }
        addConsultaViewModel?.let { it.viewModelScope.cancel() }
        updateConsultaViewModel?.let { it.viewModelScope.cancel() }

        consultasViewModel = null
        addConsultaViewModel = null
        updateConsultaViewModel = null

        repository = null
        remoteDataSource = null
    }
}
