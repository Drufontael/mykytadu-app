package br.com.mykytadu.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import br.com.mykytadu.core.theme.AppShapes

@Composable
fun AppDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = null,
    icon: ImageVector? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        icon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            AppButton(
                text = confirmText,
                onClick = onConfirm
            )
        },
        dismissButton = dismissText?.let {
            {
                TextButton(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(AppShapes.radius.button)
                ) {
                    Text(text = it)
                }
            }
        },
        shape = RoundedCornerShape(AppShapes.radius.dialog),
        containerColor = MaterialTheme.colorScheme.surface,
        iconContentColor = MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}