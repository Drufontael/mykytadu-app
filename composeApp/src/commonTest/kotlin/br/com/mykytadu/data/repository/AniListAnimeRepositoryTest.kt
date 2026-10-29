package br.com.mykytadu.data.repository

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.remote.anilist.dto.AnimeDetailsDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchPageDto
import br.com.mykytadu.data.remote.anilist.dto.PageInfoDto
import br.com.mykytadu.data.remote.api.AnimeApi
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchItemDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeTitleDto
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.core.network.NetworkFailure
import kotlin.test.assertSame
import kotlinx.coroutines.CancellationException
import kotlin.test.assertFailsWith
import br.com.mykytadu.data.local.datasource.InMemoryCatalogCacheLocalDataSource

class AniListAnimeRepositoryTest {

    @Test
    fun `deve normalizar consulta e delegar argumentos para api`() = runTest {
        val api = RecordingAnimeApi()
        val repository = AniListAnimeRepository(api)

        val result = repository.searchAnime(
            query = "  Naruto  ",
            page = 2,
            perPage = 10,
        )

        assertIs<RepositoryResult.Success<*>>(result)
        assertEquals(1, api.searchCallCount)
        assertEquals("Naruto", api.lastSearch)
        assertEquals(2, api.lastPage)
        assertEquals(10, api.lastPerPage)
    }

    @Test
    fun `nao deve chamar api quando consulta estiver vazia`() = runTest {
        val api = RecordingAnimeApi()
        val repository = AniListAnimeRepository(api)

        val result = repository.searchAnime(
            query = "   ",
            page = 1,
            perPage = 20,
        )

        val failure = assertIs<RepositoryResult.Failure>(result)
        assertEquals(RepositoryFailure.InvalidInput, failure.reason)
        assertEquals(0, api.searchCallCount)
    }

    @Test
    fun `nao deve chamar api quando pagina estiver invalida`() = runTest {
        val api = RecordingAnimeApi()
        val repository = AniListAnimeRepository(api)

        val result = repository.searchAnime(
            query = "Naruto",
            page = 0,
            perPage = 20,
        )

        val failure = assertIs<RepositoryResult.Failure>(result)
        assertEquals(RepositoryFailure.InvalidInput, failure.reason)
        assertEquals(0, api.searchCallCount)
    }

    @Test
    fun `nao deve chamar api quando tamanho da pagina estiver invalido`() = runTest {
        val api = RecordingAnimeApi()
        val repository = AniListAnimeRepository(api)

        val result = repository.searchAnime(
            query = "Naruto",
            page = 1,
            perPage = 0,
        )

        val failure = assertIs<RepositoryResult.Failure>(result)
        assertEquals(RepositoryFailure.InvalidInput, failure.reason)
        assertEquals(0, api.searchCallCount)
    }

    @Test
    fun `deve retornar pesquisa mapeada para dominio`() = runTest {
        val api = RecordingAnimeApi(
            searchResult = NetworkResult.Success(
                AnimeSearchPageDto(
                    pageInfo = PageInfoDto(
                        currentPage = 1,
                        lastPage = 1,
                        hasNextPage = false,
                        perPage = 20,
                        total = 1,
                    ),
                    media = listOf(
                        AnimeSearchItemDto(
                            id = 5114,
                            title = AnimeTitleDto(
                                romaji = "Fullmetal Alchemist",
                            ),
                        ),
                    ),
                ),
            ),
        )
        val repository = AniListAnimeRepository(api)

        val result = repository.searchAnime("Fullmetal", 1, 20)

        val success = assertIs<RepositoryResult.Success<*>>(result)
        val page = assertIs<PagedResult<*>>(success.value)
        val anime = assertIs<AnimeSummary>(page.items.single())

        assertEquals("5114", anime.id.value)
        assertEquals("Fullmetal Alchemist", anime.titles.romaji)
        assertEquals(1, page.pageInfo.currentPage)
        assertEquals(1, page.pageInfo.total)
    }

