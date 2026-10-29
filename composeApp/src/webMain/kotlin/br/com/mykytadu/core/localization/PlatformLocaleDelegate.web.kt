package br.com.mykytadu.core.localization

import br.com.mykytadu.domain.model.AppLanguagePreference

actual fun applyPlatformLocale(language: AppLanguagePreference) {
    // No ambiente Web, fallback gracioso mantendo o comportamento de compatibilidade
}
