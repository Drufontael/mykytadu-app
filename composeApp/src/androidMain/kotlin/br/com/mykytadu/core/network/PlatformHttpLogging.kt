package br.com.mykytadu.core.network

import br.com.mykytadu.BuildConfig

actual fun isHttpLoggingEnabled(): Boolean = BuildConfig.DEBUG
