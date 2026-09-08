package br.com.mykytadu.data.mapper

import br.com.mykytadu.domain.model.AnimeFormat
import br.com.mykytadu.domain.model.AnimeReleaseStatus
import br.com.mykytadu.domain.model.AnimeSeason
import br.com.mykytadu.domain.model.MediaType
import br.com.mykytadu.domain.model.AnimeRelationType

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AnimeEnumMapperTest {
    @Test
    fun `deve converter TV para AnimeFormat`() {
        assertEquals(AnimeFormat.TV, "TV".toAnimeFormat())
    }

    @Test
    fun `deve converter TV_SHORT para AnimeFormat`() {
        assertEquals(AnimeFormat.TV_SHORT, "TV_SHORT".toAnimeFormat())
    }

    @Test
    fun `deve converter MOVIE para AnimeFormat`() {
        assertEquals(AnimeFormat.MOVIE, "MOVIE".toAnimeFormat())
    }

    @Test
    fun `deve converter SPECIAL para AnimeFormat`() {
        assertEquals(AnimeFormat.SPECIAL, "SPECIAL".toAnimeFormat())
    }

    @Test
    fun `deve converter OVA para AnimeFormat`() {
        assertEquals(AnimeFormat.OVA, "OVA".toAnimeFormat())
    }

    @Test
    fun `deve converter ONA para AnimeFormat`() {
        assertEquals(AnimeFormat.ONA, "ONA".toAnimeFormat())
    }

    @Test
    fun `deve converter MUSIC para AnimeFormat`() {
        assertEquals(AnimeFormat.MUSIC, "MUSIC".toAnimeFormat())
    }

    @Test
    fun `deve converter MANGA para AnimeFormat`() {
        assertEquals(AnimeFormat.MANGA, "MANGA".toAnimeFormat())
    }

    @Test
    fun `deve converter NOVEL para AnimeFormat`() {
        assertEquals(AnimeFormat.NOVEL, "NOVEL".toAnimeFormat())
    }

    @Test
    fun `deve converter ONE_SHOT para AnimeFormat`() {
        assertEquals(AnimeFormat.ONE_SHOT, "ONE_SHOT".toAnimeFormat())
    }

    @Test
    fun `deve preservar null em AnimeFormat`() {
        val remote: String? = null
        assertNull(remote.toAnimeFormat())
    }

    @Test
    fun `deve retornar UNKNOWN para desconhecido em AnimeFormat`() {
        assertEquals(AnimeFormat.UNKNOWN, "FUTURE_VALUE".toAnimeFormat())
    }

    @Test
    fun `deve retornar UNKNOWN para vazio em AnimeFormat`() {
        assertEquals(AnimeFormat.UNKNOWN, "".toAnimeFormat())
    }

    @Test
    fun `deve retornar UNKNOWN para whitespace em AnimeFormat`() {
        assertEquals(AnimeFormat.UNKNOWN, " \t\n".toAnimeFormat())
    }

    @Test
    fun `deve retornar UNKNOWN para capitalizacao diferente em AnimeFormat`() {
        assertEquals(AnimeFormat.UNKNOWN, "tv".toAnimeFormat())
    }

    @Test
    fun `deve retornar UNKNOWN para espacos externos em AnimeFormat`() {
        assertEquals(AnimeFormat.UNKNOWN, " TV ".toAnimeFormat())
    }

    @Test
    fun `deve retornar UNKNOWN para prefixo em AnimeFormat`() {
        assertEquals(AnimeFormat.UNKNOWN, "PREFIX_TV".toAnimeFormat())
    }

    @Test
    fun `deve retornar UNKNOWN para sufixo em AnimeFormat`() {
        assertEquals(AnimeFormat.UNKNOWN, "TV_SUFFIX".toAnimeFormat())
    }

    @Test
    fun `deve converter FINISHED para AnimeReleaseStatus`() {
        assertEquals(AnimeReleaseStatus.FINISHED, "FINISHED".toAnimeReleaseStatus())
    }

    @Test
    fun `deve converter RELEASING para AnimeReleaseStatus`() {
        assertEquals(AnimeReleaseStatus.RELEASING, "RELEASING".toAnimeReleaseStatus())
    }

    @Test
    fun `deve converter NOT_YET_RELEASED para AnimeReleaseStatus`() {
        assertEquals(AnimeReleaseStatus.NOT_YET_RELEASED, "NOT_YET_RELEASED".toAnimeReleaseStatus())
    }

    @Test
    fun `deve converter CANCELLED para AnimeReleaseStatus`() {
        assertEquals(AnimeReleaseStatus.CANCELLED, "CANCELLED".toAnimeReleaseStatus())
    }

    @Test
    fun `deve converter HIATUS para AnimeReleaseStatus`() {
        assertEquals(AnimeReleaseStatus.HIATUS, "HIATUS".toAnimeReleaseStatus())
    }

    @Test
    fun `deve preservar null em AnimeReleaseStatus`() {
        val remote: String? = null
        assertNull(remote.toAnimeReleaseStatus())
    }

    @Test
    fun `deve retornar UNKNOWN para desconhecido em AnimeReleaseStatus`() {
        assertEquals(AnimeReleaseStatus.UNKNOWN, "FUTURE_VALUE".toAnimeReleaseStatus())
    }

    @Test
    fun `deve retornar UNKNOWN para vazio em AnimeReleaseStatus`() {
        assertEquals(AnimeReleaseStatus.UNKNOWN, "".toAnimeReleaseStatus())
    }

    @Test
    fun `deve retornar UNKNOWN para whitespace em AnimeReleaseStatus`() {
        assertEquals(AnimeReleaseStatus.UNKNOWN, " \t\n".toAnimeReleaseStatus())
    }

    @Test
    fun `deve retornar UNKNOWN para capitalizacao diferente em AnimeReleaseStatus`() {
        assertEquals(AnimeReleaseStatus.UNKNOWN, "finished".toAnimeReleaseStatus())
    }

    @Test
    fun `deve retornar UNKNOWN para espacos externos em AnimeReleaseStatus`() {
        assertEquals(AnimeReleaseStatus.UNKNOWN, " FINISHED ".toAnimeReleaseStatus())
    }

    @Test
    fun `deve retornar UNKNOWN para prefixo em AnimeReleaseStatus`() {
        assertEquals(AnimeReleaseStatus.UNKNOWN, "PREFIX_FINISHED".toAnimeReleaseStatus())
    }

    @Test
    fun `deve retornar UNKNOWN para sufixo em AnimeReleaseStatus`() {
        assertEquals(AnimeReleaseStatus.UNKNOWN, "FINISHED_SUFFIX".toAnimeReleaseStatus())
    }

    @Test
    fun `deve converter WINTER para AnimeSeason`() {
        assertEquals(AnimeSeason.WINTER, "WINTER".toAnimeSeason())
    }

    @Test
    fun `deve converter SPRING para AnimeSeason`() {
        assertEquals(AnimeSeason.SPRING, "SPRING".toAnimeSeason())
    }

    @Test
    fun `deve converter SUMMER para AnimeSeason`() {
        assertEquals(AnimeSeason.SUMMER, "SUMMER".toAnimeSeason())
    }

    @Test
    fun `deve converter FALL para AnimeSeason`() {
        assertEquals(AnimeSeason.FALL, "FALL".toAnimeSeason())
    }

    @Test
    fun `deve preservar null em AnimeSeason`() {
        val remote: String? = null
        assertNull(remote.toAnimeSeason())
    }

    @Test
    fun `deve retornar UNKNOWN para desconhecido em AnimeSeason`() {
        assertEquals(AnimeSeason.UNKNOWN, "FUTURE_VALUE".toAnimeSeason())
    }

    @Test
    fun `deve retornar UNKNOWN para vazio em AnimeSeason`() {
        assertEquals(AnimeSeason.UNKNOWN, "".toAnimeSeason())
    }

    @Test
    fun `deve retornar UNKNOWN para whitespace em AnimeSeason`() {
        assertEquals(AnimeSeason.UNKNOWN, " \t\n".toAnimeSeason())
    }

    @Test
    fun `deve retornar UNKNOWN para capitalizacao diferente em AnimeSeason`() {
        assertEquals(AnimeSeason.UNKNOWN, "winter".toAnimeSeason())
    }

    @Test
    fun `deve retornar UNKNOWN para espacos externos em AnimeSeason`() {
        assertEquals(AnimeSeason.UNKNOWN, " WINTER ".toAnimeSeason())
    }

    @Test
    fun `deve retornar UNKNOWN para prefixo em AnimeSeason`() {
        assertEquals(AnimeSeason.UNKNOWN, "PREFIX_WINTER".toAnimeSeason())
    }

    @Test
    fun `deve retornar UNKNOWN para sufixo em AnimeSeason`() {
        assertEquals(AnimeSeason.UNKNOWN, "WINTER_SUFFIX".toAnimeSeason())
    }

    @Test
    fun `deve converter ANIME para MediaType`() {
        assertEquals(MediaType.ANIME, "ANIME".toMediaType())
    }

    @Test
    fun `deve converter MANGA para MediaType`() {
        assertEquals(MediaType.MANGA, "MANGA".toMediaType())
    }

    @Test
    fun `deve preservar null em MediaType`() {
        val remote: String? = null
        assertNull(remote.toMediaType())
    }

    @Test
    fun `deve retornar UNKNOWN para desconhecido em MediaType`() {
        assertEquals(MediaType.UNKNOWN, "FUTURE_VALUE".toMediaType())
    }

    @Test
    fun `deve retornar UNKNOWN para vazio em MediaType`() {
        assertEquals(MediaType.UNKNOWN, "".toMediaType())
    }

    @Test
    fun `deve retornar UNKNOWN para whitespace em MediaType`() {
        assertEquals(MediaType.UNKNOWN, " \t\n".toMediaType())
    }

    @Test
    fun `deve retornar UNKNOWN para capitalizacao diferente em MediaType`() {
        assertEquals(MediaType.UNKNOWN, "anime".toMediaType())
    }

    @Test
    fun `deve retornar UNKNOWN para espacos externos em MediaType`() {
        assertEquals(MediaType.UNKNOWN, " ANIME ".toMediaType())
    }

    @Test
    fun `deve retornar UNKNOWN para prefixo em MediaType`() {
        assertEquals(MediaType.UNKNOWN, "PREFIX_ANIME".toMediaType())
    }

    @Test
    fun `deve retornar UNKNOWN para sufixo em MediaType`() {
        assertEquals(MediaType.UNKNOWN, "ANIME_SUFFIX".toMediaType())
    }

    @Test
    fun `deve converter ADAPTATION para AnimeRelationType`() {
        assertEquals(AnimeRelationType.ADAPTATION, "ADAPTATION".toAnimeRelationType())
    }

    @Test
    fun `deve converter PREQUEL para AnimeRelationType`() {
        assertEquals(AnimeRelationType.PREQUEL, "PREQUEL".toAnimeRelationType())
    }

    @Test
    fun `deve converter SEQUEL para AnimeRelationType`() {
        assertEquals(AnimeRelationType.SEQUEL, "SEQUEL".toAnimeRelationType())
    }

    @Test
    fun `deve converter PARENT para AnimeRelationType`() {
        assertEquals(AnimeRelationType.PARENT, "PARENT".toAnimeRelationType())
    }

    @Test
    fun `deve converter SIDE_STORY para AnimeRelationType`() {
        assertEquals(AnimeRelationType.SIDE_STORY, "SIDE_STORY".toAnimeRelationType())
    }

    @Test
    fun `deve converter CHARACTER para AnimeRelationType`() {
        assertEquals(AnimeRelationType.CHARACTER, "CHARACTER".toAnimeRelationType())
    }

    @Test
    fun `deve converter SUMMARY para AnimeRelationType`() {
        assertEquals(AnimeRelationType.SUMMARY, "SUMMARY".toAnimeRelationType())
    }

    @Test
    fun `deve converter ALTERNATIVE para AnimeRelationType`() {
        assertEquals(AnimeRelationType.ALTERNATIVE, "ALTERNATIVE".toAnimeRelationType())
    }

    @Test
    fun `deve converter SPIN_OFF para AnimeRelationType`() {
        assertEquals(AnimeRelationType.SPIN_OFF, "SPIN_OFF".toAnimeRelationType())
    }

    @Test
    fun `deve converter OTHER para AnimeRelationType`() {
        assertEquals(AnimeRelationType.OTHER, "OTHER".toAnimeRelationType())
    }

    @Test
    fun `deve converter SOURCE para AnimeRelationType`() {
        assertEquals(AnimeRelationType.SOURCE, "SOURCE".toAnimeRelationType())
    }

    @Test
    fun `deve converter COMPILATION para AnimeRelationType`() {
        assertEquals(AnimeRelationType.COMPILATION, "COMPILATION".toAnimeRelationType())
    }

    @Test
    fun `deve converter CONTAINS para AnimeRelationType`() {
        assertEquals(AnimeRelationType.CONTAINS, "CONTAINS".toAnimeRelationType())
    }

    @Test
    fun `deve preservar null em AnimeRelationType`() {
        val remote: String? = null
        assertNull(remote.toAnimeRelationType())
    }

    @Test
    fun `deve retornar UNKNOWN para desconhecido em AnimeRelationType`() {
        assertEquals(AnimeRelationType.UNKNOWN, "FUTURE_VALUE".toAnimeRelationType())
    }

    @Test
    fun `deve retornar UNKNOWN para vazio em AnimeRelationType`() {
        assertEquals(AnimeRelationType.UNKNOWN, "".toAnimeRelationType())
    }

    @Test
    fun `deve retornar UNKNOWN para whitespace em AnimeRelationType`() {
        assertEquals(AnimeRelationType.UNKNOWN, " \t\n".toAnimeRelationType())
    }

    @Test
    fun `deve retornar UNKNOWN para capitalizacao diferente em AnimeRelationType`() {
        assertEquals(AnimeRelationType.UNKNOWN, "sequel".toAnimeRelationType())
    }

    @Test
    fun `deve retornar UNKNOWN para espacos externos em AnimeRelationType`() {
        assertEquals(AnimeRelationType.UNKNOWN, " SEQUEL ".toAnimeRelationType())
    }

    @Test
    fun `deve retornar UNKNOWN para prefixo em AnimeRelationType`() {
        assertEquals(AnimeRelationType.UNKNOWN, "PREFIX_SEQUEL".toAnimeRelationType())
    }

    @Test
    fun `deve retornar UNKNOWN para sufixo em AnimeRelationType`() {
        assertEquals(AnimeRelationType.UNKNOWN, "SEQUEL_SUFFIX".toAnimeRelationType())
    }

    @Test
    fun `nao deve normalizar separadores de formato status ou relacao`() {
        assertEquals(AnimeFormat.UNKNOWN, "TV-SHORT".toAnimeFormat())
        assertEquals(AnimeFormat.UNKNOWN, "TVSHORT".toAnimeFormat())
        assertEquals(AnimeReleaseStatus.UNKNOWN, "NOT-YET-RELEASED".toAnimeReleaseStatus())
        assertEquals(AnimeReleaseStatus.UNKNOWN, "NOTYETRELEASED".toAnimeReleaseStatus())
        assertEquals(AnimeRelationType.UNKNOWN, "SPIN-OFF".toAnimeRelationType())
        assertEquals(AnimeRelationType.UNKNOWN, "SPINOFF".toAnimeRelationType())
    }
}
