package br.com.mykytadu.features.search

import br.com.mykytadu.domain.model.AnimeImages
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.CatalogAnimeId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SearchCoverUrlTest {

    @Test
    fun `capa extra large tem prioridade e remove espacos externos`() {
        assertEquals(
            "https://example.test/extra-large.jpg",
            summary(
                coverExtraLarge = "  https://example.test/extra-large.jpg  ",
                coverLarge = "https://example.test/large.jpg",
            ).searchCoverUrl(),
        )
    }

    @Test
    fun `capa large atende ausencia ou valor vazio da extra large`() {
        assertEquals(
            "https://example.test/large.jpg",
            summary(
                coverExtraLarge = "   ",
                coverLarge = "https://example.test/large.jpg",
            ).searchCoverUrl(),
        )
    }

    @Test
    fun `ausencia de capas mantem fallback visual sem url`() {
        assertNull(summary().searchCoverUrl())
    }

    private fun summary(
        coverExtraLarge: String? = null,
        coverLarge: String? = null,
    ): AnimeSummary = AnimeSummary(
        id = CatalogAnimeId("1"),
        images = AnimeImages(
            coverExtraLarge = coverExtraLarge,
            coverLarge = coverLarge,
        ),
    )
}
