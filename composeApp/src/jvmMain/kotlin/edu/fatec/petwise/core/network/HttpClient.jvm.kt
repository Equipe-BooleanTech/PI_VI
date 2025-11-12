package edu.fatec.petwise.core.network

import io.ktor.client.*
import io.ktor.client.engine.java.*
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.UUID
import java.util.concurrent.Executors

actual fun createHttpClient(config: PetWiseHttpClientConfig): HttpClient {
    return HttpClient(Java) {
        engine {
            pipelining = true
            dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
        }
    }
}

actual fun getUserAgent(): String {
    val javaVersion = System.getProperty("java.version")
    val osName = System.getProperty("os.name")
    val osVersion = System.getProperty("os.version")
    return "PetWise-Desktop/$javaVersion ($osName $osVersion)"
}

actual fun getPlatformName(): String = "JVM"

actual fun generateRequestId(): String = UUID.randomUUID().toString()
