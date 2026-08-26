package br.com.mykytadu.di

import io.ktor.client.*
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.koinApplication
import org.koin.mp.KoinPlatformTools
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class NetworkModuleTest {

    @Test
    fun `deve registrar HttpClient no Koin`() {
        startKoin {
            modules(NetworkModule)
        }
        val httpClient = KoinPlatformTools.defaultContext().get().get<HttpClient>()
        assertNotNull(httpClient)
        stopKoin()
    }

    @Test
    fun `deve resolver HttpClient pelo koin`() {
        val koinApplication: KoinApplication = koinApplication {
            modules(NetworkModule)
        }
        val koin = koinApplication.koin
        val httpClient = koin.get<HttpClient>()
        assertNotNull(httpClient)
        koinApplication.close()
    }
    @Test
    fun `deve retornar a mesma instancia de HttpClient`() {
        val koinApplication: KoinApplication = koinApplication {
            modules(NetworkModule)
        }
        val koin = koinApplication.koin
        val httpClient1 = koin.get<HttpClient>()
        val httpClient2 = koin.get<HttpClient>()
        assertSame(httpClient1, httpClient2)
        koinApplication.close()
    }
}