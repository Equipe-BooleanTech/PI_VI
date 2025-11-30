package edu.fatec.petwise.core.network

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import platform.Foundation.NSUUID

actual fun createHttpClient(config: PetWiseHttpClientConfig): HttpClient {
    return HttpClient(Darwin) {
        engine {
            configureRequest {
                setTimeoutInterval(config.requestTimeout / 1000.0)
            }
            
            handleChallenge { session, task, challenge, completionHandler ->
                completionHandler(0, null)
            }
        }
    }
}

actual fun getUserAgent(): String {
    return "PetWise-iOS/${platform.Foundation.NSProcessInfo.processInfo.operatingSystemVersionString}"
}

actual fun getPlatformName(): String = "iOS"

actual fun getPlatformApiUrl(): String = "http://localhost:8080"

actual fun generateRequestId(): String = NSUUID().UUIDString()
