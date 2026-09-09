package br.com.mykytadu.data.mapper

import br.com.mykytadu.data.remote.anilist.dto.AnimeCoverImageDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchDataDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchItemDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchPageDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeTitleDto
import br.com.mykytadu.data.remote.anilist.dto.PageInfoDto
import br.com.mykytadu.domain.model.AnimeFormat
import br.com.mykytadu.domain.model.AnimeReleaseStatus
import br.com.mykytadu.domain.model.AnimeSeason
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnimeSearchMapperTest {

    @Test
    fun `deve mapear pesquisa completa para dominio`() {
        val dto = AnimeSearchDataDto(
            page = AnimeSearchPageDto(
                pageInfo = PageInfoDto(
                    currentPage = 2,
                    lastPage = 8,
                    hasNextPage = true,
                    perPage = 20,
                    total = 145,
                ),
                media = listOf(
                    AnimeSearchItemDto(
                        id = 5114,
                        idMal = 5114,
                        title = AnimeTitleDto(
                            romaji = " Fullmetal Alchemist ",
                            english = " Fullmetal Alchemist Brotherhood ",
                            native = " 鋼の錬金術師 ",
                        ),
                        coverImage = AnimeCoverImageDto(
                            large = " https://example.org/large ",
                            extraLarge = " https://example.org/extra ",
                            color = " #abcdef ",
                        ),
                        format = "TV",
                        status = "FINISHED",
                        episodes = 64,
                        season = "SPRING",
                        seasonYear = 2009,
                        averageScore = 91,
                    ),
                ),
            ),
        )

        val result = dto.toPagedAnimeSummaries()
        val anime = result.items.single()

        assertEquals(5114, anime.id.value)
        assertEquals(5114, anime.idMal)
        assertEquals("Fullmetal Alchemist", anime.titles.romaji)
        assertEquals("Fullmetal Alchemist Brotherhood", anime.titles.english)
        assertEquals("鋼の錬金術師", anime.titles.native)
        assertTrue(anime.titles.synonyms.isEmpty())
        assertEquals("https://example.org/large", anime.images.coverLarge)
        assertEquals("https://example.org/extra", anime.images.coverExtraLarge)
        assertNull(anime.images.banner)
        assertEquals("#abcdef", anime.images.color)
        assertEquals(AnimeFormat.TV, anime.format)
        assertEquals(AnimeReleaseStatus.FINISHED, anime.status)
        assertEquals(64, anime.episodes)
        assertEquals(AnimeSeason.SPRING, anime.season)
        assertEquals(2009, anime.seasonYear)
        assertEquals(91, anime.averageScore)

        assertEquals(2, result.pageInfo.currentPage)
        assertEquals(8, result.pageInfo.lastPage)
        assertTrue(result.pageInfo.hasNextPage)
        assertEquals(20, result.pageInfo.perPage)
        assertEquals(145, result.pageInfo.total)
    }

    @Test
    fun `deve preservar campos opcionais ausentes`() {
        val dto = searchData(
            item = AnimeSearchItemDto(
                id = 1,
                title = AnimeTitleDto(null, null, null),
            ),
        )

        val anime = dto.toPagedAnimeSummaries().items.single()

        assertNull(anime.idMal)
        assertNull(anime.titles.romaji)
        assertNull(anime.titles.english)
        assertNull(anime.titles.native)
        assertTrue(anime.titles.synonyms.isEmpty())
        assertNull(anime.images.coverLarge)
        assertNull(anime.images.coverExtraLarge)
        assertNull(anime.images.banner)
        assertNull(anime.images.color)
        assertNull(anime.format)
        assertNull(anime.status)
        assertNull(anime.episodes)
        assertNull(anime.season)
        assertNull(anime.seasonYear)
        assertNull(anime.averageScore)
    }

    @Test
    fun `deve converter enums desconhecidos para unknown`() {
        val dto = searchData(
            item = AnimeSearchItemDto(
                id = 1,
                title = AnimeTitleDto(null, null, null),
                format = "FUTURE_FORMAT",
                status = "FUTURE_STATUS",
                season = "MONSOON",
            ),
        )

        val anime = dto.toPagedAnimeSummaries().items.single()

        assertEquals(AnimeFormat.UNKNOWN, anime.format)
        assertEquals(AnimeReleaseStatus.UNKNOWN, anime.status)
        assertEquals(AnimeSeason.UNKNOWN, anime.season)
    }

    @Test
    fun `deve aceitar pagina vazia`() {
        val dto = AnimeSearchDataDto(
            page = AnimeSearchPageDto(
                pageInfo = PageInfoDto(
                    currentPage = 1,
                    hasNextPage = false,
                    perPage = 20,
                ),
                media = emptyList(),
            ),
        )

        val result = dto.toPagedAnimeSummaries()

        assertTrue(result.items.isEmpty())
        assertFalse(result.pageInfo.hasNextPage)
    }

    @Test
    fun `deve preservar hasNextPage sem inferir pelo tamanho da lista`() {
        val dto = AnimeSearchDataDto(
            page = AnimeSearchPageDto(
                pageInfo = PageInfoDto(
                    currentPage = 1,
                    hasNextPage = true,
                    perPage = 20,
                ),
                media = emptyList(),
            ),
        )

        val result = dto.toPagedAnimeSummaries()

        assertTrue(result.items.isEmpty())
        assertTrue(result.pageInfo.hasNextPage)
    }

    @Test
    fun `deve falhar quando page estiver ausente`() {
        assertFailsWith<IllegalArgumentException> {
            AnimeSearchDataDto(page = null).toPagedAnimeSummaries()
        }
    }

    @Test
    fun `deve falhar quando pageInfo estiver ausente`() {
        assertFailsWith<IllegalArgumentException> {
            AnimeSearchDataDto(
                page = AnimeSearchPageDto(
                    pageInfo = null,
                    media = emptyList(),
                ),
            ).toPagedAnimeSummaries()
        }
    }

    @Test
    fun `deve falhar sem descartar silenciosamente anime com id invalido`() {
        assertFailsWith<IllegalArgumentException> {
            searchData(
                item = AnimeSearchItemDto(
                    id = 0,
                    title = AnimeTitleDto(null, null, null),
                ),
            ).toPagedAnimeSummaries()
        }
    }

    private fun searchData(
        item: AnimeSearchItemDto,
    ): AnimeSearchDataDto =
        AnimeSearchDataDto(
            page = AnimeSearchPageDto(
                pageInfo = PageInfoDto(
                    currentPage = 1,
                    lastPage = 1,
                    hasNextPage = false,
                    perPage = 20,
                    total = 1,
                ),
                media = listOf(item),
            ),
        )
}