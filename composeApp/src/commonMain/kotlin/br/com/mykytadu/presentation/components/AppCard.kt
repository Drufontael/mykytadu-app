package br.com.mykytadu.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.mykytadu.core.theme.AppDimensions

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        val interactionSource = remember { MutableInteractionSource() }
        val focused by interactionSource.collectIsFocusedAsState()
        Card(
            onClick = onClick,
            modifier = modifier.appInteractivePointer(enabled = true),
            interactionSource = interactionSource,
            shape = MaterialTheme.shapes.large,
            border = if (focused) BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary) else null,
            elevation = CardDefaults.cardElevation(defaultElevation = AppDimensions.elevation.low),
        ) {
            Column(modifier = Modifier.padding(AppDimensions.padding.lg), content = content)
        }
        return
    }
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(
            defaultElevation = AppDimensions.elevation.low
        ),
        content = {
            Column (
                modifier = Modifier.padding(AppDimensions.padding.lg)
            ){
                content()
            }
        }
    )
}
