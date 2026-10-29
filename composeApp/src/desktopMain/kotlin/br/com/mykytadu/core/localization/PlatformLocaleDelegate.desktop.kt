package br.com.mykytadu.core.localization

import br.com.mykytadu.domain.model.AppLanguagePreference
import java.util.Locale

private val defaultSystemLocale: Locale = Locale.getDefault()

actual fun applyPlatformLocale(language: AppLanguagePreference) {
    val targetLocale = when (language) {
        AppLanguagePreference.SYSTEM -> defaultSystemLocale
        AppLanguagePreference.PT_BR -> Locale.forLanguageTag("pt-BR")
        AppLanguagePreference.EN_US -> Locale.US
        AppLanguagePreference.ES_ES -> Locale.forLanguageTag("es-ES")
    }
    Locale.setDefault(targetLocale)
}
