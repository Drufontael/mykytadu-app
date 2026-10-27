package br.com.mykytadu.data.mapper

import br.com.mykytadu.data.local.entity.PreferencesEntity
import br.com.mykytadu.domain.model.AppLanguagePreference
import br.com.mykytadu.domain.model.ThemePreference
import br.com.mykytadu.domain.model.TitleLanguagePreference
import br.com.mykytadu.domain.model.UserPreferences

/**
 * Mapeia entidade de persistência para o modelo de domínio [UserPreferences] com fallbacks seguros.
 */
fun PreferencesEntity.toDomain(): UserPreferences = UserPreferences(
    theme = ThemePreference.safeValueOf(theme),
    titleLanguage = TitleLanguagePreference.safeValueOf(titleLanguage),
    adultContent = adultContent,
    appLanguage = AppLanguagePreference.safeValueOf(appLanguage),
)

/**
 * Mapeia modelo de domínio [UserPreferences] para entidade de persistência [PreferencesEntity].
 */
fun UserPreferences.toEntity(): PreferencesEntity = PreferencesEntity(
    theme = theme.name,
    titleLanguage = titleLanguage.name,
    adultContent = adultContent,
    appLanguage = appLanguage.name,
    schemaVersion = PreferencesEntity.CURRENT_SCHEMA_VERSION,
)
