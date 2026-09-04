package br.com.mykytadu.data.remote.anilist

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.di.configureSharedHttpClient
import br.com.mykytadu.di.provideHttpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.Logger
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AniListSmokeTest {

    @Test
    fun `deve realizar pesquisa real na AniList`() = runBlocking {
        if (System.getenv(RUN_SMOKE_TEST) != "true") {
            return@runBlocking
        }

        val client = HttpClient(provideHttpClientEngine()) {
            configureSharedHttpClient(
                networkLogger = Logger.EMPTY,
                loggingEnabled = false,
            )
        }
        val api = AniListAnimeApi(client)

        try {
            val result = api.searchAnime(
                search = "Naruto",
                page = 1,
                perPage = 10,
            )

            val success = assertIs<NetworkResult.Success<*>>(result)
            val page = assertIs<
                    br.com.mykytadu.data.remote.anilist.dto.AnimeSearchPageDto
                    >(success.value)

            assertEquals(1, page.pageInfo?.currentPage)
            assertEquals(10, page.pageInfo?.perPage)
            assertTrue(page.media.isNotEmpty())
            assertTrue(
                page.media.any { anime ->
                    anime.title.romaji
                        ?.contains("Naruto", ignoreCase = true) == true
                }
            )
        } finally {
            client.close()
        }
    }

    @Test
    fun `deve buscar detalhes reais na AniList`() = runBlocking {
        if (System.getenv(RUN_SMOKE_TEST) != "true") {
            return@runBlocking
        }

        val client = HttpClient(provideHttpClientEngine()) {
            configureSharedHttpClient(
                networkLogger = Logger.EMPTY,
                loggingEnabled = false,
            )
        }
        val api = AniListAnimeApi(client)

        try {
            val result = api.getAnimeDetails(id = 20)

            val success = assertIs<NetworkResult.Success<*>>(result)
            val anime = assertIs<
                    br.com.mykytadu.data.remote.anilist.dto.AnimeDetailsDto
                    >(success.value)

            assertEquals(20, anime.id)
            assertTrue(
                anime.title.romaji
                    ?.contains("Naruto", ignoreCase = true) == true
            )
            assertTrue(anime.genres.isNotEmpty())
            assertTrue(anime.episodes != null)
        } finally {
            client.close()
        }
    }

    private companion object {
        const val RUN_SMOKE_TEST =
            "MYKYTADU_RUN_ANILIST_SMOKE"
    }
}