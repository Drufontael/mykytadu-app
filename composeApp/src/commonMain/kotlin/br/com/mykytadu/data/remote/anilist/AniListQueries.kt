package br.com.mykytadu.data.remote.anilist

internal object AniListQueries {

    val searchAnime = """
        query SearchAnime(
            ${'$'}search: String!
            ${'$'}page: Int!
            ${'$'}perPage: Int!
        ) {
            Page(
                page: ${'$'}page
                perPage: ${'$'}perPage
            ) {
                pageInfo {
                    currentPage
                    lastPage
                    hasNextPage
                    perPage
                    total
                }

                media(
                    search: ${'$'}search
                    type: ANIME
                    sort: SEARCH_MATCH
                ) {
                    id
                    idMal

                    title {
                        romaji
                        english
                        native
                    }

                    coverImage {
                        large
                        color
                    }

                    format
                    status
                    episodes
                    season
                    seasonYear
                    averageScore
                }
            }
        }
    """.trimIndent()

    val getAnimeDetails = """
        query GetAnimeDetails(${'$'}id: Int!) {
            Media(
                id: ${'$'}id
                type: ANIME
            ) {
                id
                idMal

                title {
                    romaji
                    english
                    native
                }

                synonyms
                description(asHtml: false)

                coverImage {
                    extraLarge
                    large
                    color
                }

                bannerImage
                format
                status
                source
                episodes
                duration
                season
                seasonYear
                countryOfOrigin
                isAdult

                startDate {
                    year
                    month
                    day
                }

                endDate {
                    year
                    month
                    day
                }

                genres
                averageScore
                meanScore
                popularity
                favourites

                studios(isMain: true) {
                    nodes {
                        id
                        name
                        isAnimationStudio
                    }
                }

                trailer {
                    id
                    site
                    thumbnail
                }

                nextAiringEpisode {
                    episode
                    airingAt
                    timeUntilAiring
                }

                relations {
                    edges {
                        relationType

                        node {
                            id
                            type
                            format
                            status

                            title {
                                romaji
                                english
                                native
                            }

                            coverImage {
                                medium
                            }
                        }
                    }
                }

                externalLinks {
                    id
                    site
                    url
                    type
                }
            }
        }
    """.trimIndent()
}
