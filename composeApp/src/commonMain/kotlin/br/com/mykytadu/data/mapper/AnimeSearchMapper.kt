package br.com.mykytadu.data.mapper

import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchDataDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchItemDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeSearchPageDto
import br.com.mykytadu.data.remote.anilist.dto.PageInfoDto
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.AniListAnimeId
import br.com.mykytadu.domain.model.PageInfo
import br.com.mykytadu.domain.model.PagedResult

internal fun AnimeSearchDataDto.toPagedAnimeSummaries(): PagedResult<AnimeSummary> =
    requireNotNull(page) {
        "Anime search page must be present."
    }.toPagedAnimeSummaries()

private fun AnimeSearchPageDto.toPagedAnimeSummaries(): PagedResult<AnimeSummary> =
    PagedResult(
        items = media.map { it.toAnimeSummary() },
        pageInfo = requireNotNull(pageInfo) {
            "Anime search page info must be present."
        }.toPageInfo(),
    )

internal fun AnimeSearchItemDto.toAnimeSummary(): AnimeSummary =
    AnimeSummary(
        id = AniListAnimeId(id),
        idMal = idMal,
        titles = title.toAnimeTitles(),
        images = coverImage.toAnimeImages(),
        format = format.toAnimeFormat(),
        status = status.toAnimeReleaseStatus(),
        episodes = episodes,
        season = season.toAnimeSeason(),
        seasonYear = seasonYear,
        averageScore = averageScore,
    )

private fun PageInfoDto.toPageInfo(): PageInfo =
    PageInfo(
        currentPage = currentPage,
        lastPage = lastPage,
        hasNextPage = hasNextPage,
        perPage = perPage,
        total = total,
    )