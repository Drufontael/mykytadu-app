@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package br.com.mykytadu.web.diagnostics

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLImageElement

internal actual fun webLocationSearch(): String = window.location.search

internal actual fun probeWebImage(
    url: String?,
    onStateChanged: (WebImageProbeState) -> Unit,
): (() -> Unit)? {
    if (url.isNullOrBlank()) {
        onStateChanged(WebImageProbeState.Inactive)
        return null
    }

    onStateChanged(WebImageProbeState.Loading)
    val image = document.createElement("img") as HTMLImageElement
    image.onload = {
        onStateChanged(WebImageProbeState.Loaded(image.naturalWidth, image.naturalHeight))
    }
    image.onerror = { _, _, _, _, _ ->
        onStateChanged(WebImageProbeState.Unavailable)
        null
    }
    image.src = url

    return {
        image.onload = null
        image.onerror = null
    }
}
