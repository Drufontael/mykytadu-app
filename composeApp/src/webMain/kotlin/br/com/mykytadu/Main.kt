package br.com.mykytadu

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import br.com.mykytadu.di.initializeKoin
import br.com.mykytadu.web.diagnostics.WebNetworkDiagnosticScreen
import br.com.mykytadu.web.diagnostics.webLocationSearch
import br.com.mykytadu.web.diagnostics.isWebNetworkSmokeEnabled

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initializeKoin()

    ComposeViewport(viewportContainerId = "webApp") {
        if (isWebNetworkSmokeEnabled(webLocationSearch())) {
            WebNetworkDiagnosticScreen()
        } else {
            App()
        }
    }
}
