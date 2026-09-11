package br.com.mykytadu.presentation.components

import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon

internal fun Modifier.appInteractivePointer(enabled: Boolean): Modifier =
    if (enabled) pointerHoverIcon(PointerIcon.Hand) else this
