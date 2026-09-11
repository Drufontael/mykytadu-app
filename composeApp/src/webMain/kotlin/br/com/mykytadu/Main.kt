package br.com.mykytadu

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import br.com.mykytadu.di.initializeKoin
import br.com.mykytadu.presentation.showcase.DesignSystemShowcase
import br.com.mykytadu.web.WebEntryMode
import br.com.mykytadu.web.resolveWebEntryMode
import br.com.mykytadu.web.diagnostics.WebNetworkDiagnosticScreen
import br.com.mykytadu.web.diagnostics.webLocationSearch
import br.com.mykytadu.web.navigation.WebNavigationHistoryController
import br.com.mykytadu.web.navigation.createWebBrowserHistoryPort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initializeKoin()

    ComposeViewport(viewportContainerId = "webApp") {
        when (resolveWebEntryMode(webLocationSearch())) {
            WebEntryMode.NETWORK_SMOKE -> WebNetworkDiagnosticScreen()
            WebEntryMode.DESIGN_SYSTEM_SHOWCASE -> DesignSystemShowcase()
            WebEntryMode.NORMAL -> WebApp()
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
