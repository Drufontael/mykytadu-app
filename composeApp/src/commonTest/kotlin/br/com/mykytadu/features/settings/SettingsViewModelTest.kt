package br.com.mykytadu.features.settings

import br.com.mykytadu.data.local.datasource.InMemoryPreferencesLocalDataSource
import br.com.mykytadu.data.repository.DefaultPreferencesRepository
import br.com.mykytadu.domain.model.AppLanguagePreference
import br.com.mykytadu.domain.model.ThemePreference
import br.com.mykytadu.domain.model.TitleLanguagePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import br.com.mykytadu.data.local.datasource.InMemoryCatalogCacheLocalDataSource
import br.com.mykytadu.data.repository.DefaultCatalogCacheManager
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState inicial reflete valores padrao de preferencias`() = runTest {
        val repository = DefaultPreferencesRepository(InMemoryPreferencesLocalDataSource())
        val viewModel = SettingsViewModel(repository)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ThemePreference.SYSTEM, state.theme)
        assertEquals(TitleLanguagePreference.ROMAJI, state.titleLanguage)
        assertEquals(false, state.adultContent)
        assertEquals(AppLanguagePreference.SYSTEM, state.appLanguage)
        assertEquals("MykytaDu", state.appName)
        assertEquals("1.0.0", state.appVersion)
    }

    @Test
    fun `setTheme atualiza tema no repositorio e reflete no uiState`() = runTest {
        val repository = DefaultPreferencesRepository(InMemoryPreferencesLocalDataSource())
        val viewModel = SettingsViewModel(repository)

        advanceUntilIdle()

        viewModel.setTheme(ThemePreference.DARK)
        advanceUntilIdle()

        assertEquals(ThemePreference.DARK, viewModel.uiState.value.theme)
    }

    @Test
    fun `setTitleLanguage atualiza preferencia de idioma de titulos e reflete no uiState`() = runTest {
        val repository = DefaultPreferencesRepository(InMemoryPreferencesLocalDataSource())
        val viewModel = SettingsViewModel(repository)

        advanceUntilIdle()

        viewModel.setTitleLanguage(TitleLanguagePreference.ENGLISH)
        advanceUntilIdle()

        assertEquals(TitleLanguagePreference.ENGLISH, viewModel.uiState.value.titleLanguage)
    }

    @Test
    fun `setAdultContent atualiza flag de conteudo adulto e reflete no uiState`() = runTest {
        val repository = DefaultPreferencesRepository(InMemoryPreferencesLocalDataSource())
        val viewModel = SettingsViewModel(repository)

        advanceUntilIdle()

        viewModel.setAdultContent(true)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.adultContent)
    }

    @Test
    fun `setAppLanguage atualiza idioma da aplicacao e reflete no uiState`() = runTest {
        val repository = DefaultPreferencesRepository(InMemoryPreferencesLocalDataSource())
        val viewModel = SettingsViewModel(repository)

        advanceUntilIdle()

        viewModel.setAppLanguage(AppLanguagePreference.PT_BR)
        advanceUntilIdle()

        assertEquals(AppLanguagePreference.PT_BR, viewModel.uiState.value.appLanguage)
    }

    @Test
    fun `clearCatalogCache invoca cacheManager e reflete mensagem de sucesso no uiState`() = runTest {
        val repository = DefaultPreferencesRepository(InMemoryPreferencesLocalDataSource())
        val cacheDataSource = InMemoryCatalogCacheLocalDataSource()
        val cacheManager = DefaultCatalogCacheManager(cacheDataSource)
        val viewModel = SettingsViewModel(repository, cacheManager)

        advanceUntilIdle()

        viewModel.clearCatalogCache()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.cacheClearedMessage)

        viewModel.dismissCacheClearedMessage()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.cacheClearedMessage)
    }
}
