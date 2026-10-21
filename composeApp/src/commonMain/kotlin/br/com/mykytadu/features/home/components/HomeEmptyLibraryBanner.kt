package br.com.mykytadu.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.home_empty_library_action
import br.com.mykytadu.composeapp.generated.resources.home_empty_library_message
import br.com.mykytadu.composeapp.generated.resources.home_empty_library_title
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.presentation.components.AppButton
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeEmptyLibraryBanner(
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimensions.padding.lg, vertical = AppDimensions.padding.md)
            .clip(RoundedCornerShape(AppDimensions.radius.lg))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(AppDimensions.radius.lg),
            )
            .padding(AppDimensions.padding.xl)
            .testTag("home_empty_library_banner"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.home_empty_library_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            text = stringResource(Res.string.home_empty_library_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        AppButton(
            text = stringResource(Res.string.home_empty_library_action),
            onClick = onNavigateToSearch,
            modifier = Modifier.testTag("home_empty_library_button"),
        )
    }
}
