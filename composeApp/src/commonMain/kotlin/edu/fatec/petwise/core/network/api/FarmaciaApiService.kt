package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.*
import edu.fatec.petwise.core.network.dto.*

/**
 * Interface do serviço de API para Farmácias.
 * 
 * Define todos os endpoints REST relacionados a farmácias.
 * Todas as operações são suspending functions e retornam NetworkResult
 * para tratamento padronizado de erros.
 */
interface FarmaciaApiService {

    /**
     * Obtém todas as farmácias.
     */
    suspend fun getAllFarmacias(): NetworkResult<FarmaciaListResponse>

    /**
     * Obtém uma farmácia específica por ID.
     */
    suspend fun getFarmaciaById(id: String): NetworkResult<FarmaciaDto>

    /**
     * Cria uma nova farmácia.
     */
    suspend fun createFarmacia(request: CreateFarmaciaRequest): NetworkResult<FarmaciaDto>

    /**
     * Atualiza uma farmácia existente.
     */
    suspend fun updateFarmacia(id: String, request: UpdateFarmaciaRequest): NetworkResult<FarmaciaDto>

    /**
     * Remove uma farmácia.
     */
    suspend fun deleteFarmacia(id: String): NetworkResult<Unit>

    /**
     * Busca farmácias por cidade.
     */
    suspend fun getFarmaciasByCidade(cidade: String): NetworkResult<FarmaciaListResponse>

    /**
     * Busca farmácias por estado.
     */
    suspend fun getFarmaciasByEstado(estado: String): NetworkResult<FarmaciaListResponse>

    /**
     * Obtém apenas farmácias ativas.
     */
    suspend fun getFarmaciasAtivas(): NetworkResult<FarmaciaListResponse>

    /**
     * Atualiza o limite de crédito.
     */
    suspend fun updateLimiteCredito(id: String, request: UpdateLimiteCreditoRequest): NetworkResult<FarmaciaDto>

    /**
     * Atualiza o status da farmácia.
     */
    suspend fun updateStatus(id: String, request: UpdateStatusRequest): NetworkResult<FarmaciaDto>

    /**
     * Obtém farmácias com frete grátis.
     */
    suspend fun getFarmaciasComFreteGratis(): NetworkResult<FarmaciaListResponse>

    /**
     * Busca farmácias (search endpoint).
     */
    suspend fun searchFarmacias(query: String): NetworkResult<FarmaciaListResponse>
}
