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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.settings_about_attribution_button
import br.com.mykytadu.composeapp.generated.resources.settings_about_attribution_content
import br.com.mykytadu.composeapp.generated.resources.settings_about_attribution_title
import br.com.mykytadu.composeapp.generated.resources.settings_about_description
import br.com.mykytadu.composeapp.generated.resources.settings_about_dialog_close
import br.com.mykytadu.composeapp.generated.resources.settings_about_github_button
import br.com.mykytadu.composeapp.generated.resources.settings_about_licenses_button
import br.com.mykytadu.composeapp.generated.resources.settings_about_licenses_content
import br.com.mykytadu.composeapp.generated.resources.settings_about_licenses_title
import br.com.mykytadu.composeapp.generated.resources.settings_about_open_source
import br.com.mykytadu.composeapp.generated.resources.settings_about_privacy_button
import br.com.mykytadu.composeapp.generated.resources.settings_about_privacy_content
import br.com.mykytadu.composeapp.generated.resources.settings_about_privacy_title
import br.com.mykytadu.composeapp.generated.resources.settings_about_section
import br.com.mykytadu.composeapp.generated.resources.settings_about_version
import br.com.mykytadu.composeapp.generated.resources.settings_adult_content_description
import br.com.mykytadu.composeapp.generated.resources.settings_adult_content_label
import br.com.mykytadu.composeapp.generated.resources.settings_appearance_section
import br.com.mykytadu.composeapp.generated.resources.settings_back
import br.com.mykytadu.composeapp.generated.resources.settings_content_section
import br.com.mykytadu.composeapp.generated.resources.settings_language_en_us
import br.com.mykytadu.composeapp.generated.resources.settings_language_es_es
import br.com.mykytadu.composeapp.generated.resources.settings_language_pt_br
import br.com.mykytadu.composeapp.generated.resources.settings_language_section
import br.com.mykytadu.composeapp.generated.resources.settings_language_system
import br.com.mykytadu.composeapp.generated.resources.settings_storage_cache_cleared
import br.com.mykytadu.composeapp.generated.resources.settings_storage_cache_description
import br.com.mykytadu.composeapp.generated.resources.settings_storage_cache_title
import br.com.mykytadu.composeapp.generated.resources.settings_storage_clear_cache_button
import br.com.mykytadu.composeapp.generated.resources.settings_storage_section
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
import br.com.mykytadu.presentation.components.AppButton
import br.com.mykytadu.presentation.components.AppCard
import br.com.mykytadu.presentation.components.AppDivider
import br.com.mykytadu.presentation.components.AppInfoDialog
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
        onClearCache = viewModel::clearCatalogCache,
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
    onClearCache: () -> Unit = {},
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }
    var showAttributionDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

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

            // Seção Preferências de Conteúdo
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

                Spacer(modifier = Modifier.height(AppDimensions.spacing.sm))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .appInteractivePointer(enabled = true)
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
                Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm)) {
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
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
                    ) {
                        SettingsChoiceChip(
                            label = stringResource(Res.string.settings_language_en_us),
                            isSelected = uiState.appLanguage == AppLanguagePreference.EN_US,
                            onClick = { onAppLanguageSelected(AppLanguagePreference.EN_US) },
                            modifier = Modifier.weight(1f).testTag("settings_language_en_us"),
                        )
                        SettingsChoiceChip(
                            label = stringResource(Res.string.settings_language_es_es),
                            isSelected = uiState.appLanguage == AppLanguagePreference.ES_ES,
                            onClick = { onAppLanguageSelected(AppLanguagePreference.ES_ES) },
                            modifier = Modifier.weight(1f).testTag("settings_language_es_es"),
                        )
                    }
                }
            }

            AppDivider()

            // Seção Armazenamento e Cache
            SettingsSection(
                title = stringResource(Res.string.settings_storage_section),
                testTag = "settings_storage_section",
            ) {
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_cache_card"),
                ) {
                    Text(
                        text = stringResource(Res.string.settings_storage_cache_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.spacing.xs))
                    Text(
                        text = stringResource(Res.string.settings_storage_cache_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.spacing.md))
                    AppButton(
                        text = stringResource(Res.string.settings_storage_clear_cache_button),
                        onClick = onClearCache,
                        enabled = !uiState.isClearingCache,
                        loading = uiState.isClearingCache,
                        modifier = Modifier.testTag("settings_clear_cache_button"),
                    )
                    if (uiState.cacheClearedMessage) {
                        Spacer(modifier = Modifier.height(AppDimensions.spacing.sm))
                        Text(
                            text = stringResource(Res.string.settings_storage_cache_cleared),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("settings_cache_cleared_message"),
                        )
                    }
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
                    Spacer(modifier = Modifier.height(AppDimensions.spacing.md))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.sm),
                    ) {
                        AppButton(
                            text = stringResource(Res.string.settings_about_privacy_button),
                            onClick = { showPrivacyDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_privacy_button"),
                        )
                        AppButton(
                            text = stringResource(Res.string.settings_about_licenses_button),
                            onClick = { showLicensesDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_licenses_button"),
                        )
                        AppButton(
                            text = stringResource(Res.string.settings_about_attribution_button),
                            onClick = { showAttributionDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_attribution_button"),
                        )
                        AppButton(
                            text = stringResource(Res.string.settings_about_github_button),
                            onClick = { uriHandler.openUri("https://github.com/Drufontael/mykytadu-app") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_github_button"),
                        )
                    }
                }
            }
        }
    }

    if (showPrivacyDialog) {
        AppInfoDialog(
            title = stringResource(Res.string.settings_about_privacy_title),
            message = stringResource(Res.string.settings_about_privacy_content),
            onDismissRequest = { showPrivacyDialog = false },
            closeText = stringResource(Res.string.settings_about_dialog_close),
            modifier = Modifier.testTag("settings_privacy_dialog"),
        )
    }

    if (showLicensesDialog) {
        AppInfoDialog(
            title = stringResource(Res.string.settings_about_licenses_title),
            message = stringResource(Res.string.settings_about_licenses_content),
            onDismissRequest = { showLicensesDialog = false },
            closeText = stringResource(Res.string.settings_about_dialog_close),
            modifier = Modifier.testTag("settings_licenses_dialog"),
        )
    }

    if (showAttributionDialog) {
        AppInfoDialog(
            title = stringResource(Res.string.settings_about_attribution_title),
            message = stringResource(Res.string.settings_about_attribution_content),
            onDismissRequest = { showAttributionDialog = false },
            closeText = stringResource(Res.string.settings_about_dialog_close),
            modifier = Modifier.testTag("settings_attribution_dialog"),
        )
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
            .semantics {
                selected = isSelected
            }
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
