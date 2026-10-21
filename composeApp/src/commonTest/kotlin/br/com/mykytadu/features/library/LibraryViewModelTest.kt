package br.com.mykytadu.features.library

import br.com.mykytadu.core.network.NetworkResult
import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.repository.DefaultLibraryRepository
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeImages
import br.com.mykytadu.domain.model.AnimeTitles
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryProgress
import br.com.mykytadu.domain.model.LibraryRating
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class LibraryViewModelTest {

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

    private fun createViewModel(animeRepository: AnimeRepository? = null): LibraryViewModel {
        return LibraryViewModel(
            libraryRepository = libraryRepository,
            animeRepository = animeRepository,
            dispatcher = testDispatcher,
            timeProvider = { currentTime },
        )
    }

    @Test
    fun `deve iniciar e emitir Content com contagens por status corretas`() = runTest(testDispatcher) {
        val entry1 = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("1"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 2, total = 12),
            createdAtEpochMillis = 1700000000000L,
        )
        val entry2 = LibraryEntry(
            id = LibraryEntryId("lib-2"),
            animeId = CatalogAnimeId("2"),
            status = LibraryStatus.COMPLETED,
            progress = LibraryProgress(current = 12, total = 12),
            createdAtEpochMillis = 1700000000000L,
        )
        libraryRepository.saveEntry(entry1)
        libraryRepository.saveEntry(entry2)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<LibraryUiState.Content>(state)
        assertEquals(2, state.items.size)
        assertEquals(2, state.statusCounts[LibraryFilter.ALL])
        assertEquals(1, state.statusCounts[LibraryFilter.WATCHING])
        assertEquals(1, state.statusCounts[LibraryFilter.COMPLETED])
        assertEquals(0, state.statusCounts[LibraryFilter.PLANNING])
        assertFalse(state.isLibraryEmpty)
        assertFalse(state.isFilterEmpty)
    }

    @Test
    fun `deve filtrar por status selecionado`() = runTest(testDispatcher) {
        val entry1 = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("1"),
            status = LibraryStatus.WATCHING,
            createdAtEpochMillis = 1700000000000L,
        )
        val entry2 = LibraryEntry(
            id = LibraryEntryId("lib-2"),
            animeId = CatalogAnimeId("2"),
            status = LibraryStatus.PLANNING,
            createdAtEpochMillis = 1700000000000L,
        )
        libraryRepository.saveEntry(entry1)
        libraryRepository.saveEntry(entry2)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.setFilter(LibraryFilter.WATCHING)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<LibraryUiState.Content>(state)
        assertEquals(1, state.items.size)
        assertEquals(LibraryEntryId("lib-1"), state.items.first().id)
        assertEquals(LibraryStatus.WATCHING, state.items.first().status)
    }

    @Test
    fun `deve ordenar por titulo, rating e atualizacao`() = runTest(testDispatcher) {
        val fakeAnimeRepo = FakeAnimeRepository(
            mapOf(
                CatalogAnimeId("1") to "Zeta Anime",
                CatalogAnimeId("2") to "Alpha Anime",
            )
        )
        val entry1 = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("1"),
            status = LibraryStatus.WATCHING,
            rating = LibraryRating(70),
            createdAtEpochMillis = 1700000000000L,
            updatedAtEpochMillis = 1700001000000L,
        )
        val entry2 = LibraryEntry(
            id = LibraryEntryId("lib-2"),
            animeId = CatalogAnimeId("2"),
            status = LibraryStatus.WATCHING,
            rating = LibraryRating(90),
            createdAtEpochMillis = 1700000000000L,
            updatedAtEpochMillis = 1700002000000L,
        )
        libraryRepository.saveEntry(entry1)
        libraryRepository.saveEntry(entry2)

        val viewModel = createViewModel(animeRepository = fakeAnimeRepo)
        advanceUntilIdle()

        // 1. Ordenação por Título (A-Z)
        viewModel.setSort(LibrarySort.TITLE)
        advanceUntilIdle()
        var state = viewModel.uiState.value as LibraryUiState.Content
        assertEquals("Alpha Anime", state.items[0].title)
        assertEquals("Zeta Anime", state.items[1].title)

        // 2. Ordenação por Rating (maior para menor)
        viewModel.setSort(LibrarySort.RATING)
        advanceUntilIdle()
        state = viewModel.uiState.value as LibraryUiState.Content
        assertEquals(90, state.items[0].rating)
        assertEquals(70, state.items[1].rating)
    }

    @Test
    fun `deve incrementar e decrementar progresso e persistir`() = runTest(testDispatcher) {
        val entry = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("1"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 3, total = 12),
            createdAtEpochMillis = 1700000000000L,
            updatedAtEpochMillis = 1700000000000L,
        )
        libraryRepository.saveEntry(entry)

        val viewModel = createViewModel()
        advanceUntilIdle()

        currentTime = 1700005000000L
        viewModel.incrementProgress(LibraryEntryId("lib-1"))
        advanceUntilIdle()

        var saved = libraryRepository.getEntryById(LibraryEntryId("lib-1"))
        assertIs<RepositoryResult.Success<LibraryEntry?>>(saved)
        assertEquals(4, saved.value?.progress?.current)
        assertEquals(currentTime, saved.value?.updatedAtEpochMillis)

        viewModel.decrementProgress(LibraryEntryId("lib-1"))
        advanceUntilIdle()

        saved = libraryRepository.getEntryById(LibraryEntryId("lib-1"))
        assertIs<RepositoryResult.Success<LibraryEntry?>>(saved)
        assertEquals(3, saved.value?.progress?.current)
    }

    @Test
    fun `deve alternar favorito`() = runTest(testDispatcher) {
        val entry = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("1"),
            status = LibraryStatus.WATCHING,
            isFavorite = false,
            createdAtEpochMillis = 1700000000000L,
        )
        libraryRepository.saveEntry(entry)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.toggleFavorite(LibraryEntryId("lib-1"))
        advanceUntilIdle()

        val saved = libraryRepository.getEntryById(LibraryEntryId("lib-1"))
        assertIs<RepositoryResult.Success<LibraryEntry?>>(saved)
        assertTrue(saved.value?.isFavorite == true)
    }

    @Test
    fun `deve atualizar status, nota e notas textuais`() = runTest(testDispatcher) {
        val entry = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("1"),
            status = LibraryStatus.PLANNING,
            createdAtEpochMillis = 1700000000000L,
        )
        libraryRepository.saveEntry(entry)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateStatus(LibraryEntryId("lib-1"), LibraryStatus.COMPLETED)
        viewModel.updateRating(LibraryEntryId("lib-1"), 85)
        viewModel.updateNotes(LibraryEntryId("lib-1"), "Final surpreendente!")
        advanceUntilIdle()

        val saved = libraryRepository.getEntryById(LibraryEntryId("lib-1"))
        assertIs<RepositoryResult.Success<LibraryEntry?>>(saved)
        assertEquals(LibraryStatus.COMPLETED, saved.value?.status)
        assertEquals(85, saved.value?.rating?.value)
        assertEquals("Final surpreendente!", saved.value?.notes)
    }

    @Test
    fun `deve remover entry ao chamar deleteEntry`() = runTest(testDispatcher) {
        val entry = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("1"),
            status = LibraryStatus.DROPPED,
            createdAtEpochMillis = 1700000000000L,
        )
        libraryRepository.saveEntry(entry)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.deleteEntry(LibraryEntryId("lib-1"))
        advanceUntilIdle()

        val saved = libraryRepository.getEntryById(LibraryEntryId("lib-1"))
        assertIs<RepositoryResult.Success<LibraryEntry?>>(saved)
        assertEquals(null, saved.value)
    }

    private class FakeAnimeRepository(
        private val titles: Map<CatalogAnimeId, String> = emptyMap()
    ) : AnimeRepository {
        override suspend fun searchAnime(
            query: String,
            page: Int,
            perPage: Int
        ): RepositoryResult<PagedResult<AnimeSummary>> =
            error("Não utilizado neste teste")

        override suspend fun getAnimeDetails(id: CatalogAnimeId): RepositoryResult<AnimeDetails> {
            val title = titles[id] ?: "Title for ${id.value}"
            return RepositoryResult.Success(
                AnimeDetails(
                    id = id,
                    titles = AnimeTitles(english = title, romaji = title),
                    images = AnimeImages(coverExtraLarge = "https://example.com/cover.jpg"),
                )
            )
        }
    }
}
