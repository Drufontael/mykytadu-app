package br.com.mykytadu.di

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

actual fun provideHttpClientEngine(): HttpClientEngineFactory<*> = CIO
