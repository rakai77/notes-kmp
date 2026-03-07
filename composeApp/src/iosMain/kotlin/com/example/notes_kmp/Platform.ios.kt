package com.example.notes_kmp

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.Dispatchers
import platform.UIKit.UIDevice
import kotlin.coroutines.CoroutineContext

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override fun getHttpClient(isFromMultipart: Boolean): HttpClient {
        return HttpClient(Darwin) {
            engine {
                configureRequest {
                    setAllowsCellularAccess(true)
                }
            }
        }
    }
}

actual fun getPlatform(): Platform = IOSPlatform()
actual val coroutineContext: CoroutineContext
    get() = Dispatchers.Default