package org.example.rachai.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.rachai.auth.SessionManager

/**
 * Plugin que injeta o header "Authorization: Bearer <token>" em toda
 * requisição autenticada.
 */
private val AuthHeaderPlugin = createClientPlugin("RachaiAuthHeader") {
    onRequest { request, _ ->
        SessionManager.token?.let { token ->
            request.header("Authorization", "Bearer $token")
        }
    }
}

object RachaiHttpClient {

    val instance: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = true
                        explicitNulls = false
                    }
                )
            }

            install(Logging) {
                level = LogLevel.INFO
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
                connectTimeoutMillis = 5_000
                socketTimeoutMillis = 10_000
            }

            install(AuthHeaderPlugin)
        }
    }
}
