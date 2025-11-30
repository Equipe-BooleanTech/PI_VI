package edu.fatec.petwise.features.pettags.data.datasource

import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.api.IotApiService
import edu.fatec.petwise.core.network.dto.IotCheckInRequest
import edu.fatec.petwise.core.network.dto.StartPairingRequest
import edu.fatec.petwise.features.pettags.domain.models.TagCheckInResult
import edu.fatec.petwise.features.pettags.domain.models.TagReadResult

class RemotePetTagDataSourceImpl(
    private val iotApiService: IotApiService
) : RemotePetTagDataSource {

    override suspend fun startPairing(petId: String, readerId: String): String {
        println("PetTagDataSource: Iniciando pareamento para pet $petId no leitor $readerId")
        val request = StartPairingRequest(petId = petId, readerId = readerId)
        
        return when (val result = iotApiService.startPairing(request)) {
            is NetworkResult.Success -> {
                println("PetTagDataSource: Pareamento iniciado com sucesso - ${result.data.message}")
                result.data.message
            }
            is NetworkResult.Error -> {
                println("PetTagDataSource: Erro ao iniciar pareamento - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun checkIn(tagUid: String, readerId: String): TagCheckInResult {
        println("PetTagDataSource: Realizando check-in com tag $tagUid")
        val request = IotCheckInRequest(tagUid = tagUid, readerId = readerId)
        
        return when (val result = iotApiService.checkIn(request)) {
            is NetworkResult.Success -> {
                println("PetTagDataSource: Check-in realizado - Pet: ${result.data.petName}")
                TagCheckInResult(
                    petId = result.data.petId,
                    petName = result.data.petName,
                    ownerName = result.data.ownerName,
                    species = result.data.species,
                    ownerPhone = result.data.ownerPhone,
                    message = result.data.message
                )
            }
            is NetworkResult.Error -> {
                println("PetTagDataSource: Erro no check-in - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun getPetByTag(tagUid: String): TagCheckInResult {
        println("PetTagDataSource: Buscando pet pela tag $tagUid")
        
        return when (val result = iotApiService.getPetByTag(tagUid)) {
            is NetworkResult.Success -> {
                println("PetTagDataSource: Pet encontrado - ${result.data.petName}")
                TagCheckInResult(
                    petId = result.data.petId,
                    petName = result.data.petName,
                    ownerName = result.data.ownerName,
                    species = result.data.species,
                    ownerPhone = result.data.ownerPhone,
                    message = result.data.message
                )
            }
            is NetworkResult.Error -> {
                println("PetTagDataSource: Erro ao buscar pet pela tag - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }

    override suspend fun getLastRead(): TagReadResult {
        println("PetTagDataSource: Buscando última leitura")
        
        return when (val result = iotApiService.getLastRead()) {
            is NetworkResult.Success -> {
                println("PetTagDataSource: Última leitura obtida - Tag: ${result.data.tagUid}")
                TagReadResult(
                    tagUid = result.data.tagUid,
                    readerId = result.data.readerId,
                    timestamp = result.data.timestamp,
                    petId = result.data.petId,
                    petName = result.data.petName,
                    ownerName = result.data.ownerName,
                    species = result.data.species,
                    message = result.data.message
                )
            }
            is NetworkResult.Error -> {
                println("PetTagDataSource: Erro ao buscar última leitura - ${result.exception.message}")
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Requisição em andamento")
        }
    }
}
