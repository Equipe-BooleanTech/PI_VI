package edu.fatec.petwise.features.vaccinations.data.repository

import edu.fatec.petwise.core.data.DataRefreshManager
import edu.fatec.petwise.features.vaccinations.data.datasource.RemoteVaccinationDataSource
import edu.fatec.petwise.features.vaccinations.domain.models.Vaccination
import edu.fatec.petwise.features.vaccinations.domain.models.VaccinationFilterOptions
import edu.fatec.petwise.features.vaccinations.domain.repository.VaccinationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock

class VaccinationRepositoryImpl(
    private val remoteDataSource: RemoteVaccinationDataSource
) : VaccinationRepository {

    override fun getAllVaccinations(): Flow<List<Vaccination>> = flow {
        try {
            println("Repositório: Buscando todas as vacinações via API")
            val vacinacoes = remoteDataSource.getAllVaccinations()
            println("Repositório: ${vacinacoes.size} vacinações carregadas com sucesso da API")
            emit(vacinacoes)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar vacinações da API - ${e.message}")
            throw e
        }
    }

    override fun getVaccinationById(id: String): Flow<Vaccination?> = flow {
        try {
            println("Repositório: Buscando vacinação por ID '$id' via API")
            val vacinacao = remoteDataSource.getVaccinationById(id)
            if (vacinacao != null) {
                println("Repositório: Vacinação '${vacinacao.vaccineName}' do pet '${vacinacao.petName}' encontrada")
            } else {
                println("Repositório: Vacinação com ID '$id' não encontrada")
            }
            emit(vacinacao)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar vacinação por ID '$id' - ${e.message}")
            throw e
        }
    }

    override fun getVaccinationsByPetId(petId: String): Flow<List<Vaccination>> = flow {
        try {
            println("Repositório: Buscando vacinações do pet '$petId' via API")
            val vacinacoes = remoteDataSource.getVaccinationsByPetId(petId)
            println("Repositório: ${vacinacoes.size} vacinações encontradas para o pet")
            emit(vacinacoes)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar vacinações do pet '$petId' - ${e.message}")
            throw e
        }
    }

    override fun filterVaccinations(options: VaccinationFilterOptions): Flow<List<Vaccination>> = flow {
        try {
            println("Repositório: Aplicando filtros nas vacinações via API")
            val vacinacoes = remoteDataSource.getAllVaccinations()
            val vacinacoesFiltradas = vacinacoes.filter { vaccination ->
                val petMatch = options.petId?.let { vaccination.petId == it } ?: true
                val typeMatch = options.vaccineType?.let { vaccination.vaccineType == it } ?: true
                val statusMatch = options.status?.let { vaccination.status == it } ?: true
                val searchMatch = if (options.searchQuery.isNotBlank()) {
                    vaccination.vaccineName.contains(options.searchQuery, ignoreCase = true) ||
                    vaccination.petName.contains(options.searchQuery, ignoreCase = true) ||
                    vaccination.veterinarianName.contains(options.searchQuery, ignoreCase = true)
                } else true

                petMatch && typeMatch && statusMatch && searchMatch
            }
            println("Repositório: Filtros aplicados - ${vacinacoesFiltradas.size} vacinações encontradas")
            emit(vacinacoesFiltradas)
        } catch (e: Exception) {
            println("Repositório: Erro ao filtrar vacinações - ${e.message}")
            throw e
        }
    }

    override fun getUpcomingVaccinations(days: Int): Flow<List<Vaccination>> = flow {
        try {
            println("Repositório: Buscando vacinações próximas (próximos $days dias) via API")
            val vacinacoesPendentes = remoteDataSource.getUpcomingVaccinations(days)
            println("Repositório: ${vacinacoesPendentes.size} vacinações próximas encontradas")
            emit(vacinacoesPendentes)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar vacinações próximas - ${e.message}")
            throw e
        }
    }

    override fun getOverdueVaccinations(): Flow<List<Vaccination>> = flow {
        try {
            println("Repositório: Buscando vacinações em atraso via API")
            val vacinacoesAtrasadas = remoteDataSource.getOverdueVaccinations()
            println("Repositório: ${vacinacoesAtrasadas.size} vacinações em atraso encontradas")
            emit(vacinacoesAtrasadas)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar vacinações em atraso - ${e.message}")
            throw e
        }
    }

    override suspend fun addVaccination(vaccination: Vaccination): Result<Vaccination> {
        return try {
            println("Repositório: Adicionando nova vacinação '${vaccination.vaccineName}' para pet '${vaccination.petName}' via API")
            val novaVacinacao = remoteDataSource.createVaccination(vaccination)
            println("Repositório: Vacinação '${novaVacinacao.vaccineName}' criada com sucesso - ID: ${novaVacinacao.id}")
            DataRefreshManager.notifyVaccinationsUpdated()
            Result.success(novaVacinacao)
        } catch (e: Exception) {
            println("Repositório: Erro ao criar vacinação '${vaccination.vaccineName}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateVaccination(vaccination: Vaccination): Result<Vaccination> {
        return try {
            println("Repositório: Atualizando vacinação '${vaccination.vaccineName}' (ID: ${vaccination.id}) via API")
            val vacinacaoAtualizada = remoteDataSource.updateVaccination(vaccination)
            println("Repositório: Vacinação '${vacinacaoAtualizada.vaccineName}' atualizada com sucesso")
            DataRefreshManager.notifyVaccinationUpdated(vacinacaoAtualizada.id)
            DataRefreshManager.notifyVaccinationsUpdated()
            Result.success(vacinacaoAtualizada)
        } catch (e: Exception) {
            println("Repositório: Erro ao atualizar vacinação '${vaccination.vaccineName}' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteVaccination(id: String): Result<Unit> {
        return try {
            println("Repositório: Excluindo vacinação '$id' via API")
            remoteDataSource.deleteVaccination(id)
            println("Repositório: Vacinação excluída com sucesso")
            DataRefreshManager.notifyVaccinationsUpdated()
            Result.success(Unit)
        } catch (e: Exception) {
            println("Repositório: Erro ao excluir vacinação '$id' - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun markAsApplied(id: String, observations: String): Result<Vaccination> {
        return try {
            println("Repositório: Marcando vacinação '$id' como aplicada via API")
            val vacinacaoAtualizada = remoteDataSource.markAsApplied(
                id = id,
                observations = observations,
                sideEffects = "",
                applicationDate = Clock.System.now().toEpochMilliseconds().toString()
            )
            println("Repositório: Vacinação '${vacinacaoAtualizada.vaccineName}' marcada como aplicada com sucesso")
            DataRefreshManager.notifyVaccinationUpdated(vacinacaoAtualizada.id)
            DataRefreshManager.notifyVaccinationsUpdated()
            Result.success(vacinacaoAtualizada)
        } catch (e: Exception) {
            println("Repositório: Erro ao marcar vacinação '$id' como aplicada - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun scheduleNextDose(id: String, nextDoseDate: String): Result<Vaccination> {
        return try {
            println("Repositório: Agendando próxima dose da vacinação '$id' para '$nextDoseDate' via API")
            val vacinacaoAtualizada = remoteDataSource.scheduleNextDose(id, nextDoseDate)
            println("Repositório: Próxima dose da vacinação '${vacinacaoAtualizada.vaccineName}' agendada com sucesso")
            DataRefreshManager.notifyVaccinationUpdated(vacinacaoAtualizada.id)
            DataRefreshManager.notifyVaccinationsUpdated()
            Result.success(vacinacaoAtualizada)
        } catch (e: Exception) {
            println("Repositório: Erro ao agendar próxima dose da vacinação '$id' - ${e.message}")
            Result.failure(e)
        }
    }
}
