package br.com.mykytadu.presentation.components

import kotlin.test.Test
import kotlin.test.assertEquals

class AppLoadingTest {

    @Test
    fun `descricao vazia durante carregamento de recurso usa fallback acessivel`() {
        assertEquals("Carregando", loadingContentDescription(""))
        assertEquals("Carregando", loadingContentDescription("   "))
    }

    @Test
    fun `descricao especifica carregada e preservada`() {
        assertEquals(
            "Pesquisando animes",
            loadingContentDescription("Pesquisando animes"),
        )
    }
}
