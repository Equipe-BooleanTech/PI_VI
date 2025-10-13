package edu.fatec.petwise.core.network

import io.ktor.client.*
import io.ktor.client.engine.js.*
import kotlin.js.Date
import kotlin.random.Random

actual fun createHttpClient(config: PetWiseHttpClientConfig): HttpClient {
    return HttpClient(Js) {
        engine {
        }
    }
}

actual fun getUserAgent(): String {
    return try {
        js("navigator.userAgent") as? String ?: "PetWise-Web/Unknown"
    } catch (e: Exception) {
        "PetWise-Web/Unknown"
    }
}

actual fun getPlatformName(): String = "Web"

actual fun generateRequestId(): String {
    val timestamp = Date.now().toLong()
    val random = Random.nextInt(10000, 99999)
    return "$timestamp-$random"
}
