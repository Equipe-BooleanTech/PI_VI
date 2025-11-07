package edu.fatec.petwise.features.farmacias.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.FarmaciaApiService
import edu.fatec.petwise.core.network.dto.*

/**
 * Implementação da fonte de dados remota para Farmácias.
 * 
 * Delega as chamadas de rede para o FarmaciaApiService,
 * adicionando logging e potencial processamento adicional.
 */
class RemoteFarmaciaDataSourceImpl(
    private val apiService: FarmaciaApiService
) : RemoteFarmaciaDataSource {

    override suspend fun getAllFarmacias(): NetworkResult<FarmaciaListResponse> {
        println("DataSource: Buscando todas as farmácias")
        return try {
            val result = apiService.getAllFarmacias()
            when (result) {
                is NetworkResult.Success -> {
                    println("DataSource: ${result.data.total} farmácias obtidas com sucesso")
                }
                is NetworkResult.Error -> {
                    println("DataSource: Erro ao buscar farmácias - ${result.message}")
                }
                is NetworkResult.Loading -> {
                    println("DataSource: Carregando farmácias...")
                }
            }
            result
        } catch (e: Exception) {
            println("DataSource: Exceção ao buscar farmácias - ${e.message}")
            NetworkResult.Error(e.message ?: "Erro desconhecido")
        }
    }

    override suspend fun getFarmaciaById(id: String): NetworkResult<FarmaciaDto> {
        println("DataSource: Buscando farmácia com ID: $id")
        return try {
            val result = apiService.getFarmaciaById(id)
            when (result) {
                is NetworkResult.Success -> {
                    println("DataSource: Farmácia ${result.data.nomeFantasia} obtida com sucesso")
                }
                is NetworkResult.Error -> {
                    println("DataSource: Erro ao buscar farmácia - ${result.message}")
                }
                else -> {}
            }
            result
        } catch (e: Exception) {
            println("DataSource: Exceção ao buscar farmácia - ${e.message}")
            NetworkResult.Error(e.message ?: "Erro ao buscar farmácia")
        }
    }

    override suspend fun createFarmacia(request: CreateFarmaciaRequest): NetworkResult<FarmaciaDto> {
        println("DataSource: Criando nova farmácia: ${request.nomeFantasia}")
        return try {
            val result = apiService.createFarmacia(request)
            when (result) {
                is NetworkResult.Success -> {
                    println("DataSource: Farmácia criada com ID: ${result.data.id}")
                }
                is NetworkResult.Error -> {
                    println("DataSource: Erro ao criar farmácia - ${result.message}")
                }
                else -> {}
            }
            result
        } catch (e: Exception) {
            println("DataSource: Exceção ao criar farmácia - ${e.message}")
            NetworkResult.Error(e.message ?: "Erro ao criar farmácia")
        }
    }

    override suspend fun updateFarmacia(
        id: String,
        request: UpdateFarmaciaRequest
    ): NetworkResult<FarmaciaDto> {
        println("DataSource: Atualizando farmácia: $id")
        return try {
            val result = apiService.updateFarmacia(id, request)
            when (result) {
                is NetworkResult.Success -> {
                    println("DataSource: Farmácia atualizada com sucesso")
                }
                is NetworkResult.Error -> {
                    println("DataSource: Erro ao atualizar farmácia - ${result.message}")
                }
                else -> {}
            }
            result
        } catch (e: Exception) {
            println("DataSource: Exceção ao atualizar farmácia - ${e.message}")
            NetworkResult.Error(e.message ?: "Erro ao atualizar farmácia")
        }
    }

    override suspend fun deleteFarmacia(id: String): NetworkResult<Unit> {
        println("DataSource: Deletando farmácia: $id")
        return try {
            val result = apiService.deleteFarmacia(id)
            when (result) {
                is NetworkResult.Success -> {
                    println("DataSource: Farmácia deletada com sucesso")
                }
                is NetworkResult.Error -> {
                    println("DataSource: Erro ao deletar farmácia - ${result.message}")
                }
                else -> {}
            }
            result
        } catch (e: Exception) {
            println("DataSource: Exceção ao deletar farmácia - ${e.message}")
            NetworkResult.Error(e.message ?: "Erro ao deletar farmácia")
        }
    }

    override suspend fun getFarmaciasByCidade(cidade: String): NetworkResult<FarmaciaListResponse> {
        println("DataSource: Buscando farmácias na cidade: $cidade")
        return try {
            val result = apiService.getFarmaciasByCidade(cidade)
            when (result) {
                is NetworkResult.Success -> {
                    println("DataSource: ${result.data.total} farmácias encontradas em $cidade")
                }
                is NetworkResult.Error -> {
                    println("DataSource: Erro ao buscar farmácias por cidade - ${result.message}")
                }
                else -> {}
            }
            result
        } catch (e: Exception) {
            println("DataSource: Exceção ao buscar por cidade - ${e.message}")
            NetworkResult.Error(e.message ?: "Erro ao buscar por cidade")
        }
    }

    override suspend fun getFarmaciasByEstado(estado: String): NetworkResult<FarmaciaListResponse> {
        println("DataSource: Buscando farmácias no estado: $estado")
        return try {
            val result = apiService.getFarmaciasByEstado(estado)
            when (result) {
                is NetworkResult.Success -> {
                    println("DataSource: ${result.data.total} farmácias encontradas em $estado")
                }
                is NetworkResult.Error -> {
                    println("DataSource: Erro ao buscar farmácias por estado - ${result.message}")
                }
                else -> {}
            }
            result
        } catch (e: Exception) {
            println("DataSource: Exceção ao buscar por estado - ${e.message}")
            NetworkResult.Error(e.message ?: "Erro ao buscar por estado")
        }
    }

    override suspend fun getFarmaciasAtivas(): NetworkResult<FarmaciaListResponse> {
        println("DataSource: Buscando farmácias ativas")
        return try {
            val result = apiService.getFarmaciasAtivas()
            when (result) {
                is NetworkResult.Success -> {
                    println("DataSource: ${result.data.total} farmácias ativas encontradas")
                }
                is NetworkResult.Error -> {
                    println("DataSource: Erro ao buscar farmácias ativas - ${result.message}")
                }
                else -> {}
            }
            result
        } catch (e: Exception) {
            println("DataSource: Exceção ao buscar farmácias ativas - ${e.message}")
            NetworkResult.Error(e.message ?: "Erro ao buscar farmácias ativas")
        }
    }

    override suspend fun updateLimiteCredito(
        id: String,
        request: UpdateLimiteCreditoRequest
    ): NetworkResult<FarmaciaDto> {
        println("DataSource: Atualizando limite de crédito da farmácia $id para ${request.limiteCredito}")
        return try {
            val result = apiService.updateLimiteCredito(id, request)
            when (result) {
                is NetworkResult.Success -> {
                    println("DataSource: Limite de crédito atualizado com sucesso")
                }
                is NetworkResult.Error -> {
                    println("DataSource: Erro ao atualizar limite de crédito - ${result.message}")
                }
                else -> {}
            }
            result
        } catch (e: Exception) {
            println("DataSource: Exceção ao atualizar limite de crédito - ${e.message}")
            NetworkResult.Error(e.message ?: "Erro ao atualizar limite de crédito")
        }
    }

    override suspend fun updateStatus(
        id: String,
        request: UpdateStatusRequest
    ): NetworkResult<FarmaciaDto> {
        println("DataSource: Atualizando status da farmácia $id para ${request.status}")
        return try {
            val result = apiService.updateStatus(id, request)
            when (result) {
                is NetworkResult.Success -> {
                    println("DataSource: Status atualizado com sucesso")
                }
                is NetworkResult.Error -> {
                    println("DataSource: Erro ao atualizar status - ${result.message}")
                }
                else -> {}
            }
            result
        } catch (e: Exception) {
            println("DataSource: Exceção ao atualizar status - ${e.message}")
            NetworkResult.Error(e.message ?: "Erro ao atualizar status")
        }
    }

    override suspend fun getFarmaciasComFreteGratis(): NetworkResult<FarmaciaListResponse> {
        println("DataSource: Buscando farmácias com frete grátis")
        return try {
            val result = apiService.getFarmaciasComFreteGratis()
            when (result) {
                is NetworkResult.Success -> {
                    println("DataSource: ${result.data.total} farmácias com frete grátis encontradas")
                }
                is NetworkResult.Error -> {
                    println("DataSource: Erro ao buscar farmácias com frete grátis - ${result.message}")
                }
                else -> {}
            }
            result
        } catch (e: Exception) {
            println("DataSource: Exceção ao buscar frete grátis - ${e.message}")
            NetworkResult.Error(e.message ?: "Erro ao buscar frete grátis")
        }
    }

    override suspend fun searchFarmacias(query: String): NetworkResult<FarmaciaListResponse> {
        println("DataSource: Buscando farmácias com query: $query")
        return try {
            val result = apiService.searchFarmacias(query)
            when (result) {
                is NetworkResult.Success -> {
                    println("DataSource: ${result.data.total} farmácias encontradas na busca")
                }
                is NetworkResult.Error -> {
                    println("DataSource: Erro na busca - ${result.message}")
                }
                else -> {}
            }
            result
        } catch (e: Exception) {
            println("DataSource: Exceção na busca - ${e.message}")
            NetworkResult.Error(e.message ?: "Erro na busca")
        }
    }
}
