package br.com.mykytadu.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.mykytadu.core.theme.AppDimensions

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
){
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val baseColor = MaterialTheme.colorScheme.primary
    val focusedColor = if (isFocused && enabled && !loading) {
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f).compositeOver(baseColor)
    } else {
        baseColor
    }
    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .appInteractivePointer(enabled && !loading),
        enabled = enabled && !loading,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            containerColor = focusedColor,
            )
        )
    {
        if (loading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(AppDimensions.icon.md)
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