    @Test
    fun `deve retornar detalhes mapeados e delegar id para api`() = runTest {
        val api = RecordingAnimeApi(
            detailsResult = NetworkResult.Success(
                AnimeDetailsDto(
                    id = 5114,
                    title = AnimeTitleDto(
                        romaji = "Fullmetal Alchemist",
                    ),
                ),
            ),
        )
        val repository = AniListAnimeRepository(api)

        val result = repository.getAnimeDetails(CatalogAnimeId("5114"))

        val success = assertIs<RepositoryResult.Success<*>>(result)
        val anime = assertIs<AnimeDetails>(success.value)

        assertEquals("5114", anime.id.value)
        assertEquals("Fullmetal Alchemist", anime.titles.romaji)
        assertEquals(5114, api.requestedDetailsId)
    }

    @Test
    fun `deve reutilizar em detalhes a identidade retornada pela pesquisa`() = runTest {
        val api = RecordingAnimeApi(
            searchResult = NetworkResult.Success(
                AnimeSearchPageDto(
                    pageInfo = PageInfoDto(
                        currentPage = 1,
                        lastPage = 1,
                        hasNextPage = false,
                        perPage = 20,
                        total = 1,
                    ),
                    media = listOf(
                        AnimeSearchItemDto(
                            id = 21579,
                            title = AnimeTitleDto(romaji = "Anime pesquisado"),
                        ),
                    ),
                ),
            ),
            detailsResult = NetworkResult.Success(
                AnimeDetailsDto(
                    id = 21579,
                    title = AnimeTitleDto(romaji = "Anime detalhado"),
                ),
            ),
        )
        val repository = AniListAnimeRepository(api)

        val search = assertIs<RepositoryResult.Success<PagedResult<AnimeSummary>>>(
            repository.searchAnime("Anime", page = 1, perPage = 20),
        )
        val selectedId = search.value.items.single().id
        val details = assertIs<RepositoryResult.Success<AnimeDetails>>(
            repository.getAnimeDetails(selectedId),
        )

        assertEquals(CatalogAnimeId("21579"), selectedId)
        assertEquals(selectedId, details.value.id)
        assertEquals(21579, api.requestedDetailsId)
    }

    @Test
    fun `nao deve chamar api quando identidade nao for compativel com AniList`() = runTest {
        listOf("catalog-key", "0", "-1", "2147483648", "+1", " 20 ").forEach { value ->
            val api = RecordingAnimeApi()
            val repository = AniListAnimeRepository(api)

            val result = repository.getAnimeDetails(CatalogAnimeId(value))

            val failure = assertIs<RepositoryResult.Failure>(result)
            assertEquals(RepositoryFailure.InvalidInput, failure.reason)
            assertEquals(null, api.requestedDetailsId)
        }
    }

    @Test
    fun `deve converter falhas de rede e preservar suas causas`() = runTest {
        val cases = listOf(
            NetworkFailure.InvalidRequest("Invalid request", TestException()) to
                    RepositoryFailure.InvalidInput,
            NetworkFailure.Timeout(TestException()) to
                    RepositoryFailure.Timeout,
            NetworkFailure.Connection(TestException()) to
                    RepositoryFailure.Unavailable,
            NetworkFailure.Http(429, TestException()) to
                    RepositoryFailure.RateLimited,
            NetworkFailure.Http(503, TestException()) to
                    RepositoryFailure.Unavailable,
            NetworkFailure.Http(400, TestException()) to
                    RepositoryFailure.RemoteFailure,
            NetworkFailure.GraphQl(listOf("GraphQL error"), TestException()) to
                    RepositoryFailure.RemoteFailure,
            NetworkFailure.Serialization(TestException()) to
                    RepositoryFailure.InvalidData,
            NetworkFailure.InvalidResponse("Invalid response", TestException()) to
                    RepositoryFailure.InvalidData,
            NetworkFailure.Unknown(TestException()) to
                    RepositoryFailure.Unknown,
        )

        cases.forEach { (networkFailure, expectedFailure) ->
            val api = RecordingAnimeApi(
                searchResult = NetworkResult.Failure(networkFailure),
            )
            val repository = AniListAnimeRepository(api)

            val result = repository.searchAnime("Anime", 1, 20)

            val failure = assertIs<RepositoryResult.Failure>(result)
            assertEquals(expectedFailure, failure.reason)
            assertSame(networkFailure.cause, failure.cause)
        }
    }

