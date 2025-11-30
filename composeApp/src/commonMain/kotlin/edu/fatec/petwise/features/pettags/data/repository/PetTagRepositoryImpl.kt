package edu.fatec.petwise.features.pettags.data.repository

import edu.fatec.petwise.features.pettags.data.datasource.RemotePetTagDataSource
import edu.fatec.petwise.features.pettags.domain.models.TagCheckInResult
import edu.fatec.petwise.features.pettags.domain.models.TagReadResult
import edu.fatec.petwise.features.pettags.domain.repository.PetTagRepository

class PetTagRepositoryImpl(
    private val remoteDataSource: RemotePetTagDataSource
) : PetTagRepository {

    override suspend fun startPairing(petId: String, readerId: String): Result<String> {
        return try {
            println("Repositório: Iniciando pareamento para pet $petId")
            val message = remoteDataSource.startPairing(petId, readerId)
            println("Repositório: Pareamento iniciado com sucesso")
            Result.success(message)
        } catch (e: Exception) {
            println("Repositório: Erro ao iniciar pareamento - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun checkIn(tagUid: String, readerId: String): Result<TagCheckInResult> {
        return try {
            println("Repositório: Realizando check-in com tag $tagUid")
            val result = remoteDataSource.checkIn(tagUid, readerId)
            println("Repositório: Check-in realizado com sucesso - Pet: ${result.petName}")
            Result.success(result)
        } catch (e: Exception) {
            println("Repositório: Erro no check-in - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getPetByTag(tagUid: String): Result<TagCheckInResult> {
        return try {
            println("Repositório: Buscando pet pela tag $tagUid")
            val result = remoteDataSource.getPetByTag(tagUid)
            println("Repositório: Pet encontrado - ${result.petName}")
            Result.success(result)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar pet pela tag - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getLastRead(): Result<TagReadResult> {
        return try {
            println("Repositório: Buscando última leitura")
            val result = remoteDataSource.getLastRead()
            println("Repositório: Última leitura obtida - Tag: ${result.tagUid}")
            Result.success(result)
        } catch (e: Exception) {
            println("Repositório: Erro ao buscar última leitura - ${e.message}")
            Result.failure(e)
        }
    }
}
