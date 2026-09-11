package br.com.mykytadu.presentation.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.domain.AnimeStatus
import br.com.mykytadu.presentation.components.AppButton
import br.com.mykytadu.presentation.components.AppChip
import br.com.mykytadu.presentation.components.AppDialog
import br.com.mykytadu.presentation.components.AppEmptyState
import br.com.mykytadu.presentation.components.AppError
import br.com.mykytadu.presentation.components.AppIconButton
import br.com.mykytadu.presentation.components.AppLoading
import br.com.mykytadu.presentation.components.AppProgressBar
import br.com.mykytadu.presentation.components.AppSearchBar
import br.com.mykytadu.presentation.components.AppTextField
import br.com.mykytadu.presentation.components.AppTopBar
import br.com.mykytadu.presentation.components.icons.AppIcons

@Composable
fun DesignSystemShowcase() {
    var query by remember { mutableStateOf("") }
    var searchExpanded by remember { mutableStateOf(false) }
    var fieldValue by remember { mutableStateOf("Texto de demonstração") }
    var showDialog by remember { mutableStateOf(false) }
    var loadingDemo by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppDimensions.padding.lg)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
            ) {
                AppTopBar(
                    title = "Design System Showcase",
                    navigationIcon = AppIcons.Navigation.Home,
                    onNavigationClick = { statusMessage = "Ação de navegação demonstrada" },
                    navigationContentDescription = "Ir para início",
                ) {
                    AppSearchBar(
                        query = query,
                        onQueryChange = { query = it },
                        expanded = searchExpanded,
                        onExpandedChange = { searchExpanded = it },
                    )
                    AppIconButton(
                        onClick = { statusMessage = "Ação do ícone demonstrada" },
                        contentDescription = "Demonstrar ação do ícone",
                    ) {
                        Icon(AppIcons.Actions.Close, contentDescription = null)
                    }
                }

                Text("Botões", style = MaterialTheme.typography.titleMedium)
                AppButton(text = "Botão habilitado", onClick = { statusMessage = "Botão habilitado acionado" })
                AppButton(
                    text = "Botão desabilitado",
                    onClick = { statusMessage = "Esta ação não está disponível" },
                    enabled = false,
                )
                AppButton(
                    text = "Botão em carregamento",
                    onClick = { loadingDemo = true },
                    loading = loadingDemo,
                )

                Text("Campo e chip informativo", style = MaterialTheme.typography.titleMedium)
                AppTextField(
                    value = fieldValue,
                    onValueChange = { fieldValue = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Campo de demonstração",
                    trailingIcon = AppIcons.Actions.Close,
                    onTrailingIconClick = { fieldValue = "" },
                    trailingIconContentDescription = "Limpar campo de demonstração",
                )
                AppChip(label = "Informativo", status = AnimeStatus.COMPLETED)

                Text("Dialog", style = MaterialTheme.typography.titleMedium)
                AppButton(text = "Abrir dialog", onClick = { showDialog = true })
                if (showDialog) {
                    AppDialog(
                        title = "Dialog de demonstração",
                        message = "Use confirmar, cancelar ou Escape para fechar.",
                        confirmText = "Confirmar",
                        dismissText = "Cancelar",
                        onConfirm = {
                            showDialog = false
                            statusMessage = "Dialog confirmado"
                        },
                        onDismissRequest = {
                            showDialog = false
                            statusMessage = "Dialog cancelado"
                        },
                    )
                }

                Text("Estados", style = MaterialTheme.typography.titleMedium)
                AppLoading(contentDescription = "Carregando demonstração")
                AppProgressBar(progress = 0.75f, modifier = Modifier.fillMaxWidth())
                AppError(
                    title = "Erro de demonstração",
                    message = "Este estado representa uma falha sem realizar operação externa.",
                    retryText = "Repetir demonstração",
                    onRetry = { statusMessage = "Repetição local demonstrada" },
                )
                AppEmptyState(
                    title = "Estado vazio de demonstração",
                    message = "Nenhum item local foi fornecido.",
                    actionText = "Demonstrar ação",
                    onAction = { statusMessage = "Ação do estado vazio demonstrada" },
                )

                statusMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            }
        }
    }
}
