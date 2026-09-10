package br.com.mykytadu.di

import io.ktor.client.engine.js.Js
import kotlin.test.Test
import kotlin.test.assertSame

class WebHttpEngineTest {

    @Test
    fun `web usa explicitamente a engine Js baseada em Fetch`() {
        assertSame(Js, provideHttpClientEngine())
    }
}
