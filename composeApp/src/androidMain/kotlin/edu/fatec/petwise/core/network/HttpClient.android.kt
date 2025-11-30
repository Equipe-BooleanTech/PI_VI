package edu.fatec.petwise.core.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import java.util.UUID


actual fun createHttpClient(config: PetWiseHttpClientConfig): HttpClient {
    return HttpClient(Android) {
        engine {
            connectTimeout = config.connectTimeout.toInt()
            socketTimeout = config.socketTimeout.toInt()
            pipelining = true
        }
    }
}

actual fun getUserAgent(): String {
    return "PetWise-Android/${android.os.Build.VERSION.RELEASE}"
}

actual fun getPlatformName(): String = "Android"

actual fun getPlatformApiUrl(): String = "http://10.0.2.2:8080"

actual fun generateRequestId(): String = UUID.randomUUID().toString()
