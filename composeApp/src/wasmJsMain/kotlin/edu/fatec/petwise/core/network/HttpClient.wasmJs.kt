package edu.fatec.petwise.core.network

import io.ktor.client.*
import io.ktor.client.engine.js.*
import kotlin.random.Random

actual fun createHttpClient(config: PetWiseHttpClientConfig): HttpClient {
    return HttpClient(Js) {
        engine {
        }
    }
}

actual fun getUserAgent(): String {
    return "PetWise-Wasm/1.0"
}

actual fun getPlatformName(): String = "WasmJS"

actual fun getPlatformApiUrl(): String = "http://localhost:8081"

actual fun generateRequestId(): String {
    val timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
    val random = Random.nextInt(10000, 99999)
    return "$timestamp-$random"
}
