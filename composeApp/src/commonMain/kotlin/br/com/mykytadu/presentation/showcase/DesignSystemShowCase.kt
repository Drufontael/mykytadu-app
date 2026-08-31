package br.com.mykytadu.presentation.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.presentation.components.AppButton
import br.com.mykytadu.presentation.components.AppDialog
import br.com.mykytadu.presentation.components.AppIconButton
import br.com.mykytadu.presentation.components.AppSearchBar
import br.com.mykytadu.presentation.components.AppTopBar
import br.com.mykytadu.presentation.components.icons.AppIcons


@Composable
fun DesignSystemShowcase() {
    val textState = remember { mutableStateOf("") }
    val queryState = remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
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
                AppTopBar(
                    title = "Design System Showcase",
                    navigationIcon = AppIcons.Navigation.Home,
                    onNavigationClick = { /* Handle navigation click */ }
                ){
                    AppSearchBar(
                        query = queryState.value,
                        onQueryChange = { queryState.value = it }
                    )
                    AppIconButton(
                        onClick = { },
                        content = {
                            Icon(AppIcons.Actions.Close, contentDescription = "Close")
                            }
                    )

                }



                AppButton(
                    text = "Abrir Dialog",
                    onClick = {
                        showDialog = true
                    }
                )

                if (showDialog) {
                    AppDialog(
                        title = "Remover anime",
                        message = "Deseja remover este anime da sua biblioteca?",
                        confirmText = "Remover",
                        dismissText = "Cancelar",
                        icon = AppIcons.Actions.Close,
                        onConfirm = {
                            showDialog = false
                        },
                        onDismissRequest = {
                            showDialog = false
                        }
                    )
                }

            }
        }
    }
}
