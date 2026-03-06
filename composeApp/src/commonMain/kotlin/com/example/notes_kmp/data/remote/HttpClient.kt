package com.example.notes_kmp.data.remote

import com.example.notes_kmp.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun setupHttpClient(
    baseUrl: String,
    isDebugMode: Boolean = false,
    httpClientProvider: HttpClient
): HttpClient {
    return httpClientProvider.config {
        expectSuccess = false

        install(WebSockets) {
            pingIntervalMillis = 20_000
            maxFrameSize = Long.MAX_VALUE
            contentConverter = KotlinxWebsocketSerializationConverter(
                createJsonParser()
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            connectTimeoutMillis = 60_000
            socketTimeoutMillis = 60_000
        }

        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
                prettyPrint = true
                useAlternativeNames = false
                explicitNulls = false
            })
        }

        install(HttpRedirect) {
            checkHttpMethod = false
            allowHttpsDowngrade = false
        }

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = baseUrl
                parameters.append("apiKey", Constants.API_KEY)
            }

            header("Accept", "application/json")
            header("Content-Type", "application/json")
        }

        if (isDebugMode) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("Ktor: $message")
                    }
                }
                level = LogLevel.ALL
            }
        }
    }
}

fun createJsonParser(): Json {
    return Json {
        isLenient = true
        ignoreUnknownKeys = true
        prettyPrint = true
        useAlternativeNames = false
        explicitNulls = false
    }
}
