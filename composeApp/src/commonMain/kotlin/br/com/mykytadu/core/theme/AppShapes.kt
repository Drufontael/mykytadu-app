package br.com.mykytadu.core.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

public data class ShapeTokens(
    val card: Dp,
    val button: Dp,
    val input: Dp,
    val chip: Dp,
    val dialog: Dp,
    val bottomSheet: Dp,
    val avatar: Dp,
)

public object AppShapes {
    public val radius: ShapeTokens = ShapeTokens(
        card = 16.dp,
        button = 12.dp,
        input = 12.dp,
        chip = 8.dp,
        dialog = 20.dp,
        bottomSheet = 24.dp,
        avatar = 999.dp,
    )
}
