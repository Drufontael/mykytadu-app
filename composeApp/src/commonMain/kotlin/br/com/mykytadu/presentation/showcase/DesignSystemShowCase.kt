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
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import br.com.mykytadu.presentation.components.AppCard
import br.com.mykytadu.presentation.components.AppTextField

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
                    label = "Nome",
                    placeholder = "Digite seu nome",
                    modifier = Modifier.fillMaxWidth()
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


            }
        }
    }
}
