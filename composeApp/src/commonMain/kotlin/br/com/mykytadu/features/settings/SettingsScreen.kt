package br.com.mykytadu.features.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.settings_about_description
import br.com.mykytadu.composeapp.generated.resources.settings_about_open_source
import br.com.mykytadu.composeapp.generated.resources.settings_about_section
import br.com.mykytadu.composeapp.generated.resources.settings_about_version
import br.com.mykytadu.composeapp.generated.resources.settings_adult_content_description
import br.com.mykytadu.composeapp.generated.resources.settings_adult_content_label
import br.com.mykytadu.composeapp.generated.resources.settings_appearance_section
import br.com.mykytadu.composeapp.generated.resources.settings_back
import br.com.mykytadu.composeapp.generated.resources.settings_content_section
import br.com.mykytadu.composeapp.generated.resources.settings_language_en_us
import br.com.mykytadu.composeapp.generated.resources.settings_language_pt_br
import br.com.mykytadu.composeapp.generated.resources.settings_language_section
import br.com.mykytadu.composeapp.generated.resources.settings_language_system
import br.com.mykytadu.composeapp.generated.resources.settings_theme_dark
import br.com.mykytadu.composeapp.generated.resources.settings_theme_label
import br.com.mykytadu.composeapp.generated.resources.settings_theme_light
import br.com.mykytadu.composeapp.generated.resources.settings_theme_system
import br.com.mykytadu.composeapp.generated.resources.settings_title
import br.com.mykytadu.composeapp.generated.resources.settings_title_language_english
import br.com.mykytadu.composeapp.generated.resources.settings_title_language_label
import br.com.mykytadu.composeapp.generated.resources.settings_title_language_native
import br.com.mykytadu.composeapp.generated.resources.settings_title_language_romaji
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.domain.model.AppLanguagePreference
import br.com.mykytadu.domain.model.ThemePreference
import br.com.mykytadu.domain.model.TitleLanguagePreference
import br.com.mykytadu.features.settings.model.SettingsUiState
import br.com.mykytadu.presentation.components.AppCard
import br.com.mykytadu.presentation.components.AppDivider
import br.com.mykytadu.presentation.components.AppTopBar
import br.com.mykytadu.presentation.components.appInteractivePointer
import br.com.mykytadu.presentation.components.icons.AppIcons
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsContent(
        uiState = uiState,
        onThemeSelected = viewModel::setTheme,
        onTitleLanguageSelected = viewModel::setTitleLanguage,
        onAdultContentChanged = viewModel::setAdultContent,
        onAppLanguageSelected = viewModel::setAppLanguage,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    onThemeSelected: (ThemePreference) -> Unit,
    onTitleLanguageSelected: (TitleLanguagePreference) -> Unit,
    onAdultContentChanged: (Boolean) -> Unit,
    onAppLanguageSelected: (AppLanguagePreference) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.testTag("settings_screen"),
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.settings_title),
                navigationIcon = AppIcons.Navigation.Back,
                onNavigationClick = onNavigateBack,
                navigationContentDescription = stringResource(Res.string.settings_back),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AppDimensions.padding.lg, vertical = AppDimensions.padding.md),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.lg),
        ) {
            // Seção Aparência
            SettingsSection(
                title = stringResource(Res.string.settings_appearance_section),
                testTag = "settings_theme_section",
            ) {
                Text(
                    text = stringResource(Res.string.settings_theme_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(AppDimensions.spacing.xs))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
                ) {
                    SettingsChoiceChip(
                        label = stringResource(Res.string.settings_theme_system),
                        isSelected = uiState.theme == ThemePreference.SYSTEM,
                        onClick = { onThemeSelected(ThemePreference.SYSTEM) },
                        modifier = Modifier.weight(1f).testTag("settings_theme_system"),
                    )
                    SettingsChoiceChip(
                        label = stringResource(Res.string.settings_theme_light),
                        isSelected = uiState.theme == ThemePreference.LIGHT,
                        onClick = { onThemeSelected(ThemePreference.LIGHT) },
                        modifier = Modifier.weight(1f).testTag("settings_theme_light"),
                    )
                    SettingsChoiceChip(
                        label = stringResource(Res.string.settings_theme_dark),
                        isSelected = uiState.theme == ThemePreference.DARK,
                        onClick = { onThemeSelected(ThemePreference.DARK) },
                        modifier = Modifier.weight(1f).testTag("settings_theme_dark"),
                    )
                }
            }

            AppDivider()

            // Seção Catálogo e Exibição
            SettingsSection(
                title = stringResource(Res.string.settings_content_section),
                testTag = "settings_title_language_section",
            ) {
                Text(
                    text = stringResource(Res.string.settings_title_language_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(AppDimensions.spacing.xs))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
                ) {
                    SettingsChoiceChip(
                        label = stringResource(Res.string.settings_title_language_romaji),
                        isSelected = uiState.titleLanguage == TitleLanguagePreference.ROMAJI,
                        onClick = { onTitleLanguageSelected(TitleLanguagePreference.ROMAJI) },
                        modifier = Modifier.weight(1f).testTag("settings_title_language_romaji"),
                    )
                    SettingsChoiceChip(
                        label = stringResource(Res.string.settings_title_language_english),
                        isSelected = uiState.titleLanguage == TitleLanguagePreference.ENGLISH,
                        onClick = { onTitleLanguageSelected(TitleLanguagePreference.ENGLISH) },
                        modifier = Modifier.weight(1f).testTag("settings_title_language_english"),
                    )
                    SettingsChoiceChip(
                        label = stringResource(Res.string.settings_title_language_native),
                        isSelected = uiState.titleLanguage == TitleLanguagePreference.NATIVE,
                        onClick = { onTitleLanguageSelected(TitleLanguagePreference.NATIVE) },
                        modifier = Modifier.weight(1f).testTag("settings_title_language_native"),
                    )
                }

                Spacer(modifier = Modifier.height(AppDimensions.spacing.md))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { onAdultContentChanged(!uiState.adultContent) }
                        .padding(vertical = AppDimensions.spacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = AppDimensions.padding.md),
                    ) {
                        Text(
                            text = stringResource(Res.string.settings_adult_content_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(Res.string.settings_adult_content_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = uiState.adultContent,
                        onCheckedChange = onAdultContentChanged,
                        modifier = Modifier.testTag("settings_adult_content_switch"),
                    )
                }
            }

            AppDivider()

            // Seção Idioma do Aplicativo
            SettingsSection(
                title = stringResource(Res.string.settings_language_section),
                testTag = "settings_language_section",
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
                ) {
                    SettingsChoiceChip(
                        label = stringResource(Res.string.settings_language_system),
                        isSelected = uiState.appLanguage == AppLanguagePreference.SYSTEM,
                        onClick = { onAppLanguageSelected(AppLanguagePreference.SYSTEM) },
                        modifier = Modifier.weight(1f).testTag("settings_language_system"),
                    )
                    SettingsChoiceChip(
                        label = stringResource(Res.string.settings_language_pt_br),
                        isSelected = uiState.appLanguage == AppLanguagePreference.PT_BR,
                        onClick = { onAppLanguageSelected(AppLanguagePreference.PT_BR) },
                        modifier = Modifier.weight(1f).testTag("settings_language_pt_br"),
                    )
                    SettingsChoiceChip(
                        label = stringResource(Res.string.settings_language_en_us),
                        isSelected = uiState.appLanguage == AppLanguagePreference.EN_US,
                        onClick = { onAppLanguageSelected(AppLanguagePreference.EN_US) },
                        modifier = Modifier.weight(1f).testTag("settings_language_en_us"),
                    )
                }
            }

            AppDivider()

            // Seção Sobre o MykytaDu (sem qualquer ação de autenticação ou logout)
            SettingsSection(
                title = stringResource(Res.string.settings_about_section),
                testTag = "settings_about_section",
            ) {
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_about_card"),
                ) {
                    Text(
                        text = uiState.appName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.spacing.xs))
                    Text(
                        text = stringResource(Res.string.settings_about_version, uiState.appVersion),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.spacing.sm))
                    Text(
                        text = stringResource(Res.string.settings_about_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.spacing.sm))
                    Text(
                        text = stringResource(Res.string.settings_about_open_source),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsChoiceChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .appInteractivePointer(enabled = true)
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = AppDimensions.padding.sm, vertical = AppDimensions.padding.md),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    testTag: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        content()
    }
}
