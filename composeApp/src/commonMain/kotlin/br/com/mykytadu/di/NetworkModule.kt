package br.com.mykytadu.di

import br.com.mykytadu.core.network.HttpClientSettings
import br.com.mykytadu.core.network.isHttpLoggingEnabled
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.onClose
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import br.com.mykytadu.data.remote.anilist.AniListAnimeApi
import br.com.mykytadu.data.remote.api.AnimeApi
// Platform-specific engine factory provider (actual implementations per platform)
expect fun provideHttpClientEngine(): HttpClientEngineFactory<*>

val NetworkModule = module {
    single<HttpClient> {
        HttpClient(provideHttpClientEngine()) {
            configureSharedHttpClient()
        }
    } withOptions {
        onClose { client -> client?.close() }
    }

    single<AnimeApi> {
        AniListAnimeApi(httpClient = get())
    }
}

internal fun HttpClientConfig<*>.configureSharedHttpClient(
    networkLogger: Logger = Logger.SIMPLE,
    loggingEnabled: Boolean = isHttpLoggingEnabled(),
) {
    expectSuccess = true

    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }

    install(HttpTimeout) {
        requestTimeoutMillis = HttpClientSettings.REQUEST_TIMEOUT_MILLIS
        connectTimeoutMillis = HttpClientSettings.CONNECT_TIMEOUT_MILLIS
        socketTimeoutMillis = HttpClientSettings.SOCKET_TIMEOUT_MILLIS
    }

    install(Logging) {
        logger = networkLogger
        level = if (loggingEnabled) HttpClientSettings.LOG_LEVEL else LogLevel.NONE
        sanitizeHeader { header ->
            HttpClientSettings.SENSITIVE_HEADERS.any { sensitiveHeader ->
                header.equals(sensitiveHeader, ignoreCase = true)
            }
        }
    }

    defaultRequest {
        headers.append(HttpHeaders.Accept, HttpClientSettings.JSON_CONTENT_TYPE)
        headers.append(HttpHeaders.ContentType, HttpClientSettings.JSON_CONTENT_TYPE)
    }
}
