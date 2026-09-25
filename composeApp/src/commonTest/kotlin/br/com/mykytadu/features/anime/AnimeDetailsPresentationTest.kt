package br.com.mykytadu.features.anime

import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AnimeImages
import br.com.mykytadu.domain.model.AnimeRelation
import br.com.mykytadu.domain.model.AnimeTitles
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.PartialDate
import br.com.mykytadu.domain.model.Studio
import br.com.mykytadu.domain.model.Trailer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AnimeDetailsPresentationTest {

    @Test
    fun `titulo prioriza ingles romaji nativo e sinonimos validos`() {
        fun details(titles: AnimeTitles) = AnimeDetails(CatalogAnimeId("20"), titles = titles)

        assertEquals("English", details(AnimeTitles(english = " English ", romaji = "Romaji")).displayTitle("Fallback"))
        assertEquals("Romaji", details(AnimeTitles(english = " ", romaji = "Romaji", native = "Nativo")).displayTitle("Fallback"))
        assertEquals("Nativo", details(AnimeTitles(native = "Nativo", synonyms = listOf("Sinônimo"))).displayTitle("Fallback"))
        assertEquals("Sinônimo", details(AnimeTitles(synonyms = listOf(" ", " Sinônimo "))).displayTitle("Fallback"))
        assertEquals("Fallback", details(AnimeTitles()).displayTitle("Fallback"))
    }

    @Test
    fun `imagens ignoram urls vazias e priorizam capa extragrande`() {
        val details = AnimeDetails(
            id = CatalogAnimeId("20"),
            images = AnimeImages(
                coverExtraLarge = " https://img/extra.jpg ",
                coverLarge = "https://img/large.jpg",
                banner = " https://img/banner.jpg ",
            ),
        )

        assertEquals("https://img/extra.jpg", details.coverUrl())
        assertEquals("https://img/banner.jpg", details.bannerUrl())
        assertNull(AnimeDetails(CatalogAnimeId("21"), images = AnimeImages(coverLarge = " ")).coverUrl())
    }

    @Test
    fun `sinopse remove markup normaliza quebras e decodifica entidades basicas`() {
        val details = AnimeDetails(
            id = CatalogAnimeId("20"),
            description = "<p>Primeira &amp; segunda<br>linha</p><p>Final&nbsp;!</p>",
        )

        assertEquals("Primeira & segunda\nlinha\n\nFinal !", details.displayDescription())
        assertNull(AnimeDetails(CatalogAnimeId("21"), description = " <b> </b> ").displayDescription())
    }

    @Test
    fun `conteudo complementar remove valores vazios e duplicados`() {
        val details = AnimeDetails(
            id = CatalogAnimeId("20"),
            genres = listOf(" Action ", "", "Action", "Drama"),
            studios = listOf(Studio(1, " Bones "), Studio(2, " "), Studio(3, "Bones")),
        )

        assertEquals(listOf("Action", "Drama"), details.displayGenres())
        assertEquals(listOf("Bones"), details.displayStudios())
    }

    @Test
    fun `data parcial apresenta somente componentes recebidos`() {
        assertEquals("05/04/2009", PartialDate(2009, 4, 5).displayValue())
        assertEquals("04/2009", PartialDate(2009, 4).displayValue())
        assertEquals("2009", PartialDate(2009).displayValue())
        assertNull(PartialDate().displayValue())
    }

    @Test
    fun `trailer gera link somente para provedores reconhecidos`() {
        assertEquals("https://www.youtube.com/watch?v=abc", Trailer(" abc ", " YouTube ").externalUrl())
        assertEquals("https://www.dailymotion.com/video/xyz", Trailer("xyz", "dailymotion").externalUrl())
        assertNull(Trailer("abc", "outro").externalUrl())
        assertNull(Trailer(" ", "youtube").externalUrl())
        assertNull(Trailer("abc&autoplay=1", "youtube").externalUrl())
    }

    @Test
    fun `relacao adapta titulo e capa sem expor valores vazios`() {
        val relation = AnimeRelation(
            id = CatalogAnimeId("21"),
            titles = AnimeTitles(romaji = " Bebop Movie "),
            coverMedium = " https://img/medium.jpg ",
        )

        assertEquals("Bebop Movie", relation.displayTitle("Indisponível"))
        assertEquals("https://img/medium.jpg", relation.coverUrl())
        assertEquals("Indisponível", AnimeRelation(CatalogAnimeId("22")).displayTitle("Indisponível"))
    }
}
