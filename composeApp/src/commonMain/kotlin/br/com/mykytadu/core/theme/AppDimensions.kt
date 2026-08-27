package br.com.mykytadu.core.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

public data class SpacingTokens(
    val none: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val xxl: Dp,
    val xxxl: Dp,
    val huge: Dp,
    val section: Dp,
    val screen: Dp,
)

public data class PaddingTokens(
    val none: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val xxl: Dp,
    val xxxl: Dp,
)

public data class RadiusTokens(
    val none: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val xxl: Dp,
    val full: Dp,
)

public data class IconTokens(
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
)

public object AppDimensions {
    public val spacing: SpacingTokens = SpacingTokens(
        none = 0.dp,
        xs = 4.dp,
        sm = 8.dp,
        md = 12.dp,
        lg = 16.dp,
        xl = 20.dp,
        xxl = 24.dp,
        xxxl = 32.dp,
        huge = 40.dp,
        section = 48.dp,
        screen = 64.dp,
    )

    public val padding: PaddingTokens = PaddingTokens(
        none = 0.dp,
        xs = 4.dp,
        sm = 8.dp,
        md = 12.dp,
        lg = 16.dp,
        xl = 20.dp,
        xxl = 24.dp,
        xxxl = 32.dp,
    )

    public val radius: RadiusTokens = RadiusTokens(
        none = 0.dp,
        xs = 4.dp,
        sm = 8.dp,
        md = 12.dp,
        lg = 16.dp,
        xl = 20.dp,
        xxl = 24.dp,
        full = 999.dp,
    )

    public val icon: IconTokens = IconTokens(
        sm = 16.dp,
        md = 20.dp,
        lg = 24.dp,
        xl = 32.dp,
    )
}
