package br.com.mykytadu.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

private const val DEFAULT_LOADING_CONTENT_DESCRIPTION = "Carregando"

internal fun loadingContentDescription(description: String): String =
    description.takeIf(String::isNotBlank) ?: DEFAULT_LOADING_CONTENT_DESCRIPTION

@Composable
fun AppLoading(
    modifier: Modifier = Modifier,
    contentDescription: String = DEFAULT_LOADING_CONTENT_DESCRIPTION,
) {
    val resolvedContentDescription = loadingContentDescription(contentDescription)
    CircularProgressIndicator(
        modifier = modifier.semantics { this.contentDescription = resolvedContentDescription },
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}
