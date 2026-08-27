package br.com.mykytadu.core.theme

import androidx.compose.ui.graphics.Color

public data class DarkAppColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
)

public data class LightAppColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
)

public data class SemanticAppColors(
    val watching: Color,
    val completed: Color,
    val paused: Color,
    val dropped: Color,
    val planned: Color,
)

public object AppColors {
    public val dark: DarkAppColors = DarkAppColors(
        background = Color(0xFF0B0D12),
        surface = Color(0xFF12151D),
        surfaceVariant = Color(0xFF191D27),
        primary = Color(0xFF8B7CFF),
        primaryVariant = Color(0xFF6C5CE7),
        secondary = Color(0xFF45D6C8),
        textPrimary = Color(0xFFF4F4F7),
        textSecondary = Color(0xFFA7A9B4),
        divider = Color(0xFF292D38),
    )

    public val light: LightAppColors = LightAppColors(
        background = Color(0xFFF7F7FB),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF0F0F6),
        primary = Color(0xFF6355D9),
        primaryVariant = Color(0xFF5144C4),
        secondary = Color(0xFF159E94),
        textPrimary = Color(0xFF171820),
        textSecondary = Color(0xFF656875),
        divider = Color(0xFFE2E3EA),
    )

    public val semantic: SemanticAppColors = SemanticAppColors(
        watching = Color(0xFF8B7CFF),
        completed = Color(0xFF3CCB7F),
        paused = Color(0xFFE8B84A),
        dropped = Color(0xFFE45B68),
        planned = Color(0xFF55A8FF),
    )
}
