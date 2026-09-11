package br.com.mykytadu.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.presentation.components.AppButton

@Composable
fun NavigationPlaceholder(
    text: String,
    onClick: (() -> Unit)? = null,
    actionText: String? = null,
) {
    require(
        (onClick == null && actionText == null) ||
            (onClick != null && !actionText.isNullOrBlank())
    ) { "An action requires a non-blank actionText and callback" }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                AppDimensions.spacing.md
            )
        ) {
            Text(text = text)
            if (onClick != null && !actionText.isNullOrBlank()) {
                AppButton(text = actionText, onClick = onClick)
            }
        }
    }
}
