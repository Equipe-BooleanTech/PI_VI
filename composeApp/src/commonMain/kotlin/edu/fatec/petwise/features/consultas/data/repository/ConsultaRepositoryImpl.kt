package edu.fatec.petwise.features.consultas.data.repository

import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.features.consultas.data.datasource.RemoteConsultaDataSourceImpl
import edu.fatec.petwise.features.consultas.domain.models.Consulta
import edu.fatec.petwise.features.consultas.domain.models.ConsultaFilterOptions
import edu.fatec.petwise.features.consultas.domain.models.ConsultaStatus
import edu.fatec.petwise.features.consultas.domain.repository.ConsultaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ConsultaRepositoryImpl(
    private val remoteDataSource: RemoteConsultaDataSourceImpl
) : ConsultaRepository {

    override fun getAllConsultas(): Flow<List<Consulta>> = flow {
        try {
            println("Repositório: Buscando todas as consultas via API")
            val consultas = remoteDataSource.getAllConsultas()
            println("Repositório: ${consultas.size} consultas carregadas com sucesso da API")
            emit(consultas)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar consultas da API - ${e.message}")
            throw e
        }
    }

    override fun getConsultaById(id: String): Flow<Consulta?> = flow {
        try {
            println("Repositório: Buscando consulta por ID '$id' via API")
            val consulta = remoteDataSource.getConsultaById(id)
            if (consulta != null) {
                println("Repositório: Consulta do pet '${consulta.petName}' encontrada com sucesso")
            } else {
                println("Repositório: Consulta com ID '$id' não encontrada")
            }
            emit(consulta)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar consulta por ID '$id' - ${e.message}")
            throw e
        }
    }

    override fun getConsultasByPetId(petId: String): Flow<List<Consulta>> = flow {
        try {
            println("Repositório: Buscando consultas do pet '$petId' via API")
            val consultas = remoteDataSource.getConsultasByPet(petId)
            println("Repositório: ${consultas.size} consultas encontradas para o pet")
            emit(consultas)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar consultas do pet '$petId' - ${e.message}")
            throw e
        }
    }

    override fun searchConsultas(query: String): Flow<List<Consulta>> = flow {
        try {
            println("Repositório: Buscando consultas com consulta '$query' via API")
            val consultas = remoteDataSource.searchConsultas(query)
            println("Repositório: ${consultas.size} consultas encontradas na busca")
            emit(consultas)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar consultas - ${e.message}")
            throw e
        }
    }

    override fun filterConsultas(options: ConsultaFilterOptions): Flow<List<Consulta>> = flow {
        try {
            println("Repositório: Aplicando filtros nas consultas via API")
            val consultas = remoteDataSource.getAllConsultas()
            val consultasFiltradas = consultas.filter { consulta ->
                val typeMatch = options.consultaType?.let { consulta.consultaType == it } ?: true
                val statusMatch = options.status?.let { consulta.status == it } ?: true
                val petMatch = options.petId?.let { consulta.petId == it } ?: true
                val searchMatch = if (options.searchQuery.isNotBlank()) {
                    consulta.petName.contains(options.searchQuery, ignoreCase = true) ||
                    consulta.veterinarianName.contains(options.searchQuery, ignoreCase = true) ||
                    consulta.ownerName.contains(options.searchQuery, ignoreCase = true)
                } else true

                typeMatch && statusMatch && petMatch && searchMatch
            }
            println("Repositório: Filtros aplicados - ${consultasFiltradas.size} consultas encontradas")
            emit(consultasFiltradas)
        } catch (e: Exception) {
            println("Repositório: Erro ao filtrar consultas - ${e.message}")
            throw e
        }
    }

    override fun getUpcomingConsultas(): Flow<List<Consulta>> = flow {
        try {
            println("Repositório: Buscando consultas próximas via API")
            val consultas = remoteDataSource.getUpcomingConsultas()
            println("Repositório: ${consultas.size} consultas próximas encontradas")
            emit(consultas.sortedBy { it.consultaDate })
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar consultas próximas - ${e.message}")
            throw e
        }
    }

    override fun getConsultasByStatus(status: ConsultaStatus): Flow<List<Consulta>> = flow {
        try {
            println("Repositório: Buscando consultas com status '${status.displayName}' via API")
            val todasConsultas = remoteDataSource.getAllConsultas()
            val consultasFiltradas = todasConsultas.filter { it.status == status }
            println("Repositório: ${consultasFiltradas.size} consultas com status '${status.displayName}' encontradas")
            emit(consultasFiltradas)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar consultas por status - ${e.message}")
            throw e
        }
    }

    override suspend fun addConsulta(consulta: Consulta): Result<Consulta> {
        return try {
            println("Repositório: Adicionando nova consulta para pet '${consulta.petName}' via API")
            val novaConsulta = remoteDataSource.createConsulta(consulta)
            println("Repositório: Consulta para '${novaConsulta.petName}' criada com sucesso - ID: ${novaConsulta.id}")
            DataRefreshManager.notifyConsultasUpdated()
            Result.success(novaConsulta)
        } catch (e: Exception) {
            println("Repositório: Erro ao criar consulta para '${consulta.petName}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateConsulta(consulta: Consulta): Result<Consulta> {
        return try {
            println("Repositório: Atualizando consulta '${consulta.id}' do pet '${consulta.petName}' via API")
            val consultaAtualizada = remoteDataSource.updateConsulta(consulta)
            println("Repositório: Consulta do pet '${consultaAtualizada.petName}' atualizada com sucesso")
            DataRefreshManager.notifyConsultaUpdated(consultaAtualizada.id)
            DataRefreshManager.notifyConsultasUpdated()
            Result.success(consultaAtualizada)
        } catch (e: Exception) {
            println("Repositório: Erro ao atualizar consulta '${consulta.id}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteConsulta(id: String): Result<Unit> {
        return try {
            println("Repositório: Excluindo consulta '$id' via API")
            remoteDataSource.deleteConsulta(id)
            println("Repositório: Consulta excluída com sucesso")
            DataRefreshManager.notifyConsultasUpdated()
            Result.success(Unit)
        } catch (e: Exception) {
            println("Repositório: Erro ao excluir consulta '$id' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateConsultaStatus(id: String, status: ConsultaStatus): Result<Consulta> {
        return try {
            println("Repositório: Atualizando status da consulta '$id' para '${status.displayName}' via API")
            val consultaAtualizada = remoteDataSource.updateConsultaStatus(id, status)
            println("Repositório: Status da consulta atualizado para '${status.displayName}' com sucesso")
            DataRefreshManager.notifyConsultaUpdated(consultaAtualizada.id)
            DataRefreshManager.notifyConsultasUpdated()
            Result.success(consultaAtualizada)
        } catch (e: Exception) {
            println("Repositório: Erro ao atualizar status da consulta '$id' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun markAsPaid(id: String): Result<Consulta> {
        return try {
            println("Repositório: Marcando consulta '$id' como paga via API")
            val consulta = remoteDataSource.getConsultaById(id)
            if (consulta != null) {
                val consultaAtualizada = consulta.copy(isPaid = true)
                val resultado = remoteDataSource.updateConsulta(consultaAtualizada)
                println("Repositório: Consulta marcada como paga com sucesso")
                DataRefreshManager.notifyConsultaUpdated(resultado.id)
                DataRefreshManager.notifyConsultasUpdated()
                Result.success(resultado)
            } else {
                Result.failure(IllegalArgumentException("Consulta não encontrada"))
            }
        } catch (e: Exception) {
            println("Repositório: Erro ao marcar consulta '$id' como paga - ${e.message}")
            Result.failure(e)
        }
    }
}
