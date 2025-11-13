package edu.fatec.petwise.features.toys.data.repository

import edu.fatec.petwise.features.toys.data.datasource.RemoteToyDataSource
import edu.fatec.petwise.features.toys.domain.models.Toy
import edu.fatec.petwise.features.toys.domain.repository.ToyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ToyRepositoryImpl(
    private val remoteDataSource: RemoteToyDataSource
) : ToyRepository {

    override fun getAllToys(): Flow<List<Toy>> = flow {
        try {
            println("Repositório: Buscando todos os brinquedos via API")
            val toys = remoteDataSource.getAllToys()
            println("Repositório: ${toys.size} brinquedos carregados com sucesso da API")
            emit(toys)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar brinquedos da API - ${e.message}")
                throw e
        }
    }

    override fun getToyById(id: String): Flow<Toy?> = flow {
        try {
            println("Repositório: Buscando brinquedo por ID '$id' via API")
            val toy = remoteDataSource.getToyById(id)
            if (toy != null) {
                println("Repositório: Brinquedo '${toy.name}' encontrado com sucesso")
            } else {
                println("Repositório: Brinquedo com ID '$id' não encontrado")
            }
            emit(toy)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar brinquedo por ID '$id' - ${e.message}")
                throw e
        }
    }

    override fun searchToys(query: String): Flow<List<Toy>> = flow {
        try {
            println("Repositório: Iniciando busca de brinquedos com consulta '$query'")
            val toys = remoteDataSource.searchToys(query)
            println("Repositório: Busca concluída - ${toys.size} brinquedos encontrados")
            emit(toys)
        } catch (e: Exception) {
                println("Repositório: Erro ao buscar brinquedos na API - ${e.message}")
                throw e
            }
    }

    override fun getToysByCategory(category: String): Flow<List<Toy>> = flow {
        try {
            println("Repositório: Buscando brinquedos da categoria '$category' via API")
            val toys = remoteDataSource.getToysByCategory(category)
            println("Repositório: ${toys.size} brinquedos encontrados")
            emit(toys)
        } catch (e: Exception) {

                println("Repositório: Erro ao buscar brinquedos da categoria - ${e.message}")
                throw e
            }
    }

    override suspend fun addToy(toy: Toy): Result<Toy> {
        return try {
            println("Repositório: Adicionando novo brinquedo '${toy.name}' via API")
            val createdToy = remoteDataSource.createToy(toy)
            println("Repositório: Brinquedo '${createdToy.name}' criado com sucesso - ID: ${createdToy.id}")
            Result.success(createdToy)
        } catch (e: Exception) {
            println("Repositório: Erro ao criar brinquedo '${toy.name}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateToy(toy: Toy): Result<Toy> {
        return try {
            println("Repositório: Atualizando brinquedo '${toy.name}' (ID: ${toy.id}) via API")
            val updatedToy = remoteDataSource.updateToy(toy)
            println("Repositório: Brinquedo '${updatedToy.name}' atualizado com sucesso")
            Result.success(updatedToy)
        } catch (e: Exception) {
            println("Repositório: Erro ao atualizar brinquedo '${toy.name}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteToy(id: String): Result<Unit> {
        return try {
            println("Repositório: Excluindo brinquedo com ID '$id' via API")
            remoteDataSource.deleteToy(id)
            println("Repositório: Brinquedo excluído com sucesso")
            Result.success(Unit)
        } catch (e: Exception) {
            println("Repositório: Erro ao excluir brinquedo com ID '$id' - ${e.message}")
            Result.failure(e)
        }
    }
}

