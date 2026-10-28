package br.com.mykytadu.data.remote.dto.auth

import br.com.mykytadu.domain.model.auth.UserId
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AuthDtoMapperTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `UserDto mapeia para User de dominio`() {
        val dto = UserDto(
            id = "user-abc-123",
            email = "user@example.com",
            name = "Test User",
            createdAtEpochMillis = 1700000000000L,
        )
        val domain = dto.toDomain()
        assertEquals(UserId("user-abc-123"), domain.id)
        assertEquals("user@example.com", domain.email)
        assertEquals("Test User", domain.name)
        assertEquals(1700000000000L, domain.createdAtEpochMillis)
    }

    @Test
    fun `LoginResponseDto serializa e mapeia para AuthTokens`() {
        val jsonString = """
            {
                "accessToken": "jwt.sample.token",
                "tokenType": "Bearer",
                "expiresIn": 600,
                "refreshToken": "opaque-refresh-token",
                "user": {
                    "id": "u-1",
                    "email": "a@b.com",
                    "name": "User"
                }
            }
        """.trimIndent()

        val dto = json.decodeFromString<LoginResponseDto>(jsonString)
        val tokens = dto.toTokens()

        assertEquals("jwt.sample.token", tokens.accessToken)
        assertEquals("Bearer", tokens.tokenType)
        assertEquals(600L, tokens.expiresInSeconds)
        assertEquals("opaque-refresh-token", tokens.refreshToken)

        val user = dto.user?.toDomain()
        assertEquals(UserId("u-1"), user?.id)
    }

    @Test
    fun `RefreshResponseDto serializa e mapeia para AuthTokens`() {
        val jsonString = """
            {
                "accessToken": "new-access-jwt",
                "tokenType": "Bearer",
                "expiresIn": 600,
                "refreshToken": "new-refresh-token"
            }
        """.trimIndent()

        val dto = json.decodeFromString<RefreshResponseDto>(jsonString)
        val tokens = dto.toTokens()

        assertEquals("new-access-jwt", tokens.accessToken)
        assertEquals("new-refresh-token", tokens.refreshToken)
    }

    @Test
    fun `LoginRequestDto e LogoutRequestDto serializam corretamente`() {
        val login = LoginRequestDto("a@b.com", "pass", "NATIVE")
        val loginJson = json.encodeToString(LoginRequestDto.serializer(), login)
        val decodedLogin = json.decodeFromString<LoginRequestDto>(loginJson)
        assertEquals(login, decodedLogin)

        val logout = LogoutRequestDto()
        assertNull(logout.refreshToken)
    }
}
