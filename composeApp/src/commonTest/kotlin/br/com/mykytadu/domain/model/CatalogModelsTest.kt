package br.com.mykytadu.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CatalogModelsTest {
    private val titles = AnimeTitles("Romaji", "English", "日本語", listOf("Alias", "Outro"))
    private val images = AnimeImages("large", "extraLarge", "banner", "#abcdef")

    @Test
    fun `deve construir resumo completo sem contratos remotos`() {
        val summary = AnimeSummary(
            id = AniListAnimeId(20), idMal = 30, titles = titles, images = images,
            format = AnimeFormat.TV, status = AnimeReleaseStatus.FINISHED, episodes = 12,
            season = AnimeSeason.SPRING, seasonYear = 2020, averageScore = 80,
        )
        assertEquals(AniListAnimeId(20), summary.id)
        assertEquals(30, summary.idMal)
        assertEquals(titles, summary.titles)
        assertEquals(images, summary.images)
        assertEquals(AnimeFormat.TV, summary.format)
        assertEquals(AnimeReleaseStatus.FINISHED, summary.status)
        assertEquals(12, summary.episodes)
        assertEquals(AnimeSeason.SPRING, summary.season)
        assertEquals(2020, summary.seasonYear)
        assertEquals(80, summary.averageScore)
    }

    @Test
    fun `deve aceitar resumo com opcionais ausentes`() {
        val summary = AnimeSummary(AniListAnimeId(1))
        assertNull(summary.idMal)
        assertNull(summary.titles.english)
        assertNull(summary.titles.romaji)
        assertNull(summary.titles.native)
        assertTrue(summary.titles.synonyms.isEmpty())
        assertEquals(AnimeImages(), summary.images)
        assertNull(summary.format)
        assertNull(summary.status)
        assertNull(summary.episodes)
        assertNull(summary.season)
        assertNull(summary.seasonYear)
        assertNull(summary.averageScore)
    }

    @Test
    fun `deve construir detalhes completos independentes do resumo`() {
        val studios = listOf(Studio(1, "A", true), Studio(2, "B", false))
        val relations = listOf(
            AnimeRelation(AniListAnimeId(2), AnimeRelationType.SEQUEL, MediaType.ANIME,
                titles, AnimeFormat.MOVIE, AnimeReleaseStatus.FINISHED, "medium"),
            AnimeRelation(AniListAnimeId(3), AnimeRelationType.ADAPTATION, MediaType.MANGA,
                format = AnimeFormat.ONE_SHOT),
        )
        val details = AnimeDetails(
            id = AniListAnimeId(1), idMal = 10, titles = titles, images = images,
            description = "<br>Sinopse original", format = AnimeFormat.TV,
            status = AnimeReleaseStatus.FINISHED, episodes = 12, duration = 24,
            season = AnimeSeason.WINTER, seasonYear = 2020, isAdult = false,
            startDate = PartialDate(2020, 1, 2), endDate = PartialDate(2020, 3),
            genres = listOf("Action", "Gênero aberto"), averageScore = 85,
            studios = studios, trailer = Trailer("abc", "dailymotion"), relations = relations,
        )
        assertEquals(AniListAnimeId(1), details.id)
        assertEquals(10, details.idMal)
        assertEquals(titles, details.titles)
        assertEquals(images, details.images)
        assertEquals("<br>Sinopse original", details.description)
        assertEquals(AnimeFormat.TV, details.format)
        assertEquals(AnimeReleaseStatus.FINISHED, details.status)
        assertEquals(12, details.episodes)
        assertEquals(24, details.duration)
        assertEquals(AnimeSeason.WINTER, details.season)
        assertEquals(2020, details.seasonYear)
        assertEquals(false, details.isAdult)
        assertEquals(PartialDate(2020, 1, 2), details.startDate)
        assertEquals(PartialDate(2020, 3), details.endDate)
        assertEquals(listOf("Action", "Gênero aberto"), details.genres)
        assertEquals(85, details.averageScore)
        assertEquals(studios, details.studios)
        assertEquals(Trailer("abc", "dailymotion"), details.trailer)
        assertNull(details.trailer?.thumbnail)
        assertEquals(relations, details.relations)
        assertEquals("medium", details.relations[0].coverMedium)
        assertEquals(MediaType.MANGA, details.relations[1].mediaType)
        assertEquals(AnimeFormat.ONE_SHOT, details.relations[1].format)
    }

    @Test
    fun `deve aceitar detalhes com colecoes vazias e opcionais ausentes`() {
        val details = AnimeDetails(AniListAnimeId(1))
        assertTrue(details.genres.isEmpty())
        assertTrue(details.studios.isEmpty())
        assertTrue(details.relations.isEmpty())
        assertTrue(details.titles.synonyms.isEmpty())
        assertEquals(AnimeImages(), details.images)
        assertNull(details.idMal)
        assertNull(details.description)
        assertNull(details.format)
        assertNull(details.status)
        assertNull(details.episodes)
        assertNull(details.duration)
        assertNull(details.season)
        assertNull(details.seasonYear)
        assertNull(details.isAdult)
        assertNull(details.startDate)
        assertNull(details.endDate)
        assertNull(details.averageScore)
        assertNull(details.trailer)
    }

    @Test
    fun `deve preservar titulos e snapshot dos sinonimos`() {
        val source = mutableListOf("Alias", "", "Alias")
        val value = AnimeTitles("Romaji", "English", "日本語", source)
        source.clear()
        assertEquals("Romaji", value.romaji)
        assertEquals("English", value.english)
        assertEquals("日本語", value.native)
        assertEquals(listOf("Alias", "", "Alias"), value.synonyms)
        assertTrue(AnimeTitles(synonyms = emptyList()).synonyms.isEmpty())
    }

    @Test
    fun `deve manter snapshots das colecoes de detalhes`() {
        val genres = mutableListOf("Action", "Drama")
        val studios = mutableListOf(Studio(1, "A"), Studio(2, "B"))
        val relations = mutableListOf(AnimeRelation(AniListAnimeId(2)), AnimeRelation(AniListAnimeId(3)))
        val details = AnimeDetails(AniListAnimeId(1), genres = genres, studios = studios, relations = relations)
        genres.clear()
        studios.clear()
        relations.clear()
        assertEquals(listOf("Action", "Drama"), details.genres)
        assertEquals(listOf(Studio(1, "A"), Studio(2, "B")), details.studios)
        assertEquals(listOf(AniListAnimeId(2), AniListAnimeId(3)), details.relations.map { it.id })
    }

    @Test
    fun `deve preservar imagens opcionais sem transformar strings`() {
        assertEquals("large", images.coverLarge)
        assertEquals("extraLarge", images.coverExtraLarge)
        assertEquals("banner", images.banner)
        assertEquals("#abcdef", images.color)
        val partial = AnimeImages(coverLarge = " endereço original ")
        assertEquals(" endereço original ", partial.coverLarge)
        assertNull(partial.coverExtraLarge)
        assertNull(partial.banner)
        assertNull(partial.color)
        assertNull(AnimeImages().coverLarge)
    }

    @Test
    fun `deve aceitar relacao somente com identificador`() {
        val relation = AnimeRelation(AniListAnimeId(2))
        assertNull(relation.relationType)
        assertNull(relation.mediaType)
        assertNull(relation.format)
        assertNull(relation.status)
        assertNull(relation.coverMedium)
        assertNull(relation.titles.english)
    }

    @Test
    fun `nao deve validar strings de estudio ou trailer no contrato`() {
        assertEquals("", Studio(1, "").name)
        assertNull(Studio(1, "A").isAnimationStudio)
        assertEquals(Trailer("", "", "thumb"), Trailer("", "", "thumb"))
    }

    @Test
    fun `todos os enums devem oferecer UNKNOWN`() {
        assertTrue(AnimeFormat.UNKNOWN in AnimeFormat.entries)
        assertTrue(AnimeReleaseStatus.UNKNOWN in AnimeReleaseStatus.entries)
        assertTrue(AnimeSeason.UNKNOWN in AnimeSeason.entries)
        assertTrue(MediaType.UNKNOWN in MediaType.entries)
        assertTrue(AnimeRelationType.UNKNOWN in AnimeRelationType.entries)
    }
}
