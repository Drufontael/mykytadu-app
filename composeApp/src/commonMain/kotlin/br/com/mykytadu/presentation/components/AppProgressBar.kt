package br.com.mykytadu.presentation.components

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics

@Composable
fun AppProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val boundedProgress = progress.coerceIn(0f, 1f)
    LinearProgressIndicator(
        progress = { boundedProgress },
        modifier = modifier.semantics {
            progressBarRangeInfo = ProgressBarRangeInfo(boundedProgress, 0f..1f)
        },
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}
