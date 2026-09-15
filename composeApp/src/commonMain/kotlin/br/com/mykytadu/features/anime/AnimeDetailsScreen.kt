package br.com.mykytadu.features.anime

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.mykytadu.presentation.components.AppLoading
import br.com.mykytadu.presentation.NavigationPlaceholder
import br.com.mykytadu.domain.model.AniListAnimeId
import br.com.mykytadu.composeapp.generated.resources.Res
import br.com.mykytadu.composeapp.generated.resources.anime_details_placeholder
import br.com.mykytadu.composeapp.generated.resources.anime_details_back
import org.jetbrains.compose.resources.stringResource

@Composable
fun AnimeDetailsScreen(id: AniListAnimeId, onBack: () -> Unit) {
    val text = stringResource(Res.string.anime_details_placeholder, id.value)
    val backText = stringResource(Res.string.anime_details_back)
    // Web resources load asynchronously; do not create an action without its label.
    if (text.isBlank() || backText.isBlank()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AppLoading()
        }
        return
    }
    NavigationPlaceholder(
        text = text,
        onClick = onBack,
        actionText = backText,
    )
}
