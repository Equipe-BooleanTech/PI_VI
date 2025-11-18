package edu.fatec.petwise.features.labs.data.repository

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
            println("Repositório: Buscando todos os laboratórios via API")
            val labs = remoteDataSource.getAllLabs()
            println("Repositório: ${labs.size} laboratórios carregados com sucesso da API")
            emit(labs)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar laboratórios da API - ${e.message}")
                throw e
            }
    }

    override fun getLabById(id: String): Flow<Lab?> = flow {
        try {
            println("Repositório: Buscando laboratório por ID '$id' via API")
            val lab = remoteDataSource.getLabById(id)
            if (lab != null) {
                println("Repositório: Laboratório '${lab.name}' encontrado com sucesso")
            } else {
                println("Repositório: Laboratório com ID '$id' não encontrado")
            }
            emit(lab)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar laboratório por ID '$id' - ${e.message}")
                throw e
        }
    }

    override fun searchLabs(query: String): Flow<List<Lab>> = flow {
        try {
            println("Repositório: Iniciando busca de laboratórios com consulta '$query'")
            val labs = remoteDataSource.searchLabs(query)
            println("Repositório: Busca concluída - ${labs.size} laboratórios encontrados")
            emit(labs)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar laboratórios na API - ${e.message}")
                throw e
            }
        }

    override suspend fun addLab(lab: Lab): Result<Lab> {
        return try {
            println("Repositório: Adicionando novo laboratório '${lab.name}' via API")
            val createdLab = remoteDataSource.createLab(lab)
            println("Repositório: Laboratório '${createdLab.name}' criado com sucesso - ID: ${createdLab.id}")
            Result.success(createdLab)
        } catch (e: Exception) {
            println("Repositório: Erro ao criar laboratório '${lab.name}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateLab(lab: Lab): Result<Lab> {
        return try {
            println("Repositório: Atualizando laboratório '${lab.name}' (ID: ${lab.id}) via API")
            val updatedLab = remoteDataSource.updateLab(lab)
            println("Repositório: Laboratório '${updatedLab.name}' atualizado com sucesso")
            Result.success(updatedLab)
        } catch (e: Exception) {
            println("Repositório: Erro ao atualizar laboratório '${lab.name}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteLab(id: String): Result<Unit> {
        return try {
            println("Repositório: Excluindo laboratório com ID '$id' via API")
            remoteDataSource.deleteLab(id)
            println("Repositório: Laboratório excluído com sucesso")
            Result.success(Unit)
        } catch (e: Exception) {
            println("Repositório: Erro ao excluir laboratório com ID '$id' - ${e.message}")
            Result.failure(e)
        }
    }
}

