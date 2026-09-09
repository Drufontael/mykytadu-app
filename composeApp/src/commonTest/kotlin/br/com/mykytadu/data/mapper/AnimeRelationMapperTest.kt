package br.com.mykytadu.data.mapper

import br.com.mykytadu.data.remote.anilist.dto.AnimeCoverImageDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeRelationEdgeDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeRelationNodeDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeRelationsDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeTitleDto
import br.com.mykytadu.domain.model.AnimeFormat
import br.com.mykytadu.domain.model.AnimeRelationType
import br.com.mykytadu.domain.model.AnimeReleaseStatus
import br.com.mykytadu.domain.model.MediaType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AnimeRelationMapperTest {

    @Test
    fun `deve mapear relacao completa`() {
        val edge = relationEdge(
            relationType = "PREQUEL",
            id = 21,
            type = "ANIME",
            format = "TV",
            status = "FINISHED",
            coverMedium = " https://example.org/medium ",
        )

        val result = edge.toAnimeRelation()

        requireNotNull(result)
        assertEquals(21, result.id.value)
        assertEquals(AnimeRelationType.PREQUEL, result.relationType)
        assertEquals(MediaType.ANIME, result.mediaType)
        assertEquals("Related Romaji", result.titles.romaji)
        assertEquals("Related English", result.titles.english)
        assertEquals("関連作品", result.titles.native)
        assertEquals(AnimeFormat.TV, result.format)
        assertEquals(AnimeReleaseStatus.FINISHED, result.status)
        assertEquals("https://example.org/medium", result.coverMedium)
    }

    @Test
    fun `deve preservar valores desconhecidos como unknown`() {
        val result = relationEdge(
            relationType = "FUTURE_RELATION",
            id = 21,
            type = "LIGHT_NOVEL",
            format = "FUTURE_FORMAT",
            status = "FUTURE_STATUS",
        ).toAnimeRelation()

        requireNotNull(result)
        assertEquals(AnimeRelationType.UNKNOWN, result.relationType)
        assertEquals(MediaType.UNKNOWN, result.mediaType)
        assertEquals(AnimeFormat.UNKNOWN, result.format)
        assertEquals(AnimeReleaseStatus.UNKNOWN, result.status)
    }

    @Test
    fun `deve preservar ausencia de valores opcionais`() {
        val result = relationEdge(
            relationType = null,
            id = 21,
            type = null,
            format = null,
            status = null,
        ).toAnimeRelation()

        requireNotNull(result)
        assertNull(result.relationType)
        assertNull(result.mediaType)
        assertNull(result.format)
        assertNull(result.status)
        assertNull(result.coverMedium)
    }

    @Test
    fun `deve descartar relacao sem no`() {
        val edge = AnimeRelationEdgeDto(
            relationType = "SEQUEL",
            node = null,
        )

        assertNull(edge.toAnimeRelation())
    }

    @Test
    fun `deve descartar relacao com id invalido`() {
        assertNull(
            relationEdge(
                relationType = "SEQUEL",
                id = 0,
            ).toAnimeRelation(),
        )
    }

    @Test
    fun `deve descartar somente relacoes invalidas e preservar ordem`() {
        val relations = AnimeRelationsDto(
            edges = listOf(
                relationEdge(relationType = "PREQUEL", id = 10),
                AnimeRelationEdgeDto(
                    relationType = "OTHER",
                    node = null,
                ),
                relationEdge(relationType = "SEQUEL", id = 20),
                relationEdge(relationType = "OTHER", id = -1),
            ),
        )

        val result = relations.toAnimeRelations()

        assertEquals(listOf(10, 20), result.map { it.id.value })
        assertEquals(
            listOf(AnimeRelationType.PREQUEL, AnimeRelationType.SEQUEL),
            result.map { it.relationType },
        )
    }

    @Test
    fun `deve aceitar conexao ausente ou vazia`() {
        assertTrue((null as AnimeRelationsDto?).toAnimeRelations().isEmpty())
        assertTrue(AnimeRelationsDto(edges = emptyList()).toAnimeRelations().isEmpty())
    }

    @Test
    fun `deve converter capa medium em branco para null`() {
        val result = relationEdge(
            relationType = "OTHER",
            id = 21,
            coverMedium = "   ",
        ).toAnimeRelation()

        requireNotNull(result)
        assertNull(result.coverMedium)
    }

    private fun relationEdge(
        relationType: String?,
        id: Int,
        type: String? = null,
        format: String? = null,
        status: String? = null,
        coverMedium: String? = null,
    ): AnimeRelationEdgeDto =
        AnimeRelationEdgeDto(
            relationType = relationType,
            node = AnimeRelationNodeDto(
                id = id,
                type = type,
                format = format,
                status = status,
                title = AnimeTitleDto(
                    romaji = " Related Romaji ",
                    english = " Related English ",
                    native = " 関連作品 ",
                ),
                coverImage = coverMedium?.let {
                    AnimeCoverImageDto(medium = it)
                },
            ),
        )
}