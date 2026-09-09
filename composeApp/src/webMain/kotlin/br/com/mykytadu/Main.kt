package br.com.mykytadu

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import br.com.mykytadu.di.initializeKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initializeKoin()

    ComposeViewport(viewportContainerId = "webApp") {
        App()
    }
}
