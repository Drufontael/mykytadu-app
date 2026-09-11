package br.com.mykytadu.core.layout

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class ResponsiveLayoutTest {
    @Test
    fun widthsBelowBreakpointAreCompact() {
        assertEquals(ResponsiveLayout.COMPACT, responsiveLayoutFor(599.dp))
    }

    @Test
    fun breakpointIsExpanded() {
        assertEquals(ResponsiveLayout.EXPANDED, responsiveLayoutFor(600.dp))
    }

    @Test
    fun widthsAboveBreakpointAreExpanded() {
        assertEquals(ResponsiveLayout.EXPANDED, responsiveLayoutFor(1440.dp))
    }

    @Test
    fun contentMaxWidthIsCentralized() {
        assertEquals(1200.dp, ResponsiveLayoutTokens.contentMaxWidth)
    }
}
