package br.com.mykytadu.data.remote.api

import br.com.mykytadu.core.network.NetworkFailure
import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.remote.dto.sync.LibraryEntrySyncDto
import br.com.mykytadu.data.remote.dto.sync.SyncBatchRequestDto
import br.com.mykytadu.data.remote.dto.sync.SyncBatchResponseDto
import br.com.mykytadu.data.remote.dto.sync.SyncMutationDto
import br.com.mykytadu.data.remote.dto.sync.SyncMutationType
import br.com.mykytadu.di.configureSharedHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.Logger
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class KtorSyncApiTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

    @Test
    fun `syncLibrary envia Authorization Bearer e body serializado retornando SyncBatchResponseDto no sucesso`() = runTest {
        val responseJson = """
            {
              "syncedAtEpochMillis": 1700005000,
              "appliedCount": 1,
              "serverChanges": [
                {
                  "animeId": "100",
                  "status": "COMPLETED",
                  "progressCurrent": 24,
                  "progressTotal": 24,
                  "isFavorite": true,
                  "rating": 95,
                  "notes": "Obra de arte",
                  "createdAtEpochMillis": 1700000000,
                  "updatedAtEpochMillis": 1700004500
                }
              ],
              "deletedAnimeIds": ["200"]
            }
        """.trimIndent()

        var authHeaderReceived: String? = null
        var pathReceived: String? = null

        val engine = MockEngine { request ->
            pathReceived = request.url.encodedPath
            authHeaderReceived = request.headers[HttpHeaders.Authorization]
            respond(responseJson, HttpStatusCode.OK, jsonHeaders)
        }
        val api = KtorSyncApi(createTestClient(engine))

        val request = SyncBatchRequestDto(
            lastSyncedAtEpochMillis = 1700000000L,
            mutations = listOf(
                SyncMutationDto(
                    animeId = "100",
                    type = SyncMutationType.UPSERT,
                    payload = LibraryEntrySyncDto(
                        animeId = "100",
                        status = "WATCHING",
                        progressCurrent = 12,
                        createdAtEpochMillis = 1700000000L,
                        updatedAtEpochMillis = 1700003000L,
                    ),
                    timestampEpochMillis = 1700003000L,
                )
            )
        )

        val result = api.syncLibrary("valid-token", request)

        val success = assertIs<NetworkResult.Success<SyncBatchResponseDto>>(result)
        assertEquals("/api/v1/sync/library", pathReceived)
        assertEquals("Bearer valid-token", authHeaderReceived)
        assertEquals(1700005000L, success.value.syncedAtEpochMillis)
        assertEquals(1, success.value.appliedCount)
        assertEquals(1, success.value.serverChanges.size)
        assertEquals("100", success.value.serverChanges.first().animeId)
        assertEquals("COMPLETED", success.value.serverChanges.first().status)
        assertEquals(listOf("200"), success.value.deletedAnimeIds)
    }

    @Test
    fun `syncLibrary retorna NetworkResult Failure com Http 401 quando nao autorizado`() = runTest {
        val problemJson = """
            {
              "type": "https://api.mykytadu.com/errors/unauthorized",
              "title": "Não autorizado",
              "status": 401,
              "detail": "Token inválido ou expirado.",
              "code": "AUTH_TOKEN_EXPIRED"
            }
        """.trimIndent()

        val engine = MockEngine {
            respond(problemJson, HttpStatusCode.Unauthorized, jsonHeaders)
        }
        val api = KtorSyncApi(createTestClient(engine))

        val result = api.syncLibrary("expired-token", SyncBatchRequestDto())

        val failure = assertIs<NetworkResult.Failure>(result)
        val httpFailure = assertIs<NetworkFailure.Http>(failure.error)
        assertEquals(401, httpFailure.statusCode)
    }

    private fun createTestClient(engine: MockEngine): HttpClient = HttpClient(engine) {
        configureSharedHttpClient(
            networkLogger = Logger.EMPTY,
            loggingEnabled = false,
        )
    }
}
