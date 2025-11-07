package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.*
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Implementação do serviço de API para Farmácias.
 * 
 * Utiliza o NetworkRequestHandler para realizar chamadas HTTP
 * padronizadas com tratamento de erros e retry automático.
 */
class FarmaciaApiServiceImpl(
    private val requestHandler: NetworkRequestHandler
) : FarmaciaApiService {

    override suspend fun getAllFarmacias(): NetworkResult<FarmaciaListResponse> {
        return requestHandler.makeRequest {
            get(ApiEndpoints.FARMACIAS)
        }
    }

    override suspend fun getFarmaciaById(id: String): NetworkResult<FarmaciaDto> {
        return requestHandler.makeRequest {
            get(ApiEndpoints.getFarmacia(id))
        }
    }

    override suspend fun createFarmacia(request: CreateFarmaciaRequest): NetworkResult<FarmaciaDto> {
        return requestHandler.makeRequest {
            post(ApiEndpoints.FARMACIAS) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    override suspend fun updateFarmacia(
        id: String,
        request: UpdateFarmaciaRequest
    ): NetworkResult<FarmaciaDto> {
        return requestHandler.makeRequest {
            put(ApiEndpoints.getFarmacia(id)) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    override suspend fun deleteFarmacia(id: String): NetworkResult<Unit> {
        return requestHandler.makeRequest {
            delete(ApiEndpoints.getFarmacia(id))
        }
    }

    override suspend fun getFarmaciasByCidade(cidade: String): NetworkResult<FarmaciaListResponse> {
        return requestHandler.makeRequest {
            get(ApiEndpoints.getFarmaciasByCidade(cidade))
        }
    }

    override suspend fun getFarmaciasByEstado(estado: String): NetworkResult<FarmaciaListResponse> {
        return requestHandler.makeRequest {
            get(ApiEndpoints.getFarmaciasByEstado(estado))
        }
    }

    override suspend fun getFarmaciasAtivas(): NetworkResult<FarmaciaListResponse> {
        return requestHandler.makeRequest {
            get(ApiEndpoints.FARMACIAS_ATIVAS)
        }
    }

    override suspend fun updateLimiteCredito(
        id: String,
        request: UpdateLimiteCreditoRequest
    ): NetworkResult<FarmaciaDto> {
        return requestHandler.makeRequest {
            patch(ApiEndpoints.updateFarmaciaLimiteCredito(id)) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    override suspend fun updateStatus(
        id: String,
        request: UpdateStatusRequest
    ): NetworkResult<FarmaciaDto> {
        return requestHandler.makeRequest {
            patch(ApiEndpoints.updateFarmaciaStatus(id)) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }

    override suspend fun getFarmaciasComFreteGratis(): NetworkResult<FarmaciaListResponse> {
        return requestHandler.makeRequest {
            get(ApiEndpoints.FARMACIAS_FRETE_GRATIS)
        }
    }

    override suspend fun searchFarmacias(query: String): NetworkResult<FarmaciaListResponse> {
        return requestHandler.makeRequest {
            get(ApiEndpoints.FARMACIAS_SEARCH) {
                parameter("q", query)
            }
        }
    }
}
