package br.com.mykytadu.data.mapper

import br.com.mykytadu.data.local.entity.PreferencesEntity
import br.com.mykytadu.domain.model.AppLanguagePreference
import br.com.mykytadu.domain.model.ThemePreference
import br.com.mykytadu.domain.model.TitleLanguagePreference
import br.com.mykytadu.domain.model.UserPreferences
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PreferencesMapperTest {

    @Test
    fun `deve mapear UserPreferences para PreferencesEntity mantendo campos e versao de esquema`() {
        val domain = UserPreferences(
            theme = ThemePreference.DARK,
            titleLanguage = TitleLanguagePreference.ENGLISH,
            adultContent = true,
            appLanguage = AppLanguagePreference.PT_BR,
        )

        val entity = domain.toEntity()

        assertEquals("DARK", entity.theme)
        assertEquals("ENGLISH", entity.titleLanguage)
        assertTrue(entity.adultContent)
        assertEquals("PT_BR", entity.appLanguage)
        assertEquals(PreferencesEntity.CURRENT_SCHEMA_VERSION, entity.schemaVersion)
    }

    @Test
    fun `deve mapear PreferencesEntity para UserPreferences preservando valores validos`() {
        val entity = PreferencesEntity(
            theme = "LIGHT",
            titleLanguage = "NATIVE",
            adultContent = false,
            appLanguage = "EN_US",
        )

        val domain = entity.toDomain()

        assertEquals(ThemePreference.LIGHT, domain.theme)
        assertEquals(TitleLanguagePreference.NATIVE, domain.titleLanguage)
        assertEquals(false, domain.adultContent)
        assertEquals(AppLanguagePreference.EN_US, domain.appLanguage)
    }

    @Test
    fun `deve aplicar fallbacks seguros quando a entidade contiver valores desconhecidos`() {
        val entity = PreferencesEntity(
            theme = "UNKNOWN_THEME",
            titleLanguage = "INVALID_LANG",
            adultContent = false,
            appLanguage = "UNSUPPORTED_LOCALE",
        )

        val domain = entity.toDomain()

        assertEquals(ThemePreference.SYSTEM, domain.theme)
        assertEquals(TitleLanguagePreference.ROMAJI, domain.titleLanguage)
        assertEquals(AppLanguagePreference.SYSTEM, domain.appLanguage)
    }
}
