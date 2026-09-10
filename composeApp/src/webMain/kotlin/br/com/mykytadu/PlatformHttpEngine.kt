package br.com.mykytadu.di

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

actual fun provideHttpClientEngine(): HttpClientEngineFactory<*> = Js
