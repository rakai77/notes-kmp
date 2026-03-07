package com.example.notes_kmp

import io.ktor.client.HttpClient
import kotlin.coroutines.CoroutineContext

interface Platform {
    val name: String
    fun getHttpClient(isFromMultipart: Boolean): HttpClient
}

expect fun getPlatform(): Platform

expect val coroutineContext : CoroutineContext