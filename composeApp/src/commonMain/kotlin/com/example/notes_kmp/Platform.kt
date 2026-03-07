package com.example.notes_kmp

import io.ktor.client.engine.HttpClientEngineFactory
import kotlin.coroutines.CoroutineContext

expect val coroutineContext : CoroutineContext

expect fun provideHttpClientEngine(): HttpClientEngineFactory<*>