package br.com.mykytadu.data.local.datasource

import br.com.mykytadu.data.local.entity.PreferencesEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryPreferencesLocalDataSourceTest {

    @Test
    fun `deve iniciar com entidade padrao caso nao seja fornecida`() = runTest {
        val dataSource = InMemoryPreferencesLocalDataSource()
        val prefs = dataSource.getPreferences()

        assertEquals("SYSTEM", prefs.theme)
        assertEquals("ROMAJI", prefs.titleLanguage)
        assertEquals(false, prefs.adultContent)
        assertEquals("SYSTEM", prefs.appLanguage)
    }

    @Test
    fun `deve persistir e recuperar alteracoes de preferencias`() = runTest {
        val dataSource = InMemoryPreferencesLocalDataSource()
        val nova = PreferencesEntity(
            theme = "DARK",
            titleLanguage = "ENGLISH",
            adultContent = true,
            appLanguage = "PT_BR",
        )

        dataSource.savePreferences(nova)

        val recuperada = dataSource.getPreferences()
        assertEquals("DARK", recuperada.theme)
        assertEquals("ENGLISH", recuperada.titleLanguage)
        assertTrue(recuperada.adultContent)
        assertEquals("PT_BR", recuperada.appLanguage)
    }

    @Test
    fun `observePreferences deve emitir estado inicial e atualizacoes reativas`() = runTest {
        val dataSource = InMemoryPreferencesLocalDataSource()

        val inicial = dataSource.observePreferences().first()
        assertEquals("SYSTEM", inicial.theme)

        dataSource.savePreferences(PreferencesEntity(theme = "LIGHT"))

        val atualizada = dataSource.observePreferences().first()
        assertEquals("LIGHT", atualizada.theme)
    }
}
