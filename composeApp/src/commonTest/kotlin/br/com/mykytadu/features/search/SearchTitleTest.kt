package br.com.mykytadu.features.search

import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.AnimeTitles
import br.com.mykytadu.domain.model.AniListAnimeId
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchTitleTest {

    @Test
    fun `titulo segue ordem de apresentacao e ignora candidatos vazios`() {
        val anime = summary(
            AnimeTitles(
                english = "  English title  ",
                romaji = "Romaji title",
                native = "Native title",
                synonyms = listOf("Synonym"),
            ),
        )

        assertEquals("English title", anime.displayTitle("Unavailable"))
    }

    @Test
    fun `titulo usa romaji nativo e primeiro sinonimo utilizavel como fallbacks`() {
        assertEquals(
            "Romaji title",
            summary(AnimeTitles(english = " ", romaji = "Romaji title"))
                .displayTitle("Unavailable"),
        )
        assertEquals(
            "Native title",
            summary(AnimeTitles(native = "Native title"))
                .displayTitle("Unavailable"),
        )
        assertEquals(
            "Synonym",
            summary(AnimeTitles(synonyms = listOf(" ", " Synonym ", "Other")))
                .displayTitle("Unavailable"),
        )
    }

    @Test
    fun `titulo usa recurso localizado quando todos os candidatos estao ausentes`() {
        assertEquals(
            "Título indisponível",
            summary(AnimeTitles(english = "", romaji = " ", synonyms = listOf("  ")))
                .displayTitle("Título indisponível"),
        )
    }

    private fun summary(titles: AnimeTitles): AnimeSummary =
        AnimeSummary(
            id = AniListAnimeId(1),
            titles = titles,
        )
}
