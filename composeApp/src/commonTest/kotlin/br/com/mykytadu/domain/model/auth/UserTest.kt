package br.com.mykytadu.domain.model.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserTest {

    @Test
    fun `UserId valido instancia corretamente`() {
        val id = UserId("usr-123")
        assertEquals("usr-123", id.value)
    }

    @Test
    fun `UserId em branco lanca excecao`() {
        assertFailsWith<IllegalArgumentException> {
            UserId("")
        }
        assertFailsWith<IllegalArgumentException> {
            UserId("   ")
        }
    }

    @Test
    fun `User valido instancia com propriedades corretas`() {
        val user = User(
            id = UserId("u-1"),
            email = "eduardo@mykytadu.com",
            name = "Eduardo",
            createdAtEpochMillis = 1000L,
        )
        assertEquals(UserId("u-1"), user.id)
        assertEquals("eduardo@mykytadu.com", user.email)
        assertEquals("Eduardo", user.name)
        assertEquals(1000L, user.createdAtEpochMillis)
    }

    @Test
    fun `User com campos invalidos lanca excecao`() {
        assertFailsWith<IllegalArgumentException> {
            User(id = UserId("u-1"), email = "", name = "Nome")
        }
        assertFailsWith<IllegalArgumentException> {
            User(id = UserId("u-1"), email = "email@test.com", name = "  ")
        }
    }

    @Test
    fun `AuthTokens valida tokens e tempo de expiracao`() {
        val tokens = AuthTokens(
            accessToken = "jwt-access",
            tokenType = "Bearer",
            expiresInSeconds = 600L,
            refreshToken = "refresh-opaque",
        )
        assertEquals("jwt-access", tokens.accessToken)
        assertEquals(600L, tokens.expiresInSeconds)
        assertEquals("refresh-opaque", tokens.refreshToken)

        assertFailsWith<IllegalArgumentException> {
            AuthTokens(accessToken = "", expiresInSeconds = 600L)
        }
        assertFailsWith<IllegalArgumentException> {
            AuthTokens(accessToken = "token", expiresInSeconds = 0L)
        }
    }

    @Test
    fun `AuthState estados distintos sao representados corretamente`() {
        val user = User(UserId("1"), "a@b.com", "User")
        val unauth: AuthState = AuthState.Unauthenticated
        val init: AuthState = AuthState.Initializing
        val auth: AuthState = AuthState.Authenticated(user)

        assertEquals(AuthState.Unauthenticated, unauth)
        assertEquals(AuthState.Initializing, init)
        assertEquals(user, (auth as AuthState.Authenticated).user)
    }
}
