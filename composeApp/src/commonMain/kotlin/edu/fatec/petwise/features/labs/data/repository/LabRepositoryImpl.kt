package edu.fatec.petwise.features.labs.data.repository

import edu.fatec.petwise.features.labs.data.datasource.RemoteLabDataSource
import edu.fatec.petwise.features.labs.domain.models.LabResult
import edu.fatec.petwise.features.labs.domain.repository.LabRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LabRepositoryImpl(
    private val remoteDataSource: RemoteLabDataSource
) : LabRepository {

    override fun getAllLabResults(): Flow<List<LabResult>> = flow {
        try {
            println("Repositório: Buscando todos os resultados de laboratório via API")
            val labResults = remoteDataSource.getAllLabResults()
            println("Repositório: ${labResults.size} resultados de laboratório carregados com sucesso da API")
            emit(labResults)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar resultados de laboratório da API - ${e.message}")
                throw e
            }
    }

    override fun getLabResultById(id: String): Flow<LabResult?> = flow {
        try {
            println("Repositório: Buscando resultado de laboratório por ID '$id' via API")
            val labResult = remoteDataSource.getLabResultById(id)
            if (labResult != null) {
                println("Repositório: Resultado de laboratório '${labResult.labType}' encontrado com sucesso")
            } else {
                println("Repositório: Resultado de laboratório com ID '$id' não encontrado")
            }
            emit(labResult)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar resultado de laboratório por ID '$id' - ${e.message}")
                throw e
        }
    }

    override fun searchLabResults(query: String): Flow<List<LabResult>> = flow {
        try {
            println("Repositório: Iniciando busca de resultados de laboratório com consulta '$query'")
            val labResults = remoteDataSource.searchLabResults(query)
            println("Repositório: Busca concluída - ${labResults.size} resultados de laboratório encontrados")
            emit(labResults)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar resultados de laboratório na API - ${e.message}")
                throw e
            }
        }

    override suspend fun addLabResult(labResult: LabResult): Result<LabResult> {
        return try {
            println("Repositório: Adicionando novo resultado de laboratório '${labResult.labType}' via API")
            val createdLabResult = remoteDataSource.createLabResult(labResult)
            println("Repositório: Resultado de laboratório '${createdLabResult.labType}' criado com sucesso - ID: ${createdLabResult.id}")
            Result.success(createdLabResult)
        } catch (e: Exception) {
            println("Repositório: Erro ao criar resultado de laboratório '${labResult.labType}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateLabResult(labResult: LabResult): Result<LabResult> {
        return try {
            println("Repositório: Atualizando resultado de laboratório '${labResult.labType}' (ID: ${labResult.id}) via API")
            val updatedLabResult = remoteDataSource.updateLabResult(labResult)
            println("Repositório: Resultado de laboratório '${updatedLabResult.labType}' atualizado com sucesso")
            Result.success(updatedLabResult)
        } catch (e: Exception) {
            println("Repositório: Erro ao atualizar resultado de laboratório '${labResult.labType}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteLabResult(id: String): Result<Unit> {
        return try {
            println("Repositório: Excluindo resultado de laboratório com ID '$id' via API")
            remoteDataSource.deleteLabResult(id)
            println("Repositório: Resultado de laboratório excluído com sucesso")
            Result.success(Unit)
        } catch (e: Exception) {
            println("Repositório: Erro ao excluir resultado de laboratório com ID '$id' - ${e.message}")
            Result.failure(e)
        }
    }
}

