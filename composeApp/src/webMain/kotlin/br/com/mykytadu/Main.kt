package br.com.mykytadu

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import br.com.mykytadu.di.initializeKoin
import br.com.mykytadu.web.diagnostics.WebNetworkDiagnosticScreen
import br.com.mykytadu.web.diagnostics.webLocationSearch
import br.com.mykytadu.web.diagnostics.isWebNetworkSmokeEnabled
import br.com.mykytadu.web.navigation.WebNavigationHistoryController
import br.com.mykytadu.web.navigation.createWebBrowserHistoryPort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initializeKoin()

    ComposeViewport(viewportContainerId = "webApp") {
        if (isWebNetworkSmokeEnabled(webLocationSearch())) {
            WebNetworkDiagnosticScreen()
        } else {
            WebApp()
        }
    }
}

@Composable
private fun WebApp() {
    val navigationHistory = remember {
        WebNavigationHistoryController(createWebBrowserHistoryPort())
    }

    App(navigationHistory)
}
