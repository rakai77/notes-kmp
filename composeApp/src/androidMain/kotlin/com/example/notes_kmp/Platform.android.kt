package com.example.notes_kmp

import android.os.Build
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override fun getHttpClient(isFromMultipart: Boolean): HttpClient {
        return if (isFromMultipart) HttpClient(CIO) else HttpClient(OkHttp) {
            engine {
                config {
                    retryOnConnectionFailure(true)
                    followRedirects(true)
                }
            }
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()
actual val coroutineContext: CoroutineContext
    get() = Dispatchers.IO