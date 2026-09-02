package br.com.mykytadu.core.network

import io.ktor.client.plugins.logging.LogLevel
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

object HttpClientSettings {
    const val REQUEST_TIMEOUT_MILLIS = 30_000L
    const val CONNECT_TIMEOUT_MILLIS = 10_000L
    const val SOCKET_TIMEOUT_MILLIS = 30_000L

    val LOG_LEVEL = LogLevel.HEADERS
    val JSON_CONTENT_TYPE = ContentType.Application.Json.toString()

    internal val SENSITIVE_HEADERS = setOf(
        HttpHeaders.Authorization,
        HttpHeaders.Cookie,
        HttpHeaders.SetCookie,
    )
}

expect fun isHttpLoggingEnabled(): Boolean
