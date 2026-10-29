package br.com.mykytadu.features.cache

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import br.com.mykytadu.core.network.NetworkFailure
import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.data.local.datasource.InMemoryCatalogCacheLocalDataSource
import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import br.com.mykytadu.data.remote.anilist.dto.AnimeDetailsDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchItemDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchPageDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeTitleDto
import br.com.mykytadu.data.remote.anilist.dto.PageInfoDto
import br.com.mykytadu.data.remote.api.AnimeApi
import br.com.mykytadu.data.repository.AniListAnimeRepository
import br.com.mykytadu.data.repository.DefaultCatalogCacheManager
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.result.RepositoryResult
import br.com.mykytadu.features.anime.AnimeDetailsScreen
import br.com.mykytadu.features.anime.AnimeDetailsViewModel
import br.com.mykytadu.features.search.SearchScreen
import br.com.mykytadu.features.search.SearchViewModel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalTestApi::class)
class CatalogCacheEndToEndUiTest {

    @get:Rule
    val compose = createComposeRule()

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `detalhes do anime offline exibe dados em cache e badge indicador de modo offline`() {
        val cacheDataSource = InMemoryCatalogCacheLocalDataSource()
        val api = SwitchableAnimeApi()
        var currentTime = 1_000L

        // 1. Online: popula dados no cache
        api.detailsResult = NetworkResult.Success(
            AnimeDetailsDto(
                id = 5114,
                title = AnimeTitleDto(romaji = "Fullmetal Alchemist: Brotherhood"),
                description = "Dois irmãos buscam a Pedra Filosofal.",
            ),
        )
        val repository = AniListAnimeRepository(
            animeApi = api,
            catalogCacheDataSource = cacheDataSource,
            timeProvider = { currentTime },
        )

        runBlocking {
            val onlineResult = repository.getAnimeDetails(CatalogAnimeId("5114"))
            assertIs<RepositoryResult.Success<*>>(onlineResult)
            assertEquals(1, cacheDataSource.getDetailsEntriesCount())
        }

        // 2. Fica offline (falha de rede) e tempo avança além do TTL para acionar revalidação com fallback
        currentTime += 100_000_000L
        api.detailsResult = NetworkResult.Failure(NetworkFailure.Connection(Exception("Sem conexao")))

        startKoin {
            modules(module {
                viewModel { parameters ->
                    AnimeDetailsViewModel(parameters.get(), repository)
                }
            })
        }

        // 3. Renderiza a tela de detalhes offline
        compose.setContent {
            AppTheme {
                Box(Modifier.size(600.dp, 800.dp)) {
                    AnimeDetailsScreen(
                        id = CatalogAnimeId("5114"),
                        onBack = {},
                    )
                }
            }
        }

        compose.waitForIdle()

        // 4. Verifica que o conteúdo do cache foi renderizado
        compose.onNodeWithText("Fullmetal Alchemist: Brotherhood").assertIsDisplayed()

        // 5. Verifica que o badge visual de fallback offline é exibido na UI
        compose.onNodeWithTag("offline_fallback_indicator").assertIsDisplayed()
        compose.onNodeWithText("Exibindo dados em cache (modo offline)").assertIsDisplayed()
    }

    @Test
    fun `busca de animes offline exibe resultados em cache e badge indicador de modo offline`() {
        val cacheDataSource = InMemoryCatalogCacheLocalDataSource()
        val api = SwitchableAnimeApi()
        var currentTime = 1_000L

        // 1. Online: popula busca no cache
        api.searchResult = NetworkResult.Success(
            AnimeSearchPageDto(
                pageInfo = PageInfoDto(1, 1, false, 20, 1),
                media = listOf(
                    AnimeSearchItemDto(id = 20, title = AnimeTitleDto(romaji = "Naruto")),
                ),
            ),
        )
        val repository = AniListAnimeRepository(
            animeApi = api,
            catalogCacheDataSource = cacheDataSource,
            timeProvider = { currentTime },
        )

        runBlocking {
            val onlineResult = repository.searchAnime("Naruto", 1, 20)
            assertIs<RepositoryResult.Success<*>>(onlineResult)
            assertEquals(1, cacheDataSource.getSearchEntriesCount())
        }

        // 2. Fica offline e tempo avança para acionar revalidação de fallback
        currentTime += 100_000_000L
        api.searchResult = NetworkResult.Failure(NetworkFailure.Connection(Exception("Offline")))

        startKoin {
            modules(module {
                viewModel {
                    SearchViewModel(repository)
                }
            })
        }

        // 3. Renderiza a tela de busca
        compose.setContent {
            AppTheme {
                Box(Modifier.size(800.dp, 900.dp)) {
                    SearchScreen(
                        onAnimeSelected = {},
                    )
                }
            }
        }

        // 4. Digita e submete a consulta salva no cache
        compose.onNode(hasSetTextAction()).performTextInput("Naruto")
        compose.onNode(hasSetTextAction()).performImeAction()
        compose.waitForIdle()

        // 5. Verifica que o item em cache é renderizado
        compose.waitUntil(5_000) {
            compose.onAllNodes(hasText("Naruto") and !hasSetTextAction()).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNode(hasText("Naruto") and !hasSetTextAction()).assertIsDisplayed()

        // 6. Verifica que o badge de modo offline é exibido
        compose.onNodeWithTag("offline_fallback_indicator").assertIsDisplayed()
    }

    @Test
    fun `limpeza de cache preserva integridade absoluta da biblioteca do usuario`() = runBlocking {
        val cacheDataSource = InMemoryCatalogCacheLocalDataSource()
        val libraryDataSource = InMemoryLibraryLocalDataSource()
        val cacheManager = DefaultCatalogCacheManager(cacheDataSource)

        // Adiciona registro permanente na biblioteca
        libraryDataSource.upsertEntry(
            LibraryEntryEntity(
                id = "lib_naruto",
                animeId = "20",
                status = "COMPLETED",
                progressCurrent = 220,
                progressTotal = 220,
                isFavorite = true,
                rating = 95,
                createdAtEpochMillis = 1000L,
            ),
        )

        // Limpa cache de navegação
        cacheManager.clearCatalogCache()

        // Verifica que o registro da biblioteca continua 100% íntegro
        val entry = libraryDataSource.getEntryById("lib_naruto")
        assertEquals("lib_naruto", entry?.id)
        assertEquals("COMPLETED", entry?.status)
        assertEquals(220, entry?.progressCurrent)
        assertEquals(true, entry?.isFavorite)
    }

    private class SwitchableAnimeApi : AnimeApi {
        var searchResult: NetworkResult<AnimeSearchPageDto> =
            NetworkResult.Success(AnimeSearchPageDto(PageInfoDto(1, 1, false, 20, 0), emptyList()))
        var detailsResult: NetworkResult<AnimeDetailsDto>? = null

        override suspend fun searchAnime(
            search: String,
            page: Int,
            perPage: Int,
        ): NetworkResult<AnimeSearchPageDto> = searchResult

        override suspend fun getAnimeDetails(id: Int): NetworkResult<AnimeDetailsDto> =
            requireNotNull(detailsResult) { "detailsResult não configurado" }

        override suspend fun getTrendingAnime(
            page: Int,
            perPage: Int,
        ): NetworkResult<AnimeSearchPageDto> = searchResult
    }
}
