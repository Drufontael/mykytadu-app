package br.com.mykytadu.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PaginationTest {

    @Test
    fun `deve preservar pagina valida`() {
        val pageInfo = pageInfo(hasNextPage = true)

        assertEquals(1, pageInfo.currentPage)
        assertEquals(3, pageInfo.lastPage)
        assertTrue(pageInfo.hasNextPage)
        assertEquals(10, pageInfo.perPage)
        assertEquals(25, pageInfo.total)
    }

    @Test
    fun `deve aceitar ultima pagina ausente`() {
        assertNull(pageInfo(lastPage = null).lastPage)
    }

    @Test
    fun `deve aceitar total ausente`() {
        assertNull(pageInfo(total = null).total)
    }

    @Test
    fun `deve rejeitar pagina atual igual a zero`() {
        assertFailsWith<IllegalArgumentException> {
            pageInfo(currentPage = 0)
        }
    }

    @Test
    fun `deve rejeitar pagina atual negativa`() {
        assertFailsWith<IllegalArgumentException> {
            pageInfo(currentPage = -1)
        }
    }

    @Test
    fun `deve rejeitar quantidade por pagina igual a zero`() {
        assertFailsWith<IllegalArgumentException> {
            pageInfo(perPage = 0)
        }
    }

    @Test
    fun `deve rejeitar quantidade por pagina negativa`() {
        assertFailsWith<IllegalArgumentException> {
            pageInfo(perPage = -1)
        }
    }

    @Test
    fun `deve rejeitar ultima pagina igual a zero`() {
        assertFailsWith<IllegalArgumentException> {
            pageInfo(lastPage = 0)
        }
    }

    @Test
    fun `deve rejeitar ultima pagina negativa`() {
        assertFailsWith<IllegalArgumentException> {
            pageInfo(lastPage = -1)
        }
    }

    @Test
    fun `deve rejeitar total negativo`() {
        assertFailsWith<IllegalArgumentException> {
            pageInfo(total = -1)
        }
    }

    @Test
    fun `nao deve inferir proxima pagina pela quantidade de itens`() {
        val result = PagedResult(
            items = listOf("anime"),
            pageInfo = pageInfo(hasNextPage = false),
        )

        assertFalse(result.pageInfo.hasNextPage)
    }

    @Test
    fun `deve aceitar pagina vazia`() {
        val result = PagedResult<String>(
            items = emptyList(),
            pageInfo = pageInfo(),
        )

        assertTrue(result.items.isEmpty())
    }

    @Test
    fun `deve preservar itens e informacoes da pagina`() {
        val pageInfo = pageInfo()
        val result = PagedResult(
            items = listOf("one", "two"),
            pageInfo = pageInfo,
        )

        assertEquals(listOf("one", "two"), result.items)
        assertEquals(pageInfo, result.pageInfo)
    }

    @Test
    fun `deve manter snapshot da lista recebida`() {
        val source = mutableListOf("one")
        val result = PagedResult(
            items = source,
            pageInfo = pageInfo(),
        )

        source += "two"

        assertEquals(listOf("one"), result.items)
    }

    private fun pageInfo(
        currentPage: Int = 1,
        lastPage: Int? = 3,
        hasNextPage: Boolean = false,
        perPage: Int = 10,
        total: Int? = 25,
    ) = PageInfo(
        currentPage = currentPage,
        lastPage = lastPage,
        hasNextPage = hasNextPage,
        perPage = perPage,
        total = total,
    )
}
