package edu.fatec.petwise.features.farmacias.di

import edu.fatec.petwise.core.network.di.NetworkModule
import edu.fatec.petwise.features.farmacias.data.datasource.*
import edu.fatec.petwise.features.farmacias.data.repository.FarmaciaRepositoryImpl
import edu.fatec.petwise.features.farmacias.domain.repository.FarmaciaRepository
import edu.fatec.petwise.features.farmacias.domain.usecases.*
import edu.fatec.petwise.features.farmacias.presentation.viewmodel.*

/**
 * Container de dependências para o módulo de Farmácias.
 * 
 * Centraliza a criação e fornecimento de todas as dependências
 * do módulo, seguindo padrão de Injeção de Dependência manual.
 * 
 * Utiliza singleton pattern com lazy initialization para
 * otimizar uso de memória e evitar instanciações desnecessárias.
 */
object FarmaciaDependencyContainer {

    // ====== DATA LAYER ======

    private val remoteDataSource: RemoteFarmaciaDataSource by lazy {
        RemoteFarmaciaDataSourceImpl(NetworkModule.farmaciaApiService)
    }

    private val repository: FarmaciaRepository by lazy {
        FarmaciaRepositoryImpl(remoteDataSource)
    }

    // ====== DOMAIN LAYER - USE CASES ======

    private val getFarmaciasUseCase: GetFarmaciasUseCase by lazy {
        GetFarmaciasUseCase(repository)
    }

    private val getFarmaciaByIdUseCase: GetFarmaciaByIdUseCase by lazy {
        GetFarmaciaByIdUseCase(repository)
    }

    private val filterFarmaciasUseCase: FilterFarmaciasUseCase by lazy {
        FilterFarmaciasUseCase(repository)
    }

    private val getFarmaciasByCidadeUseCase: GetFarmaciasByCidadeUseCase by lazy {
        GetFarmaciasByCidadeUseCase(repository)
    }

    private val getFarmaciasByEstadoUseCase: GetFarmaciasByEstadoUseCase by lazy {
        GetFarmaciasByEstadoUseCase(repository)
    }

    private val getFarmaciasAtivasUseCase: GetFarmaciasAtivasUseCase by lazy {
        GetFarmaciasAtivasUseCase(repository)
    }

    private val getFarmaciasComFreteGratisUseCase: GetFarmaciasComFreteGratisUseCase by lazy {
        GetFarmaciasComFreteGratisUseCase(repository)
    }

    private val addFarmaciaUseCase: AddFarmaciaUseCase by lazy {
        AddFarmaciaUseCase(repository)
    }

    private val updateFarmaciaUseCase: UpdateFarmaciaUseCase by lazy {
        UpdateFarmaciaUseCase(repository)
    }

    private val deleteFarmaciaUseCase: DeleteFarmaciaUseCase by lazy {
        DeleteFarmaciaUseCase(repository)
    }

    private val updateLimiteCreditoUseCase: UpdateLimiteCreditoUseCase by lazy {
        UpdateLimiteCreditoUseCase(repository)
    }

    private val updateStatusFarmaciaUseCase: UpdateStatusFarmaciaUseCase by lazy {
        UpdateStatusFarmaciaUseCase(repository)
    }

    // ====== PRESENTATION LAYER - VIEWMODELS ======

    /**
     * Fornece uma nova instância de FarmaciasViewModel.
     * 
     * Novo objeto é criado a cada chamada para evitar compartilhamento
     * de estado entre diferentes telas.
     */
    fun provideFarmaciasViewModel(): FarmaciasViewModel {
        return FarmaciasViewModel(
            getFarmaciasUseCase = getFarmaciasUseCase,
            filterFarmaciasUseCase = filterFarmaciasUseCase,
            getFarmaciasAtivasUseCase = getFarmaciasAtivasUseCase,
            deleteFarmaciaUseCase = deleteFarmaciaUseCase,
            updateStatusUseCase = updateStatusFarmaciaUseCase,
            updateLimiteCreditoUseCase = updateLimiteCreditoUseCase,
            getFarmaciasByCidadeUseCase = getFarmaciasByCidadeUseCase,
            getFarmaciasByEstadoUseCase = getFarmaciasByEstadoUseCase
        )
    }

    /**
     * Fornece uma nova instância de AddFarmaciaViewModel.
     */
    fun provideAddFarmaciaViewModel(): AddFarmaciaViewModel {
        return AddFarmaciaViewModel(
            addFarmaciaUseCase = addFarmaciaUseCase
        )
    }

    /**
     * Fornece uma nova instância de UpdateFarmaciaViewModel.
     */
    fun provideUpdateFarmaciaViewModel(farmaciaId: String): UpdateFarmaciaViewModel {
        return UpdateFarmaciaViewModel(
            farmaciaId = farmaciaId,
            getFarmaciaByIdUseCase = getFarmaciaByIdUseCase,
            updateFarmaciaUseCase = updateFarmaciaUseCase
        )
    }

    /**
     * Limpa recursos se necessário.
     * 
     * Pode ser usado em logout ou finalização do app.
     */
    fun clear() {
        // Implementar limpeza de cache se necessário
    }
}
