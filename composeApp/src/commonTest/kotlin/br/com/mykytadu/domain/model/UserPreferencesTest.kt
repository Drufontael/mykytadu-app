package br.com.mykytadu.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class UserPreferencesTest {

    @Test
    fun `valores padrao de UserPreferences sao consistentes e seguros`() {
        val prefs = UserPreferences()
        assertEquals(ThemePreference.SYSTEM, prefs.theme)
        assertEquals(TitleLanguagePreference.ROMAJI, prefs.titleLanguage)
        assertFalse(prefs.adultContent)
        assertEquals(AppLanguagePreference.SYSTEM, prefs.appLanguage)
    }

    @Test
    fun `safeValueOf em ThemePreference resolve corretamente e faz fallback seguro`() {
        assertEquals(ThemePreference.LIGHT, ThemePreference.safeValueOf("LIGHT"))
        assertEquals(ThemePreference.LIGHT, ThemePreference.safeValueOf("light"))
        assertEquals(ThemePreference.DARK, ThemePreference.safeValueOf("DARK"))
        assertEquals(ThemePreference.DARK, ThemePreference.safeValueOf("dark"))
        assertEquals(ThemePreference.SYSTEM, ThemePreference.safeValueOf("SYSTEM"))
        assertEquals(ThemePreference.SYSTEM, ThemePreference.safeValueOf("invalid"))
        assertEquals(ThemePreference.SYSTEM, ThemePreference.safeValueOf(null))
    }

    @Test
    fun `safeValueOf em TitleLanguagePreference resolve corretamente e faz fallback seguro`() {
        assertEquals(TitleLanguagePreference.ROMAJI, TitleLanguagePreference.safeValueOf("ROMAJI"))
        assertEquals(TitleLanguagePreference.ROMAJI, TitleLanguagePreference.safeValueOf("romaji"))
        assertEquals(TitleLanguagePreference.ENGLISH, TitleLanguagePreference.safeValueOf("ENGLISH"))
        assertEquals(TitleLanguagePreference.ENGLISH, TitleLanguagePreference.safeValueOf("english"))
        assertEquals(TitleLanguagePreference.NATIVE, TitleLanguagePreference.safeValueOf("NATIVE"))
        assertEquals(TitleLanguagePreference.NATIVE, TitleLanguagePreference.safeValueOf("native"))
        assertEquals(TitleLanguagePreference.ROMAJI, TitleLanguagePreference.safeValueOf("invalid"))
        assertEquals(TitleLanguagePreference.ROMAJI, TitleLanguagePreference.safeValueOf(null))
    }

    @Test
    fun `safeValueOf em AppLanguagePreference resolve por nome ou codigo e faz fallback seguro`() {
        assertEquals(AppLanguagePreference.PT_BR, AppLanguagePreference.safeValueOf("PT_BR"))
        assertEquals(AppLanguagePreference.PT_BR, AppLanguagePreference.safeValueOf("pt-BR"))
        assertEquals(AppLanguagePreference.EN_US, AppLanguagePreference.safeValueOf("EN_US"))
        assertEquals(AppLanguagePreference.EN_US, AppLanguagePreference.safeValueOf("en-US"))
        assertEquals(AppLanguagePreference.SYSTEM, AppLanguagePreference.safeValueOf("SYSTEM"))
        assertEquals(AppLanguagePreference.SYSTEM, AppLanguagePreference.safeValueOf("system"))
        assertEquals(AppLanguagePreference.SYSTEM, AppLanguagePreference.safeValueOf("es-ES"))
        assertEquals(AppLanguagePreference.SYSTEM, AppLanguagePreference.safeValueOf(null))
    }

    @Test
    fun `resolveDisplayTitle prioriza romaji quando TitleLanguagePreference eh ROMAJI`() {
        val titles = AnimeTitles(
            romaji = "Shingeki no Kyojin",
            english = "Attack on Titan",
            native = "進撃の巨人",
            synonyms = listOf("AoT"),
        )
        assertEquals("Shingeki no Kyojin", titles.resolveDisplayTitle(TitleLanguagePreference.ROMAJI, "Fallback"))
    }

    @Test
    fun `resolveDisplayTitle prioriza ingles quando TitleLanguagePreference eh ENGLISH`() {
        val titles = AnimeTitles(
            romaji = "Shingeki no Kyojin",
            english = "Attack on Titan",
            native = "進撃の巨人",
            synonyms = listOf("AoT"),
        )
        assertEquals("Attack on Titan", titles.resolveDisplayTitle(TitleLanguagePreference.ENGLISH, "Fallback"))
    }

    @Test
    fun `resolveDisplayTitle prioriza nativo quando TitleLanguagePreference eh NATIVE`() {
        val titles = AnimeTitles(
            romaji = "Shingeki no Kyojin",
            english = "Attack on Titan",
            native = "進撃の巨人",
            synonyms = listOf("AoT"),
        )
        assertEquals("進撃の巨人", titles.resolveDisplayTitle(TitleLanguagePreference.NATIVE, "Fallback"))
    }

    @Test
    fun `resolveDisplayTitle faz fallback ordenado quando o titulo preferido esta ausente ou em branco`() {
        val semRomaji = AnimeTitles(
            romaji = "   ",
            english = "Attack on Titan",
            native = "進撃の巨人",
        )
        assertEquals("Attack on Titan", semRomaji.resolveDisplayTitle(TitleLanguagePreference.ROMAJI, "Fallback"))

        val apenasNativo = AnimeTitles(
            romaji = null,
            english = "",
            native = "進撃の巨人",
        )
        assertEquals("進撃の巨人", apenasNativo.resolveDisplayTitle(TitleLanguagePreference.ENGLISH, "Fallback"))
    }

    @Test
    fun `resolveDisplayTitle usa primeiro sinonimo utilizavel quando todos os titulos principais estao ausentes`() {
        val apenasSinonimo = AnimeTitles(
            romaji = null,
            english = " ",
            native = "",
            synonyms = listOf(" ", " AoT ", "Attack"),
        )
        assertEquals("AoT", apenasSinonimo.resolveDisplayTitle(TitleLanguagePreference.ROMAJI, "Fallback"))
    }

    @Test
    fun `resolveDisplayTitle retorna fallback quando todos os titulos e sinonimos sao vazios`() {
        val vazio = AnimeTitles(
            romaji = " ",
            english = "",
            native = null,
            synonyms = listOf("", "   "),
        )
        assertEquals("Título indisponível", vazio.resolveDisplayTitle(TitleLanguagePreference.ROMAJI, "Título indisponível"))
    }
}
