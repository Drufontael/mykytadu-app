package br.com.mykytadu.features.home

import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.repository.DefaultLibraryRepository
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeFormat
import br.com.mykytadu.domain.model.AnimeImages
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.AnimeTitles
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryProgress
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.domain.model.PageInfo
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val localDataSource = InMemoryLibraryLocalDataSource()
    private val libraryRepository = DefaultLibraryRepository(localDataSource = localDataSource)
    private var currentTime = 1700000000000L

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(animeRepository: AnimeRepository): HomeViewModel {
        return HomeViewModel(
            libraryRepository = libraryRepository,
            animeRepository = animeRepository,
            dispatcher = testDispatcher,
            timeProvider = { currentTime },
        )
    }

    @Test
    fun `deve iniciar com secoes carregadas quando ha entradas na biblioteca e animes em alta`() = runTest(testDispatcher) {
        val entryWatching = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("101"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 3, total = 12),
            createdAtEpochMillis = 1700000000000L,
            updatedAtEpochMillis = 1700000010000L,
        )
        val entryCompleted = LibraryEntry(
            id = LibraryEntryId("lib-2"),
            animeId = CatalogAnimeId("102"),
            status = LibraryStatus.COMPLETED,
            progress = LibraryProgress(current = 24, total = 24),
            createdAtEpochMillis = 1700000000000L,
            updatedAtEpochMillis = 1700000020000L,
        )
        libraryRepository.saveEntry(entryWatching)
        libraryRepository.saveEntry(entryCompleted)

        val trendingAnime = listOf(
            AnimeSummary(
                id = CatalogAnimeId("201"),
                titles = AnimeTitles(english = "Chainsaw Man"),
                images = AnimeImages(coverLarge = "https://example.com/csm.jpg"),
                format = AnimeFormat.TV,
                averageScore = 86,
                episodes = 12,
            )
        )
        val animeRepo = FakeAnimeRepository(trendingList = trendingAnime)

        val viewModel = createViewModel(animeRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        // Continuar assistindo deve conter apenas o item WATCHING
        assertIs<HomeSectionState.Success<List<HomeContinueWatchingItemUiState>>>(state.continueWatchingSection)
        assertEquals(1, state.continueWatchingSection.data.size)
        val watchingItem = state.continueWatchingSection.data.first()
        assertEquals(CatalogAnimeId("101"), watchingItem.animeId)
        assertEquals(3, watchingItem.progressCurrent)
        assertEquals(12, watchingItem.progressTotal)
        assertTrue(watchingItem.canIncrement)

        // Atualizados recentemente deve conter ambos os itens ordenados por updatedAt (102 mais recente que 101)
        assertIs<HomeSectionState.Success<List<HomeRecentItemUiState>>>(state.recentSection)
        assertEquals(2, state.recentSection.data.size)
        assertEquals(CatalogAnimeId("102"), state.recentSection.data[0].animeId)
        assertEquals(CatalogAnimeId("101"), state.recentSection.data[1].animeId)

        // Tendências deve conter o item retornado pela AniList
        assertIs<HomeSectionState.Success<List<HomeTrendingItemUiState>>>(state.trendingSection)
        assertEquals(1, state.trendingSection.data.size)
        assertEquals(CatalogAnimeId("201"), state.trendingSection.data.first().animeId)
        assertEquals("Chainsaw Man", state.trendingSection.data.first().title)

        assertTrue(state.hasPersonalContent)
        assertFalse(state.isLibraryEmpty)
    }

    @Test
    fun `deve marcar secoes pessoais como Empty quando a biblioteca estiver vazia`() = runTest(testDispatcher) {
        val trendingAnime = listOf(
            AnimeSummary(
                id = CatalogAnimeId("201"),
                titles = AnimeTitles(english = "Attack on Titan"),
            )
        )
        val animeRepo = FakeAnimeRepository(trendingList = trendingAnime)

        val viewModel = createViewModel(animeRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<HomeSectionState.Empty>(state.continueWatchingSection)
        assertIs<HomeSectionState.Empty>(state.recentSection)
        assertIs<HomeSectionState.Success<List<HomeTrendingItemUiState>>>(state.trendingSection)
        assertTrue(state.isLibraryEmpty)
        assertFalse(state.hasPersonalContent)
    }

    @Test
    fun `deve isolar falha na AniList mantendo secoes da biblioteca intactas`() = runTest(testDispatcher) {
        val entry = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("101"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 1, total = 12),
            createdAtEpochMillis = 1700000000000L,
        )
        libraryRepository.saveEntry(entry)

        val animeRepo = FakeAnimeRepository(trendingResult = RepositoryResult.Failure(RepositoryFailure.Unavailable))

        val viewModel = createViewModel(animeRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value

        // Biblioteca local deve carregar com sucesso mesmo com falha de rede da AniList
        assertIs<HomeSectionState.Success<List<HomeContinueWatchingItemUiState>>>(state.continueWatchingSection)
        assertIs<HomeSectionState.Success<List<HomeRecentItemUiState>>>(state.recentSection)

        // Tendências deve refletir o erro de forma isolada
        assertIs<HomeSectionState.Failure>(state.trendingSection)
        assertEquals(RepositoryFailure.Unavailable, state.trendingSection.reason)
    }

    @Test
    fun `deve permitir retry da secao de tendencias apos recuperacao de falha`() = runTest(testDispatcher) {
        val animeRepo = FakeAnimeRepository(trendingResult = RepositoryResult.Failure(RepositoryFailure.Unavailable))

        val viewModel = createViewModel(animeRepo)
        advanceUntilIdle()

        assertIs<HomeSectionState.Failure>(viewModel.uiState.value.trendingSection)

        // Simula recuperação de conexão
        animeRepo.trendingResult = RepositoryResult.Success(
            PagedResult(
                items = listOf(
                    AnimeSummary(
                        id = CatalogAnimeId("301"),
                        titles = AnimeTitles(english = "Demon Slayer"),
                    )
                ),
                pageInfo = PageInfo(currentPage = 1, lastPage = 1, hasNextPage = false, perPage = 10, total = 1),
            )
        )

        viewModel.retryTrending()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<HomeSectionState.Success<List<HomeTrendingItemUiState>>>(state.trendingSection)
        assertEquals(CatalogAnimeId("301"), state.trendingSection.data.first().animeId)
    }

    @Test
    fun `deve incrementar progresso ao chamar incrementProgress`() = runTest(testDispatcher) {
        val entry = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("101"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 5, total = 12),
            createdAtEpochMillis = 1700000000000L,
            updatedAtEpochMillis = 1700000000000L,
        )
        libraryRepository.saveEntry(entry)

        val animeRepo = FakeAnimeRepository()
        val viewModel = createViewModel(animeRepo)
        advanceUntilIdle()

        currentTime = 1700000050000L
        viewModel.incrementProgress(CatalogAnimeId("101"))
        advanceUntilIdle()

        val saved = libraryRepository.getEntryById(LibraryEntryId("lib-1"))
        assertIs<RepositoryResult.Success<LibraryEntry?>>(saved)
        val savedEntry = saved.value
        assertNotNull(savedEntry)
        assertEquals(6, savedEntry.progress.current)
        assertEquals(1700000050000L, savedEntry.updatedAtEpochMillis)

        val watchingItem = (viewModel.uiState.value.continueWatchingSection as HomeSectionState.Success).data.first()
        assertEquals(6, watchingItem.progressCurrent)
    }

    @Test
    fun `nao deve incrementar progresso quando ja tiver atingido o total`() = runTest(testDispatcher) {
        val entry = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("101"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 12, total = 12),
            createdAtEpochMillis = 1700000000000L,
        )
        libraryRepository.saveEntry(entry)

        val animeRepo = FakeAnimeRepository()
        val viewModel = createViewModel(animeRepo)
        advanceUntilIdle()

        val watchingItem = (viewModel.uiState.value.continueWatchingSection as HomeSectionState.Success).data.first()
        assertFalse(watchingItem.canIncrement)

        viewModel.incrementProgress(CatalogAnimeId("101"))
        advanceUntilIdle()

        val saved = libraryRepository.getEntryById(LibraryEntryId("lib-1"))
        assertIs<RepositoryResult.Success<LibraryEntry?>>(saved)
        assertEquals(12, saved.value?.progress?.current)
    }

    @Test
    fun `deve preencher metadados de detalhes assincronamente nas entradas da biblioteca`() = runTest(testDispatcher) {
        val entry = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("500"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 1, total = null), // sem total informado
            createdAtEpochMillis = 1700000000000L,
        )
        libraryRepository.saveEntry(entry)

        val animeRepo = FakeAnimeRepository(
            detailsMap = mapOf(
                CatalogAnimeId("500") to AnimeDetails(
                    id = CatalogAnimeId("500"),
                    titles = AnimeTitles(english = "Frieren: Beyond Journey's End"),
                    images = AnimeImages(coverExtraLarge = "https://example.com/frieren.jpg"),
                    episodes = 28,
                )
            )
        )

        val viewModel = createViewModel(animeRepo)
        advanceUntilIdle()

        val watchingItem = (viewModel.uiState.value.continueWatchingSection as HomeSectionState.Success).data.first()
        assertEquals("Frieren: Beyond Journey's End", watchingItem.title)
        assertEquals("https://example.com/frieren.jpg", watchingItem.coverUrl)
        assertEquals(28, watchingItem.progressTotal)
    }

    @Test
    fun `deve limitar secao de atualizados recentemente a 10 obras`() = runTest(testDispatcher) {
        for (i in 1..15) {
            libraryRepository.saveEntry(
                LibraryEntry(
                    id = LibraryEntryId("lib-$i"),
                    animeId = CatalogAnimeId("$i"),
                    status = LibraryStatus.COMPLETED,
                    createdAtEpochMillis = 1700000000000L,
                    updatedAtEpochMillis = 1700000000000L + i * 1000L,
                )
            )
        }

        val animeRepo = FakeAnimeRepository()
        val viewModel = createViewModel(animeRepo)
        advanceUntilIdle()

        val recentSection = viewModel.uiState.value.recentSection
        assertIs<HomeSectionState.Success<List<HomeRecentItemUiState>>>(recentSection)
        assertEquals(10, recentSection.data.size)
        // O primeiro deve ser o com maior timestamp (i = 15)
        assertEquals(CatalogAnimeId("15"), recentSection.data[0].animeId)
        assertEquals(CatalogAnimeId("6"), recentSection.data[9].animeId)
    }

    private class FakeAnimeRepository(
        var trendingResult: RepositoryResult<PagedResult<AnimeSummary>>? = null,
        trendingList: List<AnimeSummary> = emptyList(),
        private val detailsMap: Map<CatalogAnimeId, AnimeDetails> = emptyMap(),
    ) : AnimeRepository {

        init {
            if (trendingResult == null) {
                trendingResult = RepositoryResult.Success(
                    PagedResult(
                        items = trendingList,
                        pageInfo = PageInfo(
                            currentPage = 1,
                            lastPage = 1,
                            hasNextPage = false,
                            perPage = 10,
                            total = trendingList.size,
                        )
                    )
                )
            }
        }

        override suspend fun getTrendingAnime(
            page: Int,
            perPage: Int
        ): RepositoryResult<PagedResult<AnimeSummary>> =
            trendingResult ?: error("trendingResult não configurado")

        override suspend fun searchAnime(
            query: String,
            page: Int,
            perPage: Int
        ): RepositoryResult<PagedResult<AnimeSummary>> =
            error("Não utilizado neste teste")

        override suspend fun getAnimeDetails(id: CatalogAnimeId): RepositoryResult<AnimeDetails> {
            val details = detailsMap[id] ?: AnimeDetails(
                id = id,
                titles = AnimeTitles(english = "Anime #${id.value}"),
                images = AnimeImages(),
            )
            return RepositoryResult.Success(details)
        }
    }
}
