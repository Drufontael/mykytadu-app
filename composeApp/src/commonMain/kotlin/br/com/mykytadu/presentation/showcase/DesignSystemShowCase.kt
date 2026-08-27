package br.com.mykytadu.presentation.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.presentation.components.AppButton

@Composable
fun DesignSystemShowcase() {
    AppTheme {
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

            Text(
                text = "Botão 2",
                style = MaterialTheme.typography.headlineLarge
            )

            AppButton(
                text = "Desabilitado",
                onClick = {},
                enabled = false
            )

            Text(
                text = "Botão 3",
                style = MaterialTheme.typography.headlineLarge
            )

            AppButton(
                text = "Carregando",
                onClick = {},
                loading = true
            )
        }
    }
}
