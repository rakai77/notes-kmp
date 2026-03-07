package com.example.notes_kmp.data.remote

import com.example.notes_kmp.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun setupHttpClient(
    engine: HttpClientEngineFactory<*>
) = HttpClient(engine) {
    expectSuccess = false

    install(HttpTimeout) {
        socketTimeoutMillis = 30000
        connectTimeoutMillis = 30000
        requestTimeoutMillis = 30000
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
            host = Constants.BASE_URL
            parameters.append("apiKey", Constants.API_KEY)

        }
        header("Accept", "application/json")
        header("Content-Type", "application/json")
    }

    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.ALL
    }
}
