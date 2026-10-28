package br.com.mykytadu.data.remote.api

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.core.network.safeNetworkCall
import br.com.mykytadu.data.remote.dto.auth.LoginRequestDto
import br.com.mykytadu.data.remote.dto.auth.LoginResponseDto
import br.com.mykytadu.data.remote.dto.auth.LogoutRequestDto
import br.com.mykytadu.data.remote.dto.auth.RefreshRequestDto
import br.com.mykytadu.data.remote.dto.auth.RefreshResponseDto
import br.com.mykytadu.data.remote.dto.auth.RegisterRequestDto
import br.com.mykytadu.data.remote.dto.auth.RegisterResponseDto
import br.com.mykytadu.data.remote.dto.auth.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders

class KtorAuthApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = DEFAULT_BASE_URL,
) : AuthApi {

    override suspend fun login(request: LoginRequestDto): NetworkResult<LoginResponseDto> =
        safeNetworkCall {
            httpClient.post("$baseUrl/api/v1/auth/login") {
                setBody(request)
            }.body<LoginResponseDto>()
        }

    override suspend fun register(request: RegisterRequestDto): NetworkResult<RegisterResponseDto> =
        safeNetworkCall {
            httpClient.post("$baseUrl/api/v1/auth/register") {
                setBody(request)
            }.body<RegisterResponseDto>()
        }

    override suspend fun refresh(request: RefreshRequestDto): NetworkResult<RefreshResponseDto> =
        safeNetworkCall {
            httpClient.post("$baseUrl/api/v1/auth/refresh") {
                setBody(request)
            }.body<RefreshResponseDto>()
        }

    override suspend fun logout(request: LogoutRequestDto): NetworkResult<Unit> =
        safeNetworkCall {
            httpClient.post("$baseUrl/api/v1/auth/logout") {
                setBody(request)
            }
            Unit
        }

    override suspend fun getProfile(accessToken: String): NetworkResult<UserDto> =
        safeNetworkCall {
            httpClient.get("$baseUrl/api/v1/me") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }.body<UserDto>()
        }

    companion object {
        const val DEFAULT_BASE_URL = "https://api.mykytadu.com"
    }
}
