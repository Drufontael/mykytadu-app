package br.com.mykytadu.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.mykytadu.core.theme.AppShapes
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.MaterialTheme

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false

) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        isError = isError,
        shape = RoundedCornerShape(AppShapes.radius.input),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary),
        label = label?.let { { androidx.compose.material3.Text(it) } },
        placeholder = placeholder?.let { { androidx.compose.material3.Text(it) } },
    )
}

