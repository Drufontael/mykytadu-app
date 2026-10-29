package br.com.mykytadu.features.settings.model

import br.com.mykytadu.domain.model.AppLanguagePreference
import br.com.mykytadu.domain.model.ThemePreference
import br.com.mykytadu.domain.model.TitleLanguagePreference

data class SettingsUiState(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val titleLanguage: TitleLanguagePreference = TitleLanguagePreference.ROMAJI,
    val adultContent: Boolean = false,
    val appLanguage: AppLanguagePreference = AppLanguagePreference.SYSTEM,
    val appName: String = "MykytaDu",
    val appVersion: String = "1.0.0",
    val isClearingCache: Boolean = false,
    val cacheClearedMessage: Boolean = false,
)
