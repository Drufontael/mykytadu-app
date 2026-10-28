package br.com.mykytadu.data.remote.api

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.remote.dto.auth.LoginRequestDto
import br.com.mykytadu.data.remote.dto.auth.LoginResponseDto
import br.com.mykytadu.data.remote.dto.auth.LogoutRequestDto
import br.com.mykytadu.data.remote.dto.auth.RefreshRequestDto
import br.com.mykytadu.data.remote.dto.auth.RefreshResponseDto
import br.com.mykytadu.data.remote.dto.auth.RegisterRequestDto
import br.com.mykytadu.data.remote.dto.auth.RegisterResponseDto
import br.com.mykytadu.data.remote.dto.auth.UserDto

interface AuthApi {
    suspend fun login(request: LoginRequestDto): NetworkResult<LoginResponseDto>
    suspend fun register(request: RegisterRequestDto): NetworkResult<RegisterResponseDto>
    suspend fun refresh(request: RefreshRequestDto): NetworkResult<RefreshResponseDto>
    suspend fun logout(request: LogoutRequestDto): NetworkResult<Unit>
    suspend fun getProfile(accessToken: String): NetworkResult<UserDto>
}
