package br.com.mykytadu.domain.model.translation

import br.com.mykytadu.domain.model.AppLanguagePreference

enum class LanguageCode(val code: String) {
    PORTUGUESE("pt"),
    ENGLISH("en"),
    SPANISH("es"),
    JAPANESE("ja");

    companion object {
        fun fromCode(code: String): LanguageCode =
            entries.firstOrNull { it.code.equals(code.trim(), ignoreCase = true) }
                ?: when {
                    code.startsWith("pt", ignoreCase = true) -> PORTUGUESE
                    code.startsWith("es", ignoreCase = true) -> SPANISH
                    code.startsWith("ja", ignoreCase = true) -> JAPANESE
                    else -> ENGLISH
                }

        fun fromAppLanguage(preference: AppLanguagePreference): LanguageCode = when (preference) {
            AppLanguagePreference.SYSTEM -> PORTUGUESE
            AppLanguagePreference.PT_BR -> PORTUGUESE
            AppLanguagePreference.EN_US -> ENGLISH
            AppLanguagePreference.ES_ES -> SPANISH
        }
    }
}
