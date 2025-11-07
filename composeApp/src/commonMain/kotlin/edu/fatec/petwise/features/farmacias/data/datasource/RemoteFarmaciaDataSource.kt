package edu.fatec.petwise.features.farmacias.data.datasource

import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.core.network.NetworkResult

/**
 * Interface para fonte de dados remota de Farmácias.
 */
interface RemoteFarmaciaDataSource {
    suspend fun getAllFarmacias(): NetworkResult<FarmaciaListResponse>
    suspend fun getFarmaciaById(id: String): NetworkResult<FarmaciaDto>
    suspend fun createFarmacia(request: CreateFarmaciaRequest): NetworkResult<FarmaciaDto>
    suspend fun updateFarmacia(id: String, request: UpdateFarmaciaRequest): NetworkResult<FarmaciaDto>
    suspend fun deleteFarmacia(id: String): NetworkResult<Unit>
    suspend fun getFarmaciasByCidade(cidade: String): NetworkResult<FarmaciaListResponse>
    suspend fun getFarmaciasByEstado(estado: String): NetworkResult<FarmaciaListResponse>
    suspend fun getFarmaciasAtivas(): NetworkResult<FarmaciaListResponse>
    suspend fun updateLimiteCredito(id: String, request: UpdateLimiteCreditoRequest): NetworkResult<FarmaciaDto>
    suspend fun updateStatus(id: String, request: UpdateStatusRequest): NetworkResult<FarmaciaDto>
    suspend fun getFarmaciasComFreteGratis(): NetworkResult<FarmaciaListResponse>
    suspend fun searchFarmacias(query: String): NetworkResult<FarmaciaListResponse>
}
