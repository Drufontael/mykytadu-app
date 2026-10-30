package br.com.mykytadu.core.localization

import br.com.mykytadu.domain.model.AppLanguagePreference
import platform.Foundation.NSUserDefaults

actual fun applyPlatformLocale(language: AppLanguagePreference) {
    val languageCode = when (language) {
        AppLanguagePreference.SYSTEM -> null
        AppLanguagePreference.PT_BR -> "pt-BR"
        AppLanguagePreference.EN_US -> "en"
        AppLanguagePreference.ES_ES -> "es"
    }
    if (languageCode != null) {
        NSUserDefaults.standardUserDefaults.setObject(listOf(languageCode), forKey = "AppleLanguages")
        NSUserDefaults.standardUserDefaults.synchronize()
    }
}
