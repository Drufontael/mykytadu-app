package br.com.mykytadu.data.remote.anilist

import br.com.mykytadu.core.network.NetworkResult
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
import kotlin.test.assertTrue
import io.ktor.http.HttpMethod
import kotlin.test.assertNull
import br.com.mykytadu.core.network.NetworkFailure
import io.ktor.http.content.TextContent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AniListAnimeApiTest {

    @Test
    fun `deve pesquisar anime e retornar pagina desserializada`() = runTest {
        val engine = MockEngine {
            respond(
                content = SEARCH_RESPONSE,
                status = HttpStatusCode.OK,
                headers = JSON_HEADERS,
            )
        }
        val client = createTestClient(engine)
        val api = AniListAnimeApi(client)

        try {
            val result = api.searchAnime(
                search = "Naruto",
                page = 1,
                perPage = 10,
            )

            val success =
                assertIs<NetworkResult.Success<*>>(result)

            val page = assertIs<
                    br.com.mykytadu.data.remote.anilist.dto.AnimeSearchPageDto
                    >(success.value)

            assertEquals(1, page.pageInfo?.currentPage)
            assertEquals(2, page.pageInfo?.lastPage)
            assertTrue(page.pageInfo?.hasNextPage == true)
            assertEquals(10, page.pageInfo.perPage)
            assertEquals(11, page.pageInfo.total)

            val anime = page.media.single()

            assertEquals(20, anime.id)
            assertEquals(20, anime.idMal)
            assertEquals("NARUTO", anime.title.romaji)
            assertEquals("Naruto", anime.title.english)
            assertEquals("TV", anime.format)
            assertEquals(220, anime.episodes)
            assertEquals(80, anime.averageScore)
        } finally {
            client.close()
        }
    }

    @Test
    fun `deve usar POST no endpoint AniList sem Authorization`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedUrl: String? = null
        var authorizationHeader: String? = "not-inspected"

        val engine = MockEngine { request ->
            capturedMethod = request.method
            capturedUrl = request.url.toString()
            authorizationHeader = request.headers[HttpHeaders.Authorization]

            respond(
                content = SEARCH_RESPONSE,
                status = HttpStatusCode.OK,
                headers = JSON_HEADERS,
            )
        }
        val client = createTestClient(engine)
        val api = AniListAnimeApi(client)

        try {
            val result = api.searchAnime(
                search = "Naruto",
                page = 1,
                perPage = 10,
            )

            assertIs<NetworkResult.Success<*>>(result)
            assertEquals(HttpMethod.Post, capturedMethod)
            assertEquals("https://graphql.anilist.co", capturedUrl)
            assertNull(authorizationHeader)
        } finally {
            client.close()
        }
    }

    @Test
    fun `deve converter errors GraphQL em falha`() = runTest {
        val engine = MockEngine {
            respond(
                content = GRAPHQL_ERROR_RESPONSE,
                status = HttpStatusCode.OK,
                headers = JSON_HEADERS,
            )
        }
        val client = createTestClient(engine)
        val api = AniListAnimeApi(client)

        try {
            val result = api.searchAnime(
                search = "Naruto",
                page = 1,
                perPage = 10,
            )

            val failure = assertIs<NetworkResult.Failure>(result)
            val graphQlFailure =
                assertIs<NetworkFailure.GraphQl>(failure.error)

            assertEquals(
                listOf("Invalid GraphQL query."),
                graphQlFailure.messages,
            )
        } finally {
            client.close()
        }
    }

    @Test
    fun `deve retornar resposta invalida quando Page estiver ausente`() = runTest {
        val engine = MockEngine {
            respond(
                content = """{"data":{}}""",
                status = HttpStatusCode.OK,
                headers = JSON_HEADERS,
            )
        }
        val client = createTestClient(engine)
        val api = AniListAnimeApi(client)

        try {
            val result = api.searchAnime(
                search = "Naruto",
                page = 1,
                perPage = 10,
            )

            val failure = assertIs<NetworkResult.Failure>(result)
            val invalidResponse =
                assertIs<NetworkFailure.InvalidResponse>(failure.error)

            assertEquals(
                "AniList response does not contain Page data.",
                invalidResponse.message,
            )
        } finally {
            client.close()
        }
    }

    @Test
    fun `nao deve realizar request com argumentos invalidos`() = runTest {
        var requestCount = 0

        val engine = MockEngine {
            requestCount++

            respond(
                content = SEARCH_RESPONSE,
                status = HttpStatusCode.OK,
                headers = JSON_HEADERS,
            )
        }
        val client = createTestClient(engine)
        val api = AniListAnimeApi(client)

        try {
            val blankSearch = api.searchAnime(
                search = "   ",
                page = 1,
                perPage = 10,
            )
            val invalidPage = api.searchAnime(
                search = "Naruto",
                page = 0,
                perPage = 10,
            )
            val invalidPageSize = api.searchAnime(
                search = "Naruto",
                page = 1,
                perPage = 0,
            )

            assertIs<NetworkFailure.InvalidRequest>(
                assertIs<NetworkResult.Failure>(blankSearch).error
            )
            assertIs<NetworkFailure.InvalidRequest>(
                assertIs<NetworkResult.Failure>(invalidPage).error
            )
            assertIs<NetworkFailure.InvalidRequest>(
                assertIs<NetworkResult.Failure>(invalidPageSize).error
            )

            assertEquals(0, requestCount)
        } finally {
            client.close()
        }
    }

    @Test
    fun `deve aceitar pesquisa sem resultados`() = runTest {
        val engine = MockEngine {
            respond(
                content = EMPTY_SEARCH_RESPONSE,
                status = HttpStatusCode.OK,
                headers = JSON_HEADERS,
            )
        }
        val client = createTestClient(engine)
        val api = AniListAnimeApi(client)

        try {
            val result = api.searchAnime(
                search = "Anime inexistente",
                page = 1,
                perPage = 10,
            )

            val success = assertIs<NetworkResult.Success<*>>(result)
            val page = assertIs<
                    br.com.mykytadu.data.remote.anilist.dto.AnimeSearchPageDto
                    >(success.value)

            assertTrue(page.media.isEmpty())
            assertEquals(false, page.pageInfo?.hasNextPage)
            assertEquals(0, page.pageInfo?.total)
        } finally {
            client.close()
        }
    }

    @Test
    fun `deve aceitar campos opcionais nulos na pesquisa`() = runTest {
        val engine = MockEngine {
            respond(
                content = NULLABLE_SEARCH_RESPONSE,
                status = HttpStatusCode.OK,
                headers = JSON_HEADERS,
            )
        }
        val client = createTestClient(engine)
        val api = AniListAnimeApi(client)

        try {
            val result = api.searchAnime(
                search = "Future Anime",
                page = 1,
                perPage = 10,
            )

            val success = assertIs<NetworkResult.Success<*>>(result)
            val page = assertIs<
                    br.com.mykytadu.data.remote.anilist.dto.AnimeSearchPageDto
                    >(success.value)

            val anime = page.media.single()

            assertEquals(999, anime.id)
            assertEquals(null, anime.idMal)
            assertEquals(null, anime.title.english)
            assertEquals(null, anime.coverImage)
            assertEquals(null, anime.episodes)
            assertEquals(null, anime.season)
            assertEquals(null, anime.seasonYear)
            assertEquals(null, anime.averageScore)
        } finally {
            client.close()
        }
    }
    @Test
    fun `deve enviar query e variables da pesquisa`() = runTest {
        var capturedBody: String? = null

        val engine = MockEngine { request ->
            capturedBody = assertIs<TextContent>(request.body).text

            respond(
                content = SEARCH_RESPONSE,
                status = HttpStatusCode.OK,
                headers = JSON_HEADERS,
            )
        }
        val client = createTestClient(engine)
        val api = AniListAnimeApi(client)

        try {
            val result = api.searchAnime(
                search = "  Naruto  ",
                page = 2,
                perPage = 10,
            )

            assertIs<NetworkResult.Success<*>>(result)

            val requestJson = Json
                .parseToJsonElement(requireNotNull(capturedBody))
                .jsonObject

            val query = requestJson
                .getValue("query")
                .jsonPrimitive
                .content

            val variables = requestJson
                .getValue("variables")
                .jsonObject

            assertTrue(query.contains("query SearchAnime"))
            assertTrue(query.contains("type: ANIME"))
            assertTrue(query.contains("sort: SEARCH_MATCH"))

            assertEquals(
                "Naruto",
                variables.getValue("search").jsonPrimitive.content,
            )
            assertEquals(
                2,
                variables.getValue("page").jsonPrimitive.int,
            )
            assertEquals(
                10,
                variables.getValue("perPage").jsonPrimitive.int,
            )
        } finally {
            client.close()
        }
    }

    @Test
    fun `deve buscar detalhes do anime pelo id`() = runTest {
        val engine = MockEngine {
            respond(
                content = DETAILS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = JSON_HEADERS,
            )
        }
        val client = createTestClient(engine)
        val api = AniListAnimeApi(client)

        try {
            val result = api.getAnimeDetails(id = 21579)

            val success = assertIs<NetworkResult.Success<*>>(result)
            val anime = assertIs<
                    br.com.mykytadu.data.remote.anilist.dto.AnimeDetailsDto
                    >(success.value)

            assertEquals(21579, anime.id)
            assertEquals(32365, anime.idMal)
            assertEquals(
                "Boruto: Naruto the Movie - The Day Naruto Became Hokage",
                anime.title.english,
            )
            assertEquals("SPECIAL", anime.format)
            assertEquals(1, anime.episodes)
            assertEquals(10, anime.duration)
            assertEquals(2016, anime.startDate?.year)
            assertEquals(7, anime.startDate?.month)
            assertEquals(listOf("Action", "Comedy"), anime.genres)
            assertTrue(anime.studios?.nodes?.isEmpty() == true)
            assertEquals(null, anime.trailer)
            assertEquals(null, anime.nextAiringEpisode)
            assertEquals("PREQUEL", anime.relations?.edges?.single()?.relationType)
            assertTrue(anime.externalLinks.isEmpty())
        } finally {
            client.close()
        }
    }

    @Test
    fun `deve enviar query de detalhes e variable id`() = runTest {
        var capturedBody: String? = null

        val engine = MockEngine { request ->
            capturedBody = assertIs<TextContent>(request.body).text

            respond(
                content = DETAILS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = JSON_HEADERS,
            )
        }
        val client = createTestClient(engine)
        val api = AniListAnimeApi(client)

        try {
            val result = api.getAnimeDetails(id = 21579)

            assertIs<NetworkResult.Success<*>>(result)

            val requestJson = Json
                .parseToJsonElement(requireNotNull(capturedBody))
                .jsonObject

            val query = requestJson
                .getValue("query")
                .jsonPrimitive
                .content

            val variables = requestJson
                .getValue("variables")
                .jsonObject

            assertTrue(query.contains("query GetAnimeDetails"))
            assertTrue(query.contains("description(asHtml: false)"))
            assertTrue(query.contains("studios(isMain: true)"))
            assertEquals(
                21579,
                variables.getValue("id").jsonPrimitive.int,
            )
        } finally {
            client.close()
        }
    }

    @Test
    fun `nao deve buscar detalhes com id invalido`() = runTest {
        var requestCount = 0

        val engine = MockEngine {
            requestCount++

            respond(
                content = DETAILS_RESPONSE,
                status = HttpStatusCode.OK,
                headers = JSON_HEADERS,
            )
        }
        val client = createTestClient(engine)
        val api = AniListAnimeApi(client)

        try {
            val result = api.getAnimeDetails(id = 0)

            val failure = assertIs<NetworkResult.Failure>(result)
            val invalidRequest =
                assertIs<NetworkFailure.InvalidRequest>(failure.error)

            assertEquals(
                "Anime id must be greater than or equal to 1.",
                invalidRequest.message,
            )
            assertEquals(0, requestCount)
        } finally {
            client.close()
        }
    }

    @Test
    fun `deve retornar resposta invalida quando Media estiver ausente`() = runTest {
        val engine = MockEngine {
            respond(
                content = """{"data":{"Media":null}}""",
                status = HttpStatusCode.OK,
                headers = JSON_HEADERS,
            )
        }
        val client = createTestClient(engine)
        val api = AniListAnimeApi(client)

        try {
            val result = api.getAnimeDetails(id = 21579)

            val failure = assertIs<NetworkResult.Failure>(result)
            val invalidResponse =
                assertIs<NetworkFailure.InvalidResponse>(failure.error)

            assertEquals(
                "AniList response does not contain Media data.",
                invalidResponse.message,
            )
        } finally {
            client.close()
        }
    }

    @Test
    fun `deve rejeitar detalhes parciais acompanhados de errors GraphQL`() =
        runTest {
            val engine = MockEngine {
                respond(
                    content = PARTIAL_DETAILS_RESPONSE,
                    status = HttpStatusCode.OK,
                    headers = JSON_HEADERS,
                )
            }
            val client = createTestClient(engine)
            val api = AniListAnimeApi(client)

            try {
                val result = api.getAnimeDetails(id = 21579)

                val failure = assertIs<NetworkResult.Failure>(result)
                val graphQlFailure =
                    assertIs<NetworkFailure.GraphQl>(failure.error)

                assertEquals(
                    listOf("Some fields could not be resolved."),
                    graphQlFailure.messages,
                )
            } finally {
                client.close()
            }
        }

    @Test
    fun `deve retornar resposta invalida quando PageInfo estiver ausente`() =
        runTest {
            val engine = MockEngine {
                respond(
                    content = """
                    {
                      "data": {
                        "Page": {
                          "media": []
                        }
                      }
                    }
                """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = JSON_HEADERS,
                )
            }
            val client = createTestClient(engine)
            val api = AniListAnimeApi(client)

            try {
                val result = api.searchAnime(
                    search = "Naruto",
                    page = 1,
                    perPage = 10,
                )

                val failure = assertIs<NetworkResult.Failure>(result)
                val invalidResponse =
                    assertIs<NetworkFailure.InvalidResponse>(failure.error)

                assertEquals(
                    "AniList response does not contain PageInfo data.",
                    invalidResponse.message,
                )
            } finally {
                client.close()
            }
        }

    private fun createTestClient(
        engine: MockEngine,
    ): HttpClient =
        HttpClient(engine) {
            configureSharedHttpClient(
                networkLogger = Logger.EMPTY,
                loggingEnabled = false,
            )
        }

    private companion object {
        val JSON_HEADERS = headersOf(
            HttpHeaders.ContentType,
            ContentType.Application.Json.toString(),
        )

        val SEARCH_RESPONSE = """
            {
              "data": {
                "Page": {
                  "pageInfo": {
                    "currentPage": 1,
                    "lastPage": 2,
                    "hasNextPage": true,
                    "perPage": 10,
                    "total": 11
                  },
                  "media": [
                    {
                      "id": 20,
                      "idMal": 20,
                      "title": {
                        "romaji": "NARUTO",
                        "english": "Naruto",
                        "native": "NARUTO -ナルト-"
                      },
                      "coverImage": {
                        "large": "https://example.test/naruto.jpg",
                        "color": "#e47850"
                      },
                      "format": "TV",
                      "status": "FINISHED",
                      "episodes": 220,
                      "season": "FALL",
                      "seasonYear": 2002,
                      "averageScore": 80
                    }
                  ]
                }
              }
            }
        """.trimIndent()

        val GRAPHQL_ERROR_RESPONSE = """
            {
              "data": null,
              "errors": [
                {
                  "message": "Invalid GraphQL query."
                }
              ]
            }
        """.trimIndent()

        val EMPTY_SEARCH_RESPONSE = """
        {
          "data": {
            "Page": {
              "pageInfo": {
                "currentPage": 1,
                "lastPage": 1,
                "hasNextPage": false,
                "perPage": 10,
                "total": 0
              },
              "media": []
            }
          }
        }
    """.trimIndent()

        val NULLABLE_SEARCH_RESPONSE = """
        {
          "data": {
            "Page": {
              "pageInfo": {
                "currentPage": 1,
                "lastPage": 1,
                "hasNextPage": false,
                "perPage": 10,
                "total": 1
              },
              "media": [
                {
                  "id": 999,
                  "idMal": null,
                  "title": {
                    "romaji": "Future Anime",
                    "english": null,
                    "native": "Future Anime"
                  },
                  "coverImage": null,
                  "format": null,
                  "status": "NOT_YET_RELEASED",
                  "episodes": null,
                  "season": null,
                  "seasonYear": null,
                  "averageScore": null
                }
              ]
            }
          }
        }
    """.trimIndent()

        val DETAILS_RESPONSE = """
        {
          "data": {
            "Media": {
              "id": 21579,
              "idMal": 32365,
              "title": {
                "romaji": "BORUTO: NARUTO THE MOVIE - Naruto ga Hokage ni Natta Hi",
                "english": "Boruto: Naruto the Movie - The Day Naruto Became Hokage",
                "native": "BORUTO -NARUTO THE MOVIE-"
              },
              "synonyms": [],
              "description": "Bundled with the limited edition.",
              "coverImage": {
                "extraLarge": "https://example.test/cover.jpg",
                "large": "https://example.test/cover.jpg",
                "color": "#35aee4"
              },
              "bannerImage": "https://example.test/banner.jpg",
              "format": "SPECIAL",
              "status": "FINISHED",
              "source": "MANGA",
              "episodes": 1,
              "duration": 10,
              "season": "SUMMER",
              "seasonYear": 2016,
              "countryOfOrigin": "JP",
              "isAdult": false,
              "startDate": {
                "year": 2016,
                "month": 7,
                "day": 6
              },
              "endDate": {
                "year": 2016,
                "month": 7,
                "day": 6
              },
              "genres": [
                "Action",
                "Comedy"
              ],
              "averageScore": 70,
              "meanScore": 70,
              "popularity": 30687,
              "favourites": 313,
              "studios": {
                "nodes": []
              },
              "trailer": null,
              "nextAiringEpisode": null,
              "relations": {
                "edges": [
                  {
                    "relationType": "PREQUEL",
                    "node": {
                      "id": 1735,
                      "type": "ANIME",
                      "format": "TV",
                      "status": "FINISHED",
                      "title": {
                        "romaji": "NARUTO: Shippuuden",
                        "english": "Naruto: Shippuden",
                        "native": "NARUTO -ナルト- 疾風伝"
                      },
                      "coverImage": {
                        "medium": "https://example.test/relation.jpg"
                      }
                    }
                  }
                ]
              },
              "externalLinks": []
            }
          }
        }
    """.trimIndent()

        val PARTIAL_DETAILS_RESPONSE = """
        {
          "data": {
            "Media": {
              "id": 21579,
              "title": {
                "romaji": "Boruto",
                "english": null,
                "native": null
              }
            }
          },
          "errors": [
            {
              "message": "Some fields could not be resolved."
            }
          ]
        }
    """.trimIndent()

    }
}