package br.com.mykytadu.data.mapper

import br.com.mykytadu.data.remote.anilist.dto.AnimeDetailsDataDto
import br.com.mykytadu.data.remote.anilist.dto.AnimeDetailsDto
import br.com.mykytadu.domain.model.AnimeDetails
import br.com.mykytadu.domain.model.AniListAnimeId

internal fun AnimeDetailsDataDto.toAnimeDetails(): AnimeDetails =
    requireNotNull(media) {
        "Anime details media must be present."
    }.toAnimeDetails()

internal fun AnimeDetailsDto.toAnimeDetails(): AnimeDetails =
    AnimeDetails(
        id = AniListAnimeId(id),
        idMal = idMal,
        titles = title.toAnimeTitles(synonyms),
        images = coverImage.toAnimeImages(bannerImage),
        description = description,
        format = format.toAnimeFormat(),
        status = status.toAnimeReleaseStatus(),
        episodes = episodes,
        duration = duration,
        season = season.toAnimeSeason(),
        seasonYear = seasonYear,
        isAdult = isAdult,
        startDate = startDate.toPartialDate(),
        endDate = endDate.toPartialDate(),
        genres = genres,
        averageScore = averageScore,
        studios = studios.toStudios(),
        trailer = trailer.toTrailer(),
        relations = relations.toAnimeRelations(),
    )