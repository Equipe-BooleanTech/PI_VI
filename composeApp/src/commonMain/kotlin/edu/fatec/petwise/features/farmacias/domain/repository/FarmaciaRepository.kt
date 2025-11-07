package edu.fatec.petwise.features.farmacias.domain.repository

import edu.fatec.petwise.features.farmacias.domain.models.Farmacia
import edu.fatec.petwise.features.farmacias.domain.models.FarmaciaFilterOptions
import kotlinx.coroutines.flow.Flow

/**
 * Interface do repositório de Farmácias.
 * 
 * Define o contrato para acesso aos dados de farmácias,
 * abstraindo a fonte de dados (remota, local, cache).
 * 
 * Todas as operações retornam Flow para suportar dados reativos
 * ou Result para operações pontuais com tratamento de erro.
 */
interface FarmaciaRepository {

    /**
     * Obtém todas as farmácias cadastradas.
     */
    fun getAllFarmacias(): Flow<List<Farmacia>>

    /**
     * Obtém uma farmácia específica por ID.
     */
    suspend fun getFarmaciaById(id: String): Result<Farmacia>

    /**
     * Cria uma nova farmácia.
     */
    suspend fun createFarmacia(farmacia: Farmacia): Result<Farmacia>

    /**
     * Atualiza uma farmácia existente.
     */
    suspend fun updateFarmacia(id: String, farmacia: Farmacia): Result<Farmacia>

    /**
     * Remove uma farmácia.
     */
    suspend fun deleteFarmacia(id: String): Result<Unit>

    /**
     * Filtra farmácias por critérios específicos.
     */
    fun filterFarmacias(options: FarmaciaFilterOptions): Flow<List<Farmacia>>

    /**
     * Busca farmácias por cidade.
     */
    fun getFarmaciasByCidade(cidade: String): Flow<List<Farmacia>>

    /**
     * Busca farmácias por estado.
     */
    fun getFarmaciasByEstado(estado: String): Flow<List<Farmacia>>

    /**
     * Obtém apenas farmácias ativas.
     */
    fun getFarmaciasAtivas(): Flow<List<Farmacia>>

    /**
     * Atualiza o limite de crédito de uma farmácia.
     */
    suspend fun updateLimiteCredito(id: String, novoLimite: Double): Result<Farmacia>

    /**
     * Atualiza o status de uma farmácia.
     */
    suspend fun updateStatus(id: String, novoStatus: String, motivo: String? = null): Result<Farmacia>

    /**
     * Obtém farmácias que oferecem frete grátis.
     */
    fun getFarmaciasComFreteGratis(): Flow<List<Farmacia>>
}
