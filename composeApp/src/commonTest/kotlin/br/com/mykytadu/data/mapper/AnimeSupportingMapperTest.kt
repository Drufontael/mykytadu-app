package br.com.mykytadu.data.mapper

import br.com.mykytadu.data.remote.anilist.dto.AniListDateDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeCoverImageDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeStudioConnectionDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeStudioDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeTitleDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeTrailerDto
import br.com.mykytadu.domain.model.AnimeImages
import br.com.mykytadu.domain.model.PartialDate
import br.com.mykytadu.domain.model.Studio
import br.com.mykytadu.domain.model.Trailer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnimeSupportingMapperTest {
    @Test
    fun `deve preservar todos os titulos aplicando somente trim`() {
        val result = AnimeTitleDto(" Romaji  Title ", "\tEnglish\n", " 日本語 ").toAnimeTitles()
        assertEquals("Romaji  Title", result.romaji)
        assertEquals("English", result.english)
        assertEquals("日本語", result.native)
        assertTrue(result.synonyms.isEmpty())
    }

    @Test
    fun `deve preservar ausencia de titulos e sinonimos`() {
        val result = AnimeTitleDto().toAnimeTitles()
        assertNull(result.romaji)
        assertNull(result.english)
        assertNull(result.native)
        assertTrue(result.synonyms.isEmpty())
    }

    @Test
    fun `deve descartar titulos em branco sem fallback`() {
        for (blank in listOf("", " ", "\t\n")) {
            val result = AnimeTitleDto(blank, blank, blank).toAnimeTitles()
            assertNull(result.romaji)
            assertNull(result.english)
            assertNull(result.native)
        }
        val result = AnimeTitleDto(romaji = "Romaji").toAnimeTitles(listOf("Alias"))
        assertEquals("Romaji", result.romaji)
        assertNull(result.english)
        assertNull(result.native)
        assertEquals(listOf("Alias"), result.synonyms)
    }

    @Test
    fun `deve filtrar sinonimos em branco preservando ordem e duplicatas`() {
        val synonyms = mutableListOf(" B ", "", " \t ", "A", " B ")
        val result = AnimeTitleDto().toAnimeTitles(synonyms)
        synonyms.clear()
        assertEquals(listOf("B", "A", "B"), result.synonyms)
        assertTrue(AnimeTitleDto().toAnimeTitles(emptyList()).synonyms.isEmpty())
    }

    @Test
    fun `deve mapear todas as imagens e cor com trim`() {
        val result = AnimeCoverImageDto(
            large = " https://example.org/large ",
            extraLarge = " https://example.org/extra ",
            color = " #abcdef ",
        ).toAnimeImages(banner = " https://example.org/banner ")
        assertEquals(
            AnimeImages("https://example.org/large", "https://example.org/extra",
                "https://example.org/banner", "#abcdef"),
            result,
        )
    }

    @Test
    fun `deve mapear apenas large sem inventar extraLarge ou banner`() {
        assertEquals(AnimeImages(coverLarge = "large"),
            AnimeCoverImageDto(large = "large").toAnimeImages())
    }

    @Test
    fun `deve aceitar capa ausente inclusive com banner independente`() {
        val cover: AnimeCoverImageDto? = null
        assertEquals(AnimeImages(), cover.toAnimeImages())
        assertEquals(AnimeImages(banner = "banner"), cover.toAnimeImages(" banner "))
        assertEquals(AnimeImages(), AnimeCoverImageDto().toAnimeImages())
    }

    @Test
    fun `deve converter imagens e cores em branco para null`() {
        for (blank in listOf("", " ", "\t\n")) {
            assertEquals(AnimeImages(),
                AnimeCoverImageDto(large = blank, extraLarge = blank, color = blank)
                    .toAnimeImages(blank))
        }
    }

    @Test
    fun `nao deve validar URL cor ou usar medium como fallback`() {
        assertEquals(
            AnimeImages("custom://host/image", "texto sem URL", "/relative", "cor livre"),
            AnimeCoverImageDto(large = "custom://host/image", extraLarge = "texto sem URL",
                color = "cor livre").toAnimeImages("/relative"),
        )
        assertEquals(AnimeImages(), AnimeCoverImageDto(medium = "medium").toAnimeImages())
    }

    @Test
    fun `deve preservar data completa ano e ano com mes`() {
        assertEquals(PartialDate(2024, 2, 29), AniListDateDto(2024, 2, 29).toPartialDate())
        assertEquals(PartialDate(2024), AniListDateDto(year = 2024).toPartialDate())
        assertEquals(PartialDate(2024, 2), AniListDateDto(2024, 2).toPartialDate())
    }

    @Test
    fun `deve retornar null para data ausente ou sem componentes`() {
        val date: AniListDateDto? = null
        assertNull(date.toPartialDate())
        assertNull(AniListDateDto().toPartialDate())
    }

    @Test
    fun `deve preservar componentes independentes sem preenchimento automatico`() {
        for (year in listOf(null, 2024)) {
            for (month in listOf(null, 2)) {
                for (day in listOf(null, 29)) {
                    val result = AniListDateDto(year, month, day).toPartialDate()
                    if (year == null && month == null && day == null) {
                        assertNull(result)
                    } else {
                        assertEquals(PartialDate(year, month, day), result)
                    }
                }
            }
        }
    }

    @Test
    fun `deve aceitar limites de mes e dia sem validar calendario`() {
        for (month in listOf(1, 12)) {
            for (day in listOf(1, 31)) {
                assertEquals(PartialDate(month = month, day = day),
                    AniListDateDto(month = month, day = day).toPartialDate())
            }
        }
        assertEquals(PartialDate(2023, 2, 31), AniListDateDto(2023, 2, 31).toPartialDate())
    }

    @Test
    fun `nao deve limitar ano`() {
        for (year in listOf(Int.MIN_VALUE, -1, 0, Int.MAX_VALUE)) {
            assertEquals(PartialDate(year), AniListDateDto(year = year).toPartialDate())
        }
    }

    @Test
    fun `deve descartar data inteira quando mes for invalido`() {
        for (month in listOf(Int.MIN_VALUE, -1, 0, 13, Int.MAX_VALUE)) {
            assertNull(AniListDateDto(2024, month, 15).toPartialDate())
        }
    }

    @Test
    fun `deve descartar data inteira quando dia for invalido`() {
        for (day in listOf(Int.MIN_VALUE, -1, 0, 32, Int.MAX_VALUE)) {
            assertNull(AniListDateDto(2024, 6, day).toPartialDate())
        }
    }

    @Test
    fun `deve aceitar conexao de estudios ausente ou vazia`() {
        val connection: AnimeStudioConnectionDto? = null
        assertTrue(connection.toStudios().isEmpty())
        assertTrue(AnimeStudioConnectionDto().toStudios().isEmpty())
    }

    @Test
    fun `deve preservar estudio valido e indicacao de animacao`() {
        for (animation in listOf(null, false, true)) {
            val dto = AnimeStudioDto(1, " Studio  Name ", animation)
            assertEquals(Studio(1, "Studio  Name", animation), dto.toStudio())
            assertEquals(listOf(Studio(1, "Studio  Name", animation)),
                AnimeStudioConnectionDto(listOf(dto)).toStudios())
        }
    }

    @Test
    fun `deve descartar estudios com ID nao positivo ou nome em branco`() {
        for (id in listOf(Int.MIN_VALUE, -1, 0)) {
            assertNull(AnimeStudioDto(id, "Valid").toStudio())
        }
        for (name in listOf("", " ", "\t\n")) {
            assertNull(AnimeStudioDto(1, name).toStudio())
        }
    }

    @Test
    fun `deve descartar apenas nos invalidos preservando ordem e duplicatas`() {
        val valid = AnimeStudioDto(5, " B ", false)
        val result = AnimeStudioConnectionDto(listOf(
            valid, AnimeStudioDto(0, "Invalid"), AnimeStudioDto(2, "A", true),
            AnimeStudioDto(3, " "), valid,
        )).toStudios()
        assertEquals(listOf(Studio(5, "B", false), Studio(2, "A", true),
            Studio(5, "B", false)), result)
    }

    @Test
    fun `deve mapear trailer completo sem gerar URL ou restringir site`() {
        assertEquals(
            Trailer("id livre / 123", "Dailymotion", "custom://thumbnail"),
            AnimeTrailerDto(" id livre / 123 ", " Dailymotion ", " custom://thumbnail ")
                .toTrailer(),
        )
    }

    @Test
    fun `deve aceitar thumbnail ausente ou em branco`() {
        for (thumbnail in listOf(null, "", " ", "\t\n")) {
            assertEquals(Trailer("abc", "site"),
                AnimeTrailerDto("abc", "site", thumbnail).toTrailer())
        }
    }

    @Test
    fun `deve descartar trailer ausente ou sem ID`() {
        val trailer: AnimeTrailerDto? = null
        assertNull(trailer.toTrailer())
        for (id in listOf(null, "", " ", "\t\n")) {
            assertNull(AnimeTrailerDto(id, "site", "thumb").toTrailer())
        }
    }

    @Test
    fun `deve descartar trailer sem site`() {
        for (site in listOf(null, "", " ", "\t\n")) {
            assertNull(AnimeTrailerDto("id", site, "thumb").toTrailer())
        }
    }
}