    @Test
    fun `deve retornar invalid data quando pesquisa nao puder ser mapeada`() = runTest {
        val api = RecordingAnimeApi(
            searchResult = NetworkResult.Success(
                AnimeSearchPageDto(
                    pageInfo = null,
                    media = emptyList(),
                ),
            ),
        )
        val repository = AniListAnimeRepository(api)

        val result = repository.searchAnime("Anime", 1, 20)

        val failure = assertIs<RepositoryResult.Failure>(result)
        assertEquals(RepositoryFailure.InvalidData, failure.reason)
        assertIs<IllegalArgumentException>(failure.cause)
    }

    @Test
    fun `deve retornar invalid data quando detalhes nao puderem ser mapeados`() = runTest {
        val api = RecordingAnimeApi(
            detailsResult = NetworkResult.Success(
                AnimeDetailsDto(
                    id = 0,
                    title = AnimeTitleDto(),
                ),
            ),
        )
        val repository = AniListAnimeRepository(api)

        val result = repository.getAnimeDetails(CatalogAnimeId("5114"))

        val failure = assertIs<RepositoryResult.Failure>(result)
        assertEquals(RepositoryFailure.InvalidData, failure.reason)
        assertIs<IllegalArgumentException>(failure.cause)
    }

    @Test
    fun `deve propagar cancelamento da api`() = runTest {
        val cancellation = CancellationException("Cancelled")
        val repository = AniListAnimeRepository(
            ThrowingAnimeApi(cancellation),
        )

        val thrown = assertFailsWith<CancellationException> {
            repository.searchAnime("Anime", 1, 20)
        }

        assertSame(cancellation, thrown)
    }

    @Test
    fun `deve propagar excecao inesperada da api`() = runTest {
        val defect = IllegalStateException("Unexpected defect")
        val repository = AniListAnimeRepository(
            ThrowingAnimeApi(defect),
        )

        val thrown = assertFailsWith<IllegalStateException> {
            repository.getAnimeDetails(CatalogAnimeId("5114"))
        }

        assertSame(defect, thrown)
    }

    @Test
    fun `deve converter falha de rede ao buscar detalhes`() = runTest {
        val cause = TestException()
        val api = RecordingAnimeApi(
            detailsResult = NetworkResult.Failure(
                NetworkFailure.Timeout(cause),
            ),
        )
        val repository = AniListAnimeRepository(api)

        val result = repository.getAnimeDetails(CatalogAnimeId("5114"))

        val failure = assertIs<RepositoryResult.Failure>(result)
        assertEquals(RepositoryFailure.Timeout, failure.reason)
        assertSame(cause, failure.cause)
        assertEquals(5114, api.requestedDetailsId)
    }

    @Test
    fun `deve delegar getTrendingAnime para api com sucesso`() = runTest {
        val api = RecordingAnimeApi()
        val repository = AniListAnimeRepository(api)

        val result = repository.getTrendingAnime(page = 1, perPage = 10)

        assertIs<RepositoryResult.Success<*>>(result)
        assertEquals(1, api.trendingCallCount)
        assertEquals(1, api.lastPage)
        assertEquals(10, api.lastPerPage)
    }

    @Test
    fun `deve validar argumentos invalidos em getTrendingAnime`() = runTest {
        val api = RecordingAnimeApi()
        val repository = AniListAnimeRepository(api)

        val resultPage = repository.getTrendingAnime(page = 0, perPage = 10)
        val failurePage = assertIs<RepositoryResult.Failure>(resultPage)
        assertEquals(RepositoryFailure.InvalidInput, failurePage.reason)

        val resultPerPage = repository.getTrendingAnime(page = 1, perPage = 0)
        val failurePerPage = assertIs<RepositoryResult.Failure>(resultPerPage)
        assertEquals(RepositoryFailure.InvalidInput, failurePerPage.reason)
        assertEquals(0, api.trendingCallCount)
    }

