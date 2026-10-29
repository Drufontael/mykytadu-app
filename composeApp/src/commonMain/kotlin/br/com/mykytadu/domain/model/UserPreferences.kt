package br.com.mykytadu.domain.model

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun safeValueOf(value: String?): ThemePreference =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: SYSTEM
    }
}

enum class TitleLanguagePreference {
    ROMAJI,
    ENGLISH,
    NATIVE;

    companion object {
        fun safeValueOf(value: String?): TitleLanguagePreference =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: ROMAJI
    }
}

enum class AppLanguagePreference(val code: String) {
    SYSTEM("system"),
    PT_BR("pt-BR"),
    EN_US("en-US"),
    ES_ES("es-ES");

    companion object {
        fun safeValueOf(value: String?): AppLanguagePreference =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.code.equals(value, ignoreCase = true) } ?: SYSTEM
    }
}

data class UserPreferences(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val titleLanguage: TitleLanguagePreference = TitleLanguagePreference.ROMAJI,
    val adultContent: Boolean = false,
    val appLanguage: AppLanguagePreference = AppLanguagePreference.SYSTEM,
)
