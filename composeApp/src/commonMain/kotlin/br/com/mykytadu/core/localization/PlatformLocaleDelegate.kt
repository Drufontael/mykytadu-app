package br.com.mykytadu.core.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import br.com.mykytadu.domain.model.AppLanguagePreference

expect fun applyPlatformLocale(language: AppLanguagePreference)

@Composable
fun ProvideAppLocale(
    language: AppLanguagePreference,
    content: @Composable () -> Unit,
) {
    applyPlatformLocale(language)
    key(language) {
        content()
    }
}