    private class ThrowingAnimeApi(
        private val cause: Throwable,
    ) : AnimeApi {

        override suspend fun searchAnime(
            search: String,
            page: Int,
            perPage: Int,
        ): NetworkResult<AnimeSearchPageDto> =
            throw cause

        override suspend fun getAnimeDetails(
            id: Int,
        ): NetworkResult<AnimeDetailsDto> =
            throw cause

        override suspend fun getTrendingAnime(
            page: Int,
            perPage: Int,
        ): NetworkResult<AnimeSearchPageDto> =
            throw cause
    }

    @Test
    fun `searchAnime grava no cache em caso de sucesso e reutiliza cache dentro do TTL`() = runTest {
        val cache = InMemoryCatalogCacheLocalDataSource()
        val api = RecordingAnimeApi(
            searchResult = NetworkResult.Success(
                AnimeSearchPageDto(
                    pageInfo = PageInfoDto(1, 1, false, 20, 1),
                    media = listOf(
                        AnimeSearchItemDto(id = 10, title = AnimeTitleDto(romaji = "Naruto")),
                    ),
                ),
            ),
        )
        var currentTime = 1000L
        val repository = AniListAnimeRepository(
            animeApi = api,
            catalogCacheDataSource = cache,
            timeProvider = { currentTime },
        )

        // Primeira chamada busca na rede e popula o cache
        val result1 = repository.searchAnime("Naruto", 1, 20)
        assertIs<RepositoryResult.Success<*>>(result1)
        assertEquals(1, api.searchCallCount)

        // Segunda chamada dentro do TTL reutiliza o cache sem chamar a rede
        currentTime += 60_000L
        val result2 = repository.searchAnime("Naruto", 1, 20)
        assertIs<RepositoryResult.Success<*>>(result2)
        assertEquals(1, api.searchCallCount)
    }

    @Test
    fun `searchAnime usa cache como fallback offline quando rede falha`() = runTest {
        val cache = InMemoryCatalogCacheLocalDataSource()
        val successApi = RecordingAnimeApi(
            searchResult = NetworkResult.Success(
                AnimeSearchPageDto(
                    pageInfo = PageInfoDto(1, 1, false, 20, 1),
                    media = listOf(
                        AnimeSearchItemDto(id = 10, title = AnimeTitleDto(romaji = "Naruto")),
                    ),
                ),
            ),
        )
        var currentTime = 1000L
        val repositorySuccess = AniListAnimeRepository(
            animeApi = successApi,
            catalogCacheDataSource = cache,
            timeProvider = { currentTime },
        )

        // Popula cache
        repositorySuccess.searchAnime("Naruto", 1, 20)

        // Simula passagem de tempo para expirar o cache e falha de rede
        currentTime += 100_000_000L
        val failingApi = RecordingAnimeApi(
            searchResult = NetworkResult.Failure(NetworkFailure.Connection(Exception("Sem conexao"))),
        )
        val repositoryFailing = AniListAnimeRepository(
            animeApi = failingApi,
            catalogCacheDataSource = cache,
            timeProvider = { currentTime },
        )

        val result = repositoryFailing.searchAnime("Naruto", 1, 20)
        val success = assertIs<RepositoryResult.Success<*>>(result)
        val page = assertIs<PagedResult<*>>(success.value)
        assertEquals(1, page.items.size)
    }

    @Test
    fun `getAnimeDetails grava no cache e reutiliza dentro do TTL`() = runTest {
        val cache = InMemoryCatalogCacheLocalDataSource()
        val api = RecordingAnimeApi(
            detailsResult = NetworkResult.Success(
                AnimeDetailsDto(id = 5114, title = AnimeTitleDto(romaji = "Fullmetal Alchemist")),
            ),
        )
        var currentTime = 1000L
        val repository = AniListAnimeRepository(
            animeApi = api,
            catalogCacheDataSource = cache,
            timeProvider = { currentTime },
        )

        val result1 = repository.getAnimeDetails(CatalogAnimeId("5114"))
        assertIs<RepositoryResult.Success<*>>(result1)
        assertEquals(5114, api.requestedDetailsId)

        // Segunda consulta dentro do TTL
        val result2 = repository.getAnimeDetails(CatalogAnimeId("5114"))
        assertIs<RepositoryResult.Success<*>>(result2)
        val details = (result2 as RepositoryResult.Success<AnimeDetails>).value
        assertEquals("5114", details.id.value)
    }

