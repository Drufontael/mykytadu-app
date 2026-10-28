package br.com.mykytadu.domain.model.auth

import kotlin.jvm.JvmInline

@JvmInline
value class UserId(val value: String) {
    init {
        require(value.isNotBlank()) { "UserId cannot be blank" }
    }
}

data class User(
    val id: UserId,
    val email: String,
    val name: String,
    val createdAtEpochMillis: Long = 0L,
) {
    init {
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(name.isNotBlank()) { "Name cannot be blank" }
    }
}
