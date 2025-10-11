package edu.fatec.petwise.features.consultas.data.datasource

import edu.fatec.petwise.features.consultas.domain.models.Consulta
import kotlinx.coroutines.flow.Flow

interface LocalConsultaDataSource {
    fun getAllConsultas(): Flow<List<Consulta>>
    fun getConsultaById(id: String): Flow<Consulta?>
    suspend fun insertConsulta(consulta: Consulta)
    suspend fun updateConsulta(consulta: Consulta)
    suspend fun deleteConsulta(id: String)
}

interface RemoteConsultaDataSource {
    suspend fun getAllConsultas(): List<Consulta>
    suspend fun getConsultaById(id: String): Consulta?
    suspend fun createConsulta(consulta: Consulta): Consulta
    suspend fun updateConsulta(consulta: Consulta): Consulta
    suspend fun deleteConsulta(id: String)
}
