package edu.fatec.petwise.features.dashboard.domain.repository

import edu.fatec.petwise.core.network.dto.ConsultaDto

interface DashboardRepository {
    suspend fun getStatistics(): List<Number>
    suspend fun getUpcomingConsultas(): List<ConsultaDto>
}