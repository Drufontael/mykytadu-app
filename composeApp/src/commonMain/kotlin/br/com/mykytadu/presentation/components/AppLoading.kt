package br.com.mykytadu.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun AppLoading(
    modifier: Modifier = Modifier,
    contentDescription: String = "Carregando",
) {
    require(contentDescription.isNotBlank()) { "contentDescription must not be blank" }
    CircularProgressIndicator(
        modifier = modifier.semantics { this.contentDescription = contentDescription },
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}
