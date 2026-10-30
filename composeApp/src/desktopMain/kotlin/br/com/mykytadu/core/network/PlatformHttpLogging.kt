package br.com.mykytadu.core.network

actual fun isHttpLoggingEnabled(): Boolean =
    System.getProperty(HTTP_LOGGING_PROPERTY)?.toBooleanStrictOrNull() ?: false

private const val HTTP_LOGGING_PROPERTY = "mykytadu.http.logging"
