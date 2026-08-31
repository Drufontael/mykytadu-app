package br.com.mykytadu.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.dark.primary,
    onPrimary = AppColors.dark.textPrimary,

    secondary = AppColors.dark.secondary,
    onSecondary = AppColors.dark.textPrimary,

    background = AppColors.dark.background,
    onBackground = AppColors.dark.textPrimary,

    surface = AppColors.dark.surface,
    onSurface = AppColors.dark.textPrimary,

    surfaceVariant = AppColors.dark.surfaceVariant,
    onSurfaceVariant = AppColors.dark.textSecondary,

    outline = AppColors.dark.divider
)

private val LightColorScheme = lightColorScheme(
    primary = AppColors.light.primary,
    onPrimary = AppColors.light.textPrimary,

    secondary = AppColors.light.secondary,
    onSecondary = AppColors.light.textPrimary,

    background = AppColors.light.background,
    onBackground = AppColors.light.textPrimary,

    surface = AppColors.light.surface,
    onSurface = AppColors.light.textPrimary,

    surfaceVariant = AppColors.light.surfaceVariant,
    onSurfaceVariant = AppColors.light.textSecondary,

    outline = AppColors.light.divider
)

private val AppShapeScheme = Shapes(
    small = RoundedCornerShape(AppShapes.radius.chip),
    medium = RoundedCornerShape(AppShapes.radius.button),
    large = RoundedCornerShape(AppShapes.radius.card)
)

private fun AppTypographyTokens.toMaterialTypography(): Typography {
    return Typography(
        displayLarge = display,
        headlineLarge = headline,
        titleLarge = title,
        bodyLarge = body,
        bodyMedium = bodySmall,
        bodySmall = caption
    )
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    //darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val typography = appTypography().toMaterialTypography()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = AppShapeScheme,
        content = content
    )
}

