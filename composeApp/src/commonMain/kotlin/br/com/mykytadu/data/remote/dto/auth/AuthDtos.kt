package br.com.mykytadu.data.remote.dto.auth

import br.com.mykytadu.domain.model.auth.AuthTokens
import br.com.mykytadu.domain.model.auth.User
import br.com.mykytadu.domain.model.auth.UserId
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String,
    val platform: String = "NATIVE",
)

@Serializable
data class LoginResponseDto(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val refreshToken: String? = null,
    val user: UserDto? = null,
)

@Serializable
data class RegisterRequestDto(
    val email: String,
    val password: String,
    val name: String,
)

@Serializable
data class RegisterResponseDto(
    val id: String,
    val email: String,
    val name: String,
)

@Serializable
data class RefreshRequestDto(
    val refreshToken: String,
)

@Serializable
data class RefreshResponseDto(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val refreshToken: String? = null,
)

@Serializable
data class LogoutRequestDto(
    val refreshToken: String? = null,
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    val createdAtEpochMillis: Long = 0L,
)

fun UserDto.toDomain(): User = User(
    id = UserId(id),
    email = email,
    name = name,
    createdAtEpochMillis = createdAtEpochMillis,
)

fun LoginResponseDto.toTokens(): AuthTokens = AuthTokens(
    accessToken = accessToken,
    tokenType = tokenType,
    expiresInSeconds = expiresIn,
    refreshToken = refreshToken,
)

fun RefreshResponseDto.toTokens(): AuthTokens = AuthTokens(
    accessToken = accessToken,
    tokenType = tokenType,
    expiresInSeconds = expiresIn,
    refreshToken = refreshToken,
)
