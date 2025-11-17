package edu.fatec.petwise.features.dashboard.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.ConsultaApiService
import edu.fatec.petwise.core.network.api.PetApiService
import edu.fatec.petwise.core.network.dto.ConsultaDto

class DashboardDataSourceImpl (
    private val petApiService: PetApiService,
    private val consultaApiService: ConsultaApiService,
    // Adicionar vacina
    // Adicionar medicação
): RemoteDashboardDataSource {
    override suspend fun getStatistics(): List<Number> {
        var totalPets = 0
        var totalConsultas = 0
        
        when (val petsResult = petApiService.getAllPets(1, 1000)) {
            is NetworkResult.Success -> {
                totalPets = petsResult.data.size
            }
            is NetworkResult.Error -> {
                println("API: Erro ao buscar pets - ${petsResult.exception.message}")
                throw petsResult.exception
            }
            is NetworkResult.Loading -> { }
        }

        when (val consultasResult = consultaApiService.getAllConsultas(1, 1000)) {
            is NetworkResult.Success -> {
                totalConsultas = consultasResult.data.size
            }
            is NetworkResult.Error -> {
                println("API: Erro ao buscar consultas - ${consultasResult.exception.message}")
                throw consultasResult.exception
            }
            is NetworkResult.Loading -> { }
        }
        
        return listOf(totalPets, totalConsultas)
    }

    override suspend fun getUpcomingConsultas(): List<ConsultaDto> {
        var consultas = listOf<ConsultaDto>()
        when  (val result = consultaApiService.getUpcomingConsultas(1, 1000)) {
            is NetworkResult.Success -> {
                consultas = result.data
            }
            is NetworkResult.Error -> {
                println("API: Erro ao buscar pets - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> null
        }
        return consultas
    }

}