package com.example.notes_kmp

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext


actual val coroutineContext: CoroutineContext
    get() = Dispatchers.Default

actual fun provideHttpClientEngine(): HttpClientEngineFactory<*> {
    return Darwin
}