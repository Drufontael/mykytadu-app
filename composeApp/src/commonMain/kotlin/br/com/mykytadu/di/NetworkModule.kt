package br.com.mykytadu.di

import org.koin.dsl.module
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// Platform-specific engine factory provider (actual implementations per platform)
expect fun provideHttpClientEngine(): HttpClientEngineFactory<*>

val NetworkModule = module {
    single<HttpClient> {
        HttpClient(provideHttpClientEngine()) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
}
