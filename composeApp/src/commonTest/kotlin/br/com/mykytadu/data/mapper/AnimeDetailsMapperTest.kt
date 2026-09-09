package br.com.mykytadu.data.mapper

import br.com.mykytadu.data.remote.anilist.dto.AniListDateDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeCoverImageDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeDetailsDataDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeDetailsDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeRelationEdgeDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeRelationNodeDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeRelationsDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeStudioConnectionDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeStudioDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeTitleDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeTrailerDto
import br.com.mykytadu.domain.model.AnimeFormat
import br.com.mykytadu.domain.model.AnimeRelationType
import br.com.mykytadu.domain.model.AnimeReleaseStatus
import br.com.mykytadu.domain.model.AnimeSeason
import br.com.mykytadu.domain.model.MediaType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnimeDetailsMapperTest {

    @Test
    fun `deve mapear detalhes completos para dominio`() {
        val dto = AnimeDetailsDataDto(
            media = AnimeDetailsDto(
                id = 5114,
                idMal = 5114,
                title = AnimeTitleDto(
                    romaji = " Fullmetal Alchemist ",
                    english = " Fullmetal Alchemist Brotherhood ",
                    native = " 鋼の錬金術師 ",
                ),
                synonyms = listOf(" FMA Brotherhood ", "   "),
                description = "  Description preserved\n",
                coverImage = AnimeCoverImageDto(
                    large = " https://example.org/large ",
                    extraLarge = " https://example.org/extra ",
                    color = " #abcdef ",
                ),
                bannerImage = " https://example.org/banner ",
                format = "TV",
                status = "FINISHED",
                episodes = 64,
                duration = 24,
                season = "SPRING",
                seasonYear = 2009,
                isAdult = false,
                startDate = AniListDateDto(
                    year = 2009,
                    month = 4,
                    day = 5,
                ),
                endDate = AniListDateDto(
                    year = 2010,
                    month = 7,
                    day = 4,
                ),
                genres = listOf("Action", "Adventure"),
                averageScore = 91,
                studios = AnimeStudioConnectionDto(
                    nodes = listOf(
                        AnimeStudioDto(
                            id = 4,
                            name = " Bones ",
                            isAnimationStudio = true,
                        ),
                    ),
                ),
                trailer = AnimeTrailerDto(
                    id = " trailer-id ",
                    site = " youtube ",
                    thumbnail = " https://example.org/thumbnail ",
                ),
                relations = AnimeRelationsDto(
                    edges = listOf(
                        AnimeRelationEdgeDto(
                            relationType = "ADAPTATION",
                            node = AnimeRelationNodeDto(
                                id = 121,
                                type = "MANGA",
                                format = "MANGA",
                                status = "FINISHED",
                                title = AnimeTitleDto(
                                    romaji = " Hagane no Renkinjutsushi ",
                                    english = null,
                                    native = " 鋼の錬金術師 ",
                                ),
                                coverImage = AnimeCoverImageDto(
                                    medium = " https://example.org/relation ",
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val result = dto.toAnimeDetails()

        assertEquals(5114, result.id.value)
        assertEquals(5114, result.idMal)
        assertEquals("Fullmetal Alchemist", result.titles.romaji)
        assertEquals("Fullmetal Alchemist Brotherhood", result.titles.english)
        assertEquals("鋼の錬金術師", result.titles.native)
        assertEquals(listOf("FMA Brotherhood"), result.titles.synonyms)

        assertEquals("https://example.org/large", result.images.coverLarge)
        assertEquals("https://example.org/extra", result.images.coverExtraLarge)
        assertEquals("https://example.org/banner", result.images.banner)
        assertEquals("#abcdef", result.images.color)

        assertEquals("  Description preserved\n", result.description)
        assertEquals(AnimeFormat.TV, result.format)
        assertEquals(AnimeReleaseStatus.FINISHED, result.status)
        assertEquals(64, result.episodes)
        assertEquals(24, result.duration)
        assertEquals(AnimeSeason.SPRING, result.season)
        assertEquals(2009, result.seasonYear)
        assertEquals(false, result.isAdult)
        assertEquals(2009, result.startDate?.year)
        assertEquals(4, result.startDate?.month)
        assertEquals(5, result.startDate?.day)
        assertEquals(2010, result.endDate?.year)
        assertEquals(listOf("Action", "Adventure"), result.genres)
        assertEquals(91, result.averageScore)

        assertEquals(1, result.studios.size)
        assertEquals(4, result.studios.single().id)
        assertEquals("Bones", result.studios.single().name)
        assertEquals(true, result.studios.single().isAnimationStudio)

        assertEquals("trailer-id", result.trailer?.id)
        assertEquals("youtube", result.trailer?.site)
        assertEquals(
            "https://example.org/thumbnail",
            result.trailer?.thumbnail,
        )

        val relation = result.relations.single()
        assertEquals(121, relation.id.value)
        assertEquals(AnimeRelationType.ADAPTATION, relation.relationType)
        assertEquals(MediaType.MANGA, relation.mediaType)
        assertEquals(AnimeFormat.MANGA, relation.format)
        assertEquals(AnimeReleaseStatus.FINISHED, relation.status)
        assertEquals(
            "https://example.org/relation",
            relation.coverMedium,
        )
    }

    @Test
    fun `deve preservar opcionais ausentes e colecoes vazias`() {
        val result = AnimeDetailsDto(
            id = 1,
            title = AnimeTitleDto(null, null, null),
        ).toAnimeDetails()

        assertEquals(1, result.id.value)
        assertNull(result.idMal)
        assertNull(result.description)
        assertNull(result.format)
        assertNull(result.status)
        assertNull(result.episodes)
        assertNull(result.duration)
        assertNull(result.season)
        assertNull(result.seasonYear)
        assertNull(result.isAdult)
        assertNull(result.startDate)
        assertNull(result.endDate)
        assertNull(result.averageScore)
        assertNull(result.trailer)
        assertTrue(result.titles.synonyms.isEmpty())
        assertTrue(result.genres.isEmpty())
        assertTrue(result.studios.isEmpty())
        assertTrue(result.relations.isEmpty())
    }

    @Test
    fun `deve preservar enums desconhecidos como unknown`() {
        val result = AnimeDetailsDto(
            id = 1,
            title = AnimeTitleDto(null, null, null),
            format = "FUTURE_FORMAT",
            status = "FUTURE_STATUS",
            season = "MONSOON",
        ).toAnimeDetails()

        assertEquals(AnimeFormat.UNKNOWN, result.format)
        assertEquals(AnimeReleaseStatus.UNKNOWN, result.status)
        assertEquals(AnimeSeason.UNKNOWN, result.season)
    }

    @Test
    fun `deve descartar datas opcionais invalidas`() {
        val result = AnimeDetailsDto(
            id = 1,
            title = AnimeTitleDto(null, null, null),
            startDate = AniListDateDto(
                year = 2024,
                month = 13,
                day = 1,
            ),
            endDate = AniListDateDto(
                year = 2024,
                month = 12,
                day = 32,
            ),
        ).toAnimeDetails()

        assertNull(result.startDate)
        assertNull(result.endDate)
    }

    @Test
    fun `deve descartar apenas studios e relacoes invalidos`() {
        val result = AnimeDetailsDto(
            id = 1,
            title = AnimeTitleDto(null, null, null),
            studios = AnimeStudioConnectionDto(
                nodes = listOf(
                    AnimeStudioDto(
                        id = 0,
                        name = "Invalid",
                        isAnimationStudio = true,
                    ),
                    AnimeStudioDto(
                        id = 7,
                        name = "Valid Studio",
                        isAnimationStudio = false,
                    ),
                ),
            ),
            relations = AnimeRelationsDto(
                edges = listOf(
                    AnimeRelationEdgeDto(
                        relationType = "OTHER",
                        node = null,
                    ),
                    AnimeRelationEdgeDto(
                        relationType = "SEQUEL",
                        node = AnimeRelationNodeDto(
                            id = 8,
                            type = "ANIME",
                            title = AnimeTitleDto(null, null, null),
                        ),
                    ),
                ),
            ),
        ).toAnimeDetails()

        assertEquals(listOf(7), result.studios.map { it.id })
        assertEquals(listOf(8), result.relations.map { it.id.value })
    }

    @Test
    fun `deve falhar quando media de detalhes estiver ausente`() {
        assertFailsWith<IllegalArgumentException> {
            AnimeDetailsDataDto(media = null).toAnimeDetails()
        }
    }

    @Test
    fun `deve falhar para id principal invalido`() {
        assertFailsWith<IllegalArgumentException> {
            AnimeDetailsDto(
                id = 0,
                title = AnimeTitleDto(null, null, null),
            ).toAnimeDetails()
        }
    }
}