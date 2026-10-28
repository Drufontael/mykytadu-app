package br.com.mykytadu.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mykytadu.domain.model.AppLanguagePreference
import br.com.mykytadu.domain.model.ThemePreference
import br.com.mykytadu.domain.model.TitleLanguagePreference
import br.com.mykytadu.domain.repository.PreferencesRepository
import br.com.mykytadu.features.settings.model.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.observePreferences().collect { prefs ->
                _uiState.value = SettingsUiState(
                    theme = prefs.theme,
                    titleLanguage = prefs.titleLanguage,
                    adultContent = prefs.adultContent,
                    appLanguage = prefs.appLanguage,
                )
            }
        }
    }

    fun setTheme(theme: ThemePreference) {
        viewModelScope.launch {
            preferencesRepository.updateTheme(theme)
        }
    }

    fun setTitleLanguage(language: TitleLanguagePreference) {
        viewModelScope.launch {
            preferencesRepository.updateTitleLanguage(language)
        }
    }

    fun setAdultContent(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateAdultContent(enabled)
        }
    }

    fun setAppLanguage(language: AppLanguagePreference) {
        viewModelScope.launch {
            preferencesRepository.updateAppLanguage(language)
        }
    }
}
