package edu.fatec.petwise.features.labs.data.repository

import edu.fatec.petwise.core.config.AppConfig
import edu.fatec.petwise.core.data.MockDataProvider
import edu.fatec.petwise.features.labs.data.datasource.RemoteLabDataSource
import edu.fatec.petwise.features.labs.domain.models.Lab
import edu.fatec.petwise.features.labs.domain.repository.LabRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LabRepositoryImpl(
    private val remoteDataSource: RemoteLabDataSource
) : LabRepository {

    override fun getAllLabs(): Flow<List<Lab>> = flow {
        try {
            println("Repositório: Buscando todos os resultados laboratoriais via API")
            val labs = remoteDataSource.getAllLabs()
            println("Repositório: ${labs.size} resultados carregados com sucesso da API")
            emit(labs)
        } catch (e: Exception) {
            if (AppConfig.useMockDataFallback) {
                println("Repositório: Erro ao buscar resultados da API - ${e.message}. Usando dados mock como fallback")
                try {
                    val mockLabs = MockDataProvider.getMockLabs()
                    emit(mockLabs)
                } catch (emitError: Exception) {
                    println("Repositório: Erro ao emitir dados mock - ${emitError.message}")
                }
            } else {
                println("Repositório: Erro ao buscar resultados da API - ${e.message}")
                throw e
            }
        }
    }

    override fun getLabById(id: String): Flow<Lab?> = flow {
        try {
            println("Repositório: Buscando resultado laboratorial por ID '$id' via API")
            val lab = remoteDataSource.getLabById(id)
            if (lab != null) {
                println("Repositório: Resultado '${lab.testType}' encontrado com sucesso")
            } else {
                println("Repositório: Resultado com ID '$id' não encontrado")
            }
            emit(lab)
        } catch (e: Exception) {
            if (AppConfig.useMockDataFallback) {
                println("Repositório: Erro ao buscar resultado por ID '$id' - ${e.message}. Usando dados mock como fallback")
                try {
                    val mockLab = MockDataProvider.getMockLabs().find { it.id == id }
                    emit(mockLab)
                } catch (emitError: Exception) {
                    println("Repositório: Erro ao emitir dados mock - ${emitError.message}")
                }
            } else {
                println("Repositório: Erro ao buscar resultado por ID '$id' - ${e.message}")
                throw e
            }
        }
    }

    override fun searchLabs(query: String): Flow<List<Lab>> = flow {
        try {
            println("Repositório: Iniciando busca de resultados com consulta '$query'")
            val labs = remoteDataSource.searchLabs(query)
            println("Repositório: Busca concluída - ${labs.size} resultados encontrados")
            emit(labs)
        } catch (e: Exception) {
            if (AppConfig.useMockDataFallback) {
                println("Repositório: Erro ao buscar resultados na API - ${e.message}. Usando dados mock como fallback")
                try {
                    val mockLabs = MockDataProvider.getMockLabs()
                        .filter { it.testType.contains(query, ignoreCase = true) || it.labName.contains(query, ignoreCase = true) }
                    emit(mockLabs)
                } catch (emitError: Exception) {
                    println("Repositório: Erro ao emitir dados mock - ${emitError.message}")
                }
            } else {
                println("Repositório: Erro ao buscar resultados na API - ${e.message}")
                throw e
            }
        }
    }

    override fun getLabsByVeterinaryId(veterinaryId: String): Flow<List<Lab>> = flow {
        try {
            println("Repositório: Buscando resultados do veterinário '$veterinaryId' via API")
            val labs = remoteDataSource.getLabsByVeterinaryId(veterinaryId)
            println("Repositório: ${labs.size} resultados encontrados")
            emit(labs)
        } catch (e: Exception) {
            if (AppConfig.useMockDataFallback) {
                println("Repositório: Erro ao buscar resultados do veterinário - ${e.message}. Usando dados mock como fallback")
                try {
                    val mockLabs = MockDataProvider.getMockLabs()
                        .filter { it.veterinaryId == veterinaryId }
                    emit(mockLabs)
                } catch (emitError: Exception) {
                    println("Repositório: Erro ao emitir dados mock - ${emitError.message}")
                }
            } else {
                println("Repositório: Erro ao buscar resultados do veterinário - ${e.message}")
                throw e
            }
        }
    }

    override suspend fun addLab(lab: Lab): Result<Lab> {
        return try {
            println("Repositório: Adicionando novo resultado '${lab.testType}' via API")
            val createdLab = remoteDataSource.createLab(lab)
            println("Repositório: Resultado '${createdLab.testType}' criado com sucesso - ID: ${createdLab.id}")
            Result.success(createdLab)
        } catch (e: Exception) {
            println("Repositório: Erro ao criar resultado '${lab.testType}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateLab(lab: Lab): Result<Lab> {
        return try {
            println("Repositório: Atualizando resultado '${lab.testType}' (ID: ${lab.id}) via API")
            val updatedLab = remoteDataSource.updateLab(lab)
            println("Repositório: Resultado '${updatedLab.testType}' atualizado com sucesso")
            Result.success(updatedLab)
        } catch (e: Exception) {
            println("Repositório: Erro ao atualizar resultado '${lab.testType}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteLab(id: String): Result<Unit> {
        return try {
            println("Repositório: Excluindo resultado com ID '$id' via API")
            remoteDataSource.deleteLab(id)
            println("Repositório: Resultado excluído com sucesso")
            Result.success(Unit)
        } catch (e: Exception) {
            println("Repositório: Erro ao excluir resultado com ID '$id' - ${e.message}")
            Result.failure(e)
        }
    }
}
