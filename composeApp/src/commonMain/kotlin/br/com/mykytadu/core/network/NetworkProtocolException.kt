package br.com.mykytadu.core.network

class GraphQlException(
    val errors: List<String>,
) : Exception(errors.joinToString(separator = "; "))

class InvalidResponseException(
    message: String,
) : Exception(message)

class InvalidRequestException(
    message: String,
) : IllegalArgumentException(message)