    @Test
    fun `getAnimeDetails usa cache como fallback offline diante de erro de rede`() = runTest {
        val cache = InMemoryCatalogCacheLocalDataSource()
        val successApi = RecordingAnimeApi(
            detailsResult = NetworkResult.Success(
                AnimeDetailsDto(id = 5114, title = AnimeTitleDto(romaji = "Fullmetal Alchemist")),
            ),
        )
        var currentTime = 1000L
        val repositorySuccess = AniListAnimeRepository(
            animeApi = successApi,
            catalogCacheDataSource = cache,
            timeProvider = { currentTime },
        )

        repositorySuccess.getAnimeDetails(CatalogAnimeId("5114"))

        // Simula tempo expirado e erro de rede
        currentTime += 100_000_000L
        val failingApi = RecordingAnimeApi(
            detailsResult = NetworkResult.Failure(NetworkFailure.Connection(Exception("Sem conexao"))),
        )
        val repositoryFailing = AniListAnimeRepository(
            animeApi = failingApi,
            catalogCacheDataSource = cache,
            timeProvider = { currentTime },
        )

        val result = repositoryFailing.getAnimeDetails(CatalogAnimeId("5114"))
        assertIs<RepositoryResult.Success<*>>(result)
        val details = (result as RepositoryResult.Success<AnimeDetails>).value
        assertEquals("5114", details.id.value)
    }

    @Test
    fun `getAnimeDetails falha se rede falhar e nao houver cache`() = runTest {
        val cache = InMemoryCatalogCacheLocalDataSource()
        val failingApi = RecordingAnimeApi(
            detailsResult = NetworkResult.Failure(NetworkFailure.Connection(Exception("Sem conexao"))),
        )
        val repository = AniListAnimeRepository(
            animeApi = failingApi,
            catalogCacheDataSource = cache,
        )

        val result = repository.getAnimeDetails(CatalogAnimeId("5114"))
        assertIs<RepositoryResult.Failure>(result)
    }

    private class RecordingAnimeApi(
        private val searchResult: NetworkResult<AnimeSearchPageDto> =
            NetworkResult.Success(
                AnimeSearchPageDto(
                    pageInfo = PageInfoDto(
                        currentPage = 1,
                        lastPage = 1,
                        hasNextPage = false,
                        perPage = 20,
                        total = 0,
                    ),
                    media = emptyList(),
                ),
            ),
        private val detailsResult: NetworkResult<AnimeDetailsDto>? = null,
    ) : AnimeApi {

        var requestedDetailsId: Int? = null
            private set

        var searchCallCount = 0
            private set

        var trendingCallCount = 0
            private set

        var lastSearch: String? = null
            private set

        var lastPage: Int? = null
            private set

        var lastPerPage: Int? = null
            private set

        override suspend fun searchAnime(
            search: String,
            page: Int,
            perPage: Int,
        ): NetworkResult<AnimeSearchPageDto> {
            searchCallCount++
            lastSearch = search
            lastPage = page
            lastPerPage = perPage

            return searchResult
        }

        override suspend fun getTrendingAnime(
            page: Int,
            perPage: Int,
        ): NetworkResult<AnimeSearchPageDto> {
            trendingCallCount++
            lastPage = page
            lastPerPage = perPage
            return searchResult
        }

        override suspend fun getAnimeDetails(
            id: Int,
        ): NetworkResult<AnimeDetailsDto> {
            requestedDetailsId = id

            return requireNotNull(detailsResult) {
                "Resultado de detalhes não configurado para este teste."
            }
        }
    }

    private class TestException : Exception()
}
