package edu.fatec.petwise.features.consultas.data.datasource

import edu.fatec.petwise.features.consultas.domain.models.Consulta

interface RemoteConsultaDataSource {
    suspend fun getAllConsultas(): List<Consulta>
    suspend fun getConsultaById(id: String): Consulta?
    suspend fun createConsulta(consulta: Consulta): Consulta
    suspend fun updateConsulta(consulta: Consulta): Consulta
    suspend fun deleteConsulta(id: String)
}
