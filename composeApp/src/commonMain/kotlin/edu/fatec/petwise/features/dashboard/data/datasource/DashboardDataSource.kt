package edu.fatec.petwise.features.dashboard.data.datasource

import edu.fatec.petwise.core.network.dto.ConsultaDto

interface RemoteDashboardDataSource {
    suspend fun getStatistics(): List<Number>
    suspend fun getUpcomingConsultas(): List<ConsultaDto>
}