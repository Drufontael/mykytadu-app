package br.com.mykytadu

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import br.com.mykytadu.di.initializeKoin

fun main() {
    initializeKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "MykytaDu") {
            App()
        }
    }
}