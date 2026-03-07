package com.example.notes_kmp

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

actual val coroutineContext: CoroutineContext
    get() = Dispatchers.IO

actual fun provideHttpClientEngine(): HttpClientEngineFactory<*> {
    return OkHttp
}