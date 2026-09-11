package br.com.mykytadu.core.layout

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

public enum class ResponsiveLayout {
    COMPACT,
    EXPANDED,
}

public object ResponsiveLayoutTokens {
    public val compactBreakpoint: Dp = 600.dp
    public val contentMaxWidth: Dp = 1200.dp
}

public fun responsiveLayoutFor(width: Dp): ResponsiveLayout =
    if (width < ResponsiveLayoutTokens.compactBreakpoint) {
        ResponsiveLayout.COMPACT
    } else {
        ResponsiveLayout.EXPANDED
    }
