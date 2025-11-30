package edu.fatec.petwise.core.network.api

import edu.fatec.petwise.core.network.ApiEndpoints
import edu.fatec.petwise.core.network.NetworkRequestHandler
import edu.fatec.petwise.core.network.NetworkResult
import edu.fatec.petwise.core.network.dto.*
import io.ktor.client.request.*

interface IotApiService {
    suspend fun startPairing(request: StartPairingRequest): NetworkResult<MessageResponse>
    suspend fun checkIn(request: IotCheckInRequest): NetworkResult<IotCheckInResponse>
    suspend fun getPetByTag(tagUid: String): NetworkResult<IotCheckInResponse>
    suspend fun getLastRead(): NetworkResult<LastTagReadResponse>
}

class IotApiServiceImpl(
    private val networkHandler: NetworkRequestHandler
) : IotApiService {

    override suspend fun startPairing(request: StartPairingRequest): NetworkResult<MessageResponse> {
        println("IotApiService: Iniciando pareamento para petId: ${request.petId}")
        return networkHandler.post<MessageResponse, StartPairingRequest>(
            urlString = ApiEndpoints.IOT_START_PAIRING,
            body = request
        )
    }

    override suspend fun checkIn(request: IotCheckInRequest): NetworkResult<IotCheckInResponse> {
        println("IotApiService: Realizando check-in com tag: ${request.tagUid}")
        return networkHandler.post<IotCheckInResponse, IotCheckInRequest>(
            urlString = ApiEndpoints.IOT_CHECK_IN,
            body = request
        )
    }

    override suspend fun getPetByTag(tagUid: String): NetworkResult<IotCheckInResponse> {
        println("IotApiService: Buscando pet pela tag: $tagUid")
        return networkHandler.get<IotCheckInResponse>(ApiEndpoints.getPetByTag(tagUid))
    }

    override suspend fun getLastRead(): NetworkResult<LastTagReadResponse> {
        println("IotApiService: Buscando última leitura de tag")
        return networkHandler.get<LastTagReadResponse>(ApiEndpoints.IOT_LAST_READ)
    }
}
