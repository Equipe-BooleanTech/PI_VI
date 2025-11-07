package edu.fatec.petwise.features.farmacias.data.repository

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import edu.fatec.petwise.features.farmacias.data.datasource.RemoteFarmaciaDataSource
import edu.fatec.petwise.features.farmacias.domain.models.*
import edu.fatec.petwise.features.farmacias.domain.repository.FarmaciaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementação do repositório de Farmácias.
 * 
 * Coordena o acesso aos dados de farmácias, aplicando transformações
 * entre DTOs e modelos de domínio, e implementando lógica de negócio.
 */
class FarmaciaRepositoryImpl(
    private val remoteDataSource: RemoteFarmaciaDataSource
) : FarmaciaRepository {

    override fun getAllFarmacias(): Flow<List<Farmacia>> = flow {
        println("Repository: Obtendo todas as farmácias")
        when (val result = remoteDataSource.getAllFarmacias()) {
            is NetworkResult.Success -> {
                val farmacias = result.data.farmacias.map { it.toDomain() }
                println("Repository: ${farmacias.size} farmácias mapeadas para domínio")
                emit(farmacias)
            }
            is NetworkResult.Error -> {
                println("Repository: Erro ao obter farmácias - ${result.message}")
                throw Exception(result.message)
            }
            is NetworkResult.Loading -> {
                println("Repository: Carregando farmácias...")
            }
        }
    }

    override suspend fun getFarmaciaById(id: String): Result<Farmacia> {
        println("Repository: Obtendo farmácia por ID: $id")
        return when (val result = remoteDataSource.getFarmaciaById(id)) {
            is NetworkResult.Success -> {
                val farmacia = result.data.toDomain()
                println("Repository: Farmácia ${farmacia.nomeFantasia} obtida com sucesso")
                Result.success(farmacia)
            }
            is NetworkResult.Error -> {
                println("Repository: Erro ao obter farmácia - ${result.message}")
                Result.failure(Exception(result.message))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Carregando..."))
            }
        }
    }

    override suspend fun createFarmacia(farmacia: Farmacia): Result<Farmacia> {
        println("Repository: Criando nova farmácia: ${farmacia.nomeFantasia}")
        return when (val result = remoteDataSource.createFarmacia(farmacia.toCreateRequest())) {
            is NetworkResult.Success -> {
                val created = result.data.toDomain()
                println("Repository: Farmácia criada com ID: ${created.id}")
                Result.success(created)
            }
            is NetworkResult.Error -> {
                println("Repository: Erro ao criar farmácia - ${result.message}")
                Result.failure(Exception(result.message))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Carregando..."))
            }
        }
    }

    override suspend fun updateFarmacia(id: String, farmacia: Farmacia): Result<Farmacia> {
        println("Repository: Atualizando farmácia: $id")
        return when (val result = remoteDataSource.updateFarmacia(id, farmacia.toUpdateRequest())) {
            is NetworkResult.Success -> {
                val updated = result.data.toDomain()
                println("Repository: Farmácia atualizada com sucesso")
                Result.success(updated)
            }
            is NetworkResult.Error -> {
                println("Repository: Erro ao atualizar farmácia - ${result.message}")
                Result.failure(Exception(result.message))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Carregando..."))
            }
        }
    }

    override suspend fun deleteFarmacia(id: String): Result<Unit> {
        println("Repository: Deletando farmácia: $id")
        return when (val result = remoteDataSource.deleteFarmacia(id)) {
            is NetworkResult.Success -> {
                println("Repository: Farmácia deletada com sucesso")
                Result.success(Unit)
            }
            is NetworkResult.Error -> {
                println("Repository: Erro ao deletar farmácia - ${result.message}")
                Result.failure(Exception(result.message))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Carregando..."))
            }
        }
    }

    override fun filterFarmacias(options: FarmaciaFilterOptions): Flow<List<Farmacia>> = flow {
        println("Repository: Filtrando farmácias com opções: $options")
        when (val result = remoteDataSource.getAllFarmacias()) {
            is NetworkResult.Success -> {
                var farmacias = result.data.farmacias.map { it.toDomain() }
                
                // Aplica filtros localmente
                options.tipo?.let { tipo ->
                    farmacias = farmacias.filter { it.tipo == tipo }
                }
                
                options.status?.let { status ->
                    farmacias = farmacias.filter { it.status == status }
                }
                
                options.regiao?.let { regiao ->
                    farmacias = farmacias.filter { it.regiao == regiao }
                }
                
                options.estado?.let { estado ->
                    farmacias = farmacias.filter { 
                        it.estado.equals(estado, ignoreCase = true) 
                    }
                }
                
                options.cidade?.let { cidade ->
                    farmacias = farmacias.filter { 
                        it.cidade.equals(cidade, ignoreCase = true) 
                    }
                }
                
                if (options.apenasComCredito) {
                    farmacias = farmacias.filter { it.limiteCredito > 0 }
                }
                
                if (options.apenasFreteGratis) {
                    farmacias = farmacias.filter { it.freteGratis }
                }
                
                options.descontoMinimo?.let { desconto ->
                    farmacias = farmacias.filter { it.descontoMaximo >= desconto }
                }
                
                println("Repository: ${farmacias.size} farmácias após filtros")
                emit(farmacias)
            }
            is NetworkResult.Error -> {
                println("Repository: Erro ao filtrar - ${result.message}")
                throw Exception(result.message)
            }
            is NetworkResult.Loading -> {
                println("Repository: Carregando para filtrar...")
            }
        }
    }

    override fun getFarmaciasByCidade(cidade: String): Flow<List<Farmacia>> = flow {
        println("Repository: Obtendo farmácias por cidade: $cidade")
        when (val result = remoteDataSource.getFarmaciasByCidade(cidade)) {
            is NetworkResult.Success -> {
                val farmacias = result.data.farmacias.map { it.toDomain() }
                println("Repository: ${farmacias.size} farmácias encontradas em $cidade")
                emit(farmacias)
            }
            is NetworkResult.Error -> {
                println("Repository: Erro ao buscar por cidade - ${result.message}")
                throw Exception(result.message)
            }
            is NetworkResult.Loading -> {}
        }
    }

    override fun getFarmaciasByEstado(estado: String): Flow<List<Farmacia>> = flow {
        println("Repository: Obtendo farmácias por estado: $estado")
        when (val result = remoteDataSource.getFarmaciasByEstado(estado)) {
            is NetworkResult.Success -> {
                val farmacias = result.data.farmacias.map { it.toDomain() }
                println("Repository: ${farmacias.size} farmácias encontradas em $estado")
                emit(farmacias)
            }
            is NetworkResult.Error -> {
                println("Repository: Erro ao buscar por estado - ${result.message}")
                throw Exception(result.message)
            }
            is NetworkResult.Loading -> {}
        }
    }

    override fun getFarmaciasAtivas(): Flow<List<Farmacia>> = flow {
        println("Repository: Obtendo farmácias ativas")
        when (val result = remoteDataSource.getFarmaciasAtivas()) {
            is NetworkResult.Success -> {
                val farmacias = result.data.farmacias.map { it.toDomain() }
                println("Repository: ${farmacias.size} farmácias ativas encontradas")
                emit(farmacias)
            }
            is NetworkResult.Error -> {
                println("Repository: Erro ao buscar ativas - ${result.message}")
                throw Exception(result.message)
            }
            is NetworkResult.Loading -> {}
        }
    }

    override suspend fun updateLimiteCredito(id: String, novoLimite: Double): Result<Farmacia> {
        println("Repository: Atualizando limite de crédito para: $novoLimite")
        
        if (novoLimite < 0) {
            return Result.failure(Exception("Limite de crédito não pode ser negativo"))
        }
        
        return when (val result = remoteDataSource.updateLimiteCredito(
            id,
            UpdateLimiteCreditoRequest(novoLimite)
        )) {
            is NetworkResult.Success -> {
                val updated = result.data.toDomain()
                println("Repository: Limite de crédito atualizado com sucesso")
                Result.success(updated)
            }
            is NetworkResult.Error -> {
                println("Repository: Erro ao atualizar limite - ${result.message}")
                Result.failure(Exception(result.message))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Carregando..."))
            }
        }
    }

    override suspend fun updateStatus(
        id: String,
        novoStatus: String,
        motivo: String?
    ): Result<Farmacia> {
        println("Repository: Atualizando status para: $novoStatus")
        return when (val result = remoteDataSource.updateStatus(
            id,
            UpdateStatusRequest(novoStatus, motivo)
        )) {
            is NetworkResult.Success -> {
                val updated = result.data.toDomain()
                println("Repository: Status atualizado com sucesso")
                Result.success(updated)
            }
            is NetworkResult.Error -> {
                println("Repository: Erro ao atualizar status - ${result.message}")
                Result.failure(Exception(result.message))
            }
            is NetworkResult.Loading -> {
                Result.failure(Exception("Carregando..."))
            }
        }
    }

    override fun getFarmaciasComFreteGratis(): Flow<List<Farmacia>> = flow {
        println("Repository: Obtendo farmácias com frete grátis")
        when (val result = remoteDataSource.getFarmaciasComFreteGratis()) {
            is NetworkResult.Success -> {
                val farmacias = result.data.farmacias.map { it.toDomain() }
                println("Repository: ${farmacias.size} farmácias com frete grátis")
                emit(farmacias)
            }
            is NetworkResult.Error -> {
                println("Repository: Erro ao buscar frete grátis - ${result.message}")
                throw Exception(result.message)
            }
            is NetworkResult.Loading -> {}
        }
    }
}
