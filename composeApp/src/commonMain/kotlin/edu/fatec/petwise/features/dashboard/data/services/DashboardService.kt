package edu.fatec.petwise.features.dashboard.data.services

import edu.fatec.petwise.core.network.dto.ConsultaDto
import edu.fatec.petwise.features.dashboard.data.datasource.RemoteDashboardDataSource
import edu.fatec.petwise.features.dashboard.domain.repository.DashboardRepository

class DashboardService(
    private val remoteDataSource: RemoteDashboardDataSource
) : DashboardRepository {

    override suspend fun getStatistics(): List<Number> = remoteDataSource.getStatistics()

    override suspend fun getUpcomingConsultas(): List<ConsultaDto> = remoteDataSource.getUpcomingConsultas()
}