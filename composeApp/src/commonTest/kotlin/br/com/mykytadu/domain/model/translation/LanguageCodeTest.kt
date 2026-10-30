package br.com.mykytadu.domain.model.translation

import br.com.mykytadu.domain.model.AppLanguagePreference
import kotlin.test.Test
import kotlin.test.assertEquals

class LanguageCodeTest {

    @Test
    fun `fromCode mapeia codigos ISO comuns corretamente`() {
        assertEquals(LanguageCode.PORTUGUESE, LanguageCode.fromCode("pt"))
        assertEquals(LanguageCode.PORTUGUESE, LanguageCode.fromCode("pt-BR"))
        assertEquals(LanguageCode.ENGLISH, LanguageCode.fromCode("en"))
        assertEquals(LanguageCode.ENGLISH, LanguageCode.fromCode("en-US"))
        assertEquals(LanguageCode.SPANISH, LanguageCode.fromCode("es"))
        assertEquals(LanguageCode.SPANISH, LanguageCode.fromCode("es-ES"))
        assertEquals(LanguageCode.JAPANESE, LanguageCode.fromCode("ja"))
        assertEquals(LanguageCode.JAPANESE, LanguageCode.fromCode("ja-JP"))
    }

    @Test
    fun `fromCode faz fallback para ENGLISH para codigos desconhecidos`() {
        assertEquals(LanguageCode.ENGLISH, LanguageCode.fromCode("unknown"))
        assertEquals(LanguageCode.ENGLISH, LanguageCode.fromCode("fr"))
        assertEquals(LanguageCode.ENGLISH, LanguageCode.fromCode(""))
    }

    @Test
    fun `fromAppLanguage mapeia preferencias do usuario corretamente`() {
        assertEquals(LanguageCode.PORTUGUESE, LanguageCode.fromAppLanguage(AppLanguagePreference.PT_BR))
        assertEquals(LanguageCode.ENGLISH, LanguageCode.fromAppLanguage(AppLanguagePreference.EN_US))
        assertEquals(LanguageCode.SPANISH, LanguageCode.fromAppLanguage(AppLanguagePreference.ES_ES))
        assertEquals(LanguageCode.PORTUGUESE, LanguageCode.fromAppLanguage(AppLanguagePreference.SYSTEM))
    }
}
