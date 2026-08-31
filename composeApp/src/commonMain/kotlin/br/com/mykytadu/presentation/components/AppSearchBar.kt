package br.com.mykytadu.presentation.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import br.com.mykytadu.core.theme.AppShapes
import br.com.mykytadu.presentation.components.icons.AppIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    leadingIcon: ImageVector? = AppIcons.Actions.Search,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    SearchBar(
        inputField = {
            AppTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                onTrailingIconClick = onTrailingIconClick
        )},
        expanded = expanded,
        modifier = modifier,
        shape = RoundedCornerShape(AppShapes.radius.input),
        onExpandedChange = onExpandedChange,
        content = content
    )
}