package br.com.mykytadu.presentation.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.presentation.components.AppButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import br.com.mykytadu.domain.AnimeStatus
import br.com.mykytadu.presentation.components.AppCard
import br.com.mykytadu.presentation.components.AppChip
import br.com.mykytadu.presentation.components.AppDivider
import br.com.mykytadu.presentation.components.AppIconButton
import br.com.mykytadu.presentation.components.AppSearchBar
import br.com.mykytadu.presentation.components.AppTextField
import br.com.mykytadu.presentation.components.AppTopBar
import br.com.mykytadu.presentation.components.icons.AppIcons


@Composable
fun DesignSystemShowcase() {
    val textState = remember { mutableStateOf("") }
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppDimensions.padding.lg),
                verticalArrangement = Arrangement.spacedBy(
                    AppDimensions.spacing.md
                )
            ) {
                Text(
                    text = "Botão 1",
                    style = MaterialTheme.typography.headlineLarge
                )

                AppButton(
                    text = "Continuar",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )


                AppTextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Nome",
                    placeholder = "Digite seu nome",
                )

                AppCard (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Meu primeiro Card",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Acompanhe seus animes favoritos",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                AppIconButton(
                    onClick = {}
                ) {
                    Text("♥")
                }

                AppChip(
                    label = "Watching",
                    status = AnimeStatus.WATCHING,
                )
                AppChip(label = "Anime")

                AppDivider()

                AppTopBar(
                    title = "Top Bar",
                    navigationIcon = AppIcons.Navigation.Back,
                    onNavigationClick = {},
                    actions = {
                        AppIconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = AppIcons.Actions.Search,
                                contentDescription = "Search"
                            )
                        }
                    }
                )

                AppSearchBar(
                    query = textState.value,
                    onQueryChange = { textState.value = it }
                )


            }
        }
    }
}
