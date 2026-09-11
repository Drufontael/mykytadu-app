package br.com.mykytadu.presentation.components

import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun AppIconButton(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    require(contentDescription.isNotBlank()) { "contentDescription must not be blank" }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val containerColor = if (isFocused && enabled) {
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.22f)
            .compositeOver(MaterialTheme.colorScheme.background)
    } else {
        MaterialTheme.colorScheme.background
    }
    IconButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .appInteractivePointer(enabled)
            .semantics { this.contentDescription = contentDescription },
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = containerColor,
        ),
        content = content
    )
}
