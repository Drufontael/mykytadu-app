package br.com.mykytadu.features.library

import br.com.mykytadu.data.local.datasource.InMemoryLibraryLocalDataSource
import br.com.mykytadu.data.local.datasource.LibraryLocalDataSource
import br.com.mykytadu.data.local.entity.LibraryEntryEntity
import br.com.mykytadu.data.repository.DefaultLibraryRepository
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryEntry
import br.com.mykytadu.domain.model.LibraryEntryId
import br.com.mykytadu.domain.model.LibraryProgress
import br.com.mykytadu.domain.model.LibraryRating
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.domain.repository.LibraryRepository
import br.com.mykytadu.domain.result.RepositoryResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LibraryIntegrationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Test
    fun `deve gerenciar ciclo de vida completo de uma entrada na biblioteca de ponta a ponta`() = runTest {
        val dataSource: LibraryLocalDataSource = InMemoryLibraryLocalDataSource()
        val repository: LibraryRepository = DefaultLibraryRepository(localDataSource = dataSource)

        val animeId = CatalogAnimeId("101")
        val entryId = LibraryEntryId("lib-101")
        val initialTime = 1700000000000L

        // 1. Criar entrada em PLANNING
        val initialEntry = LibraryEntry(
            id = entryId,
            animeId = animeId,
            status = LibraryStatus.PLANNING,
            progress = LibraryProgress(current = 0, total = 24),
            rating = null,
            notes = "Planejando assistir nas férias",
            createdAtEpochMillis = initialTime,
            updatedAtEpochMillis = initialTime,
        )

        val saveResult = repository.saveEntry(initialEntry)
        assertIs<RepositoryResult.Success<Unit>>(saveResult)

        // Observar imediatamente
        val observedFirst = repository.observeEntryByAnimeId(animeId).first()
        assertNotNull(observedFirst)
        assertEquals(LibraryStatus.PLANNING, observedFirst.status)
        assertEquals(0, observedFirst.progress.current)

        // 2. Atualizar para WATCHING e avançar progresso
        val stepTime1 = 1700001000000L
        val watchingEntry = observedFirst
            .withStatus(LibraryStatus.WATCHING, stepTime1)
            .withProgress(observedFirst.progress.withCurrent(5), stepTime1)

        val updateResult1 = repository.saveEntry(watchingEntry)
        assertIs<RepositoryResult.Success<Unit>>(updateResult1)

        val observedWatching = repository.observeEntryByAnimeId(animeId).first()
        assertNotNull(observedWatching)
        assertEquals(LibraryStatus.WATCHING, observedWatching.status)
        assertEquals(5, observedWatching.progress.current)

        // 3. Atribuir nota pessoal e notas detalhadas
        val stepTime2 = 1700002000000L
        val ratedEntry = observedWatching
            .withRating(LibraryRating(90), stepTime2)
            .withNotes("Episódio 5 excelente. Animação fluida.", stepTime2)

        val updateResult2 = repository.saveEntry(ratedEntry)
        assertIs<RepositoryResult.Success<Unit>>(updateResult2)

        val observedRated = repository.observeEntryByAnimeId(animeId).first()
        assertNotNull(observedRated)
        assertEquals(90, observedRated.rating?.value)
        assertEquals("Episódio 5 excelente. Animação fluida.", observedRated.notes)

        // 4. Concluir a obra
        val stepTime3 = 1700003000000L
        val completedEntry = observedRated
            .withProgress(LibraryProgress(current = 24, total = 24), stepTime3)
            .withStatus(LibraryStatus.COMPLETED, stepTime3)

        repository.saveEntry(completedEntry)

        val completedList = repository.observeEntriesByStatus(LibraryStatus.COMPLETED).first()
        assertEquals(1, completedList.size)
        assertEquals(animeId, completedList.first().animeId)
        assertEquals(24, completedList.first().progress.current)
        assertEquals(1.0f, completedList.first().progress.completionRatio)
    }

    @Test
    fun `deve manter a integridade dos dados apos simulacao de persistencia e reinicializacao da fonte de dados`() = runTest {
        // Fase 1: Criar dados no storage inicial
        val initialDataSource = InMemoryLibraryLocalDataSource()
        val initialRepository = DefaultLibraryRepository(localDataSource = initialDataSource)

        val entry1 = LibraryEntry(
            id = LibraryEntryId("lib-1"),
            animeId = CatalogAnimeId("anime-1"),
            status = LibraryStatus.WATCHING,
            progress = LibraryProgress(current = 12, total = 24),
            rating = LibraryRating(88),
            notes = "Primeira temporada concluída.",
            createdAtEpochMillis = 1700000000000L,
            updatedAtEpochMillis = 1700005000000L,
        )
        val entry2 = LibraryEntry(
            id = LibraryEntryId("lib-2"),
            animeId = CatalogAnimeId("anime-2"),
            status = LibraryStatus.COMPLETED,
            progress = LibraryProgress(current = 12, total = 12),
            rating = LibraryRating(95),
            notes = "Obra-prima imperdível.",
            createdAtEpochMillis = 1700000000000L,
            updatedAtEpochMillis = 1700008000000L,
        )

        initialRepository.saveEntry(entry1)
        initialRepository.saveEntry(entry2)

        // Fase 2: Simular desligamento / persistência de disco (dump em JSON das entidades salvas)
        val rawEntitiesBeforeRestart: List<LibraryEntryEntity> = initialDataSource.getAllEntries()
        assertEquals(2, rawEntitiesBeforeRestart.size)

        val serializedDump = json.encodeToString(rawEntitiesBeforeRestart)
        assertTrue(serializedDump.contains("lib-1"))
        assertTrue(serializedDump.contains("lib-2"))

        // Fase 3: Simular reinicialização completa do aplicativo com nova instância de datasource e repository
        val restoredEntities: List<LibraryEntryEntity> = json.decodeFromString(serializedDump)
        val restartedDataSource = InMemoryLibraryLocalDataSource()

        for (entity in restoredEntities) {
            restartedDataSource.upsertEntry(entity)
        }

        val restartedRepository = DefaultLibraryRepository(localDataSource = restartedDataSource)

        // Fase 4: Validar que os dados lidos pelo novo repositório mantêm 100% de integridade funcional e de schema
        val allEntriesResult = restartedRepository.getAllEntries()
        assertIs<RepositoryResult.Success<List<LibraryEntry>>>(allEntriesResult)
        assertEquals(2, allEntriesResult.value.size)

        val restoredEntry1 = restartedRepository.getEntryByAnimeId(CatalogAnimeId("anime-1"))
        assertIs<RepositoryResult.Success<LibraryEntry?>>(restoredEntry1)
        val value1 = restoredEntry1.value
        assertNotNull(value1)
        assertEquals(LibraryEntryId("lib-1"), value1.id)
        assertEquals(LibraryStatus.WATCHING, value1.status)
        assertEquals(12, value1.progress.current)
        assertEquals(24, value1.progress.total)
        assertEquals(88, value1.rating?.value)
        assertEquals("Primeira temporada concluída.", value1.notes)
        assertEquals(1700000000000L, value1.createdAtEpochMillis)
        assertEquals(1700005000000L, value1.updatedAtEpochMillis)

        val restoredEntry2 = restartedRepository.getEntryByAnimeId(CatalogAnimeId("anime-2"))
        assertIs<RepositoryResult.Success<LibraryEntry?>>(restoredEntry2)
        val value2 = restoredEntry2.value
        assertNotNull(value2)
        assertEquals(LibraryStatus.COMPLETED, value2.status)
        assertEquals(95, value2.rating?.value)
        assertEquals(12, value2.progress.current)

        // Assegurar que consultas filtradas por status continuam funcionando após o restart
        val watchingResult = restartedRepository.getEntriesByStatus(LibraryStatus.WATCHING)
        assertIs<RepositoryResult.Success<List<LibraryEntry>>>(watchingResult)
        assertEquals(1, watchingResult.value.size)
        assertEquals(CatalogAnimeId("anime-1"), watchingResult.value.first().animeId)
    }

    @Test
    fun `deve suportar operacoes simultaneas de remocao e atualizacao reativa`() = runTest {
        val dataSource = InMemoryLibraryLocalDataSource()
        val repository = DefaultLibraryRepository(localDataSource = dataSource)

        val animeId1 = CatalogAnimeId("anime-10")
        val animeId2 = CatalogAnimeId("anime-20")

        repository.saveEntry(
            LibraryEntry(
                id = LibraryEntryId("lib-10"),
                animeId = animeId1,
                status = LibraryStatus.PAUSED,
                progress = LibraryProgress(current = 3, total = 12),
                createdAtEpochMillis = 1700000000000L,
                updatedAtEpochMillis = 1700000000000L,
            )
        )
        repository.saveEntry(
            LibraryEntry(
                id = LibraryEntryId("lib-20"),
                animeId = animeId2,
                status = LibraryStatus.DROPPED,
                progress = LibraryProgress(current = 1, total = 12),
                createdAtEpochMillis = 1700000000000L,
                updatedAtEpochMillis = 1700000000000L,
            )
        )

        assertEquals(2, repository.observeAllEntries().first().size)

        // Remover anime 1
        val deleteResult = repository.deleteEntryByAnimeId(animeId1)
        assertIs<RepositoryResult.Success<Boolean>>(deleteResult)
        assertTrue(deleteResult.value)

        // Observar que apenas anime 2 permaneceu
        val remainingEntries = repository.observeAllEntries().first()
        assertEquals(1, remainingEntries.size)
        assertEquals(animeId2, remainingEntries.first().animeId)

        val nullLookup = repository.getEntryByAnimeId(animeId1)
        assertIs<RepositoryResult.Success<LibraryEntry?>>(nullLookup)
        assertNull(nullLookup.value)
    }
}
