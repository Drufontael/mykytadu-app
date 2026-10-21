package br.com.mykytadu.features.home

import br.com.mykytadu.domain.model.AnimeFormat
import br.com.mykytadu.domain.model.CatalogAnimeId
import br.com.mykytadu.domain.model.LibraryStatus
import br.com.mykytadu.domain.result.RepositoryFailure

/**
 * Representa os estados de ciclo de vida de uma seção independente da tela Home.
 * O isolamento por seção garante que falhas de rede em serviços externos (ex: AniList)
 * não afetem nem bloqueiem o acesso aos dados da biblioteca pessoal local-first.
 */
sealed interface HomeSectionState<out T> {
    data object Loading : HomeSectionState<Nothing>

    data class Success<T>(val data: T) : HomeSectionState<T>

    data class Empty(val message: String? = null) : HomeSectionState<Nothing>

    data class Failure(
        val reason: RepositoryFailure? = null,
        val message: String? = null,
    ) : HomeSectionState<Nothing>
}

/**
 * Modelo de apresentação para o card de obra em andamento na seção "Continuar assistindo".
 */
data class HomeContinueWatchingItemUiState(
    val animeId: CatalogAnimeId,
    val title: String,
    val coverUrl: String? = null,
    val progressCurrent: Int,
    val progressTotal: Int? = null,
    val progressRatio: Float? = null,
    val updatedAtEpochMillis: Long,
) {
    val progressLabel: String
        get() = progressTotal?.let { "$progressCurrent / $it" } ?: "$progressCurrent"

    val canIncrement: Boolean
        get() = progressTotal == null || progressCurrent < progressTotal
}

/**
 * Modelo de apresentação para o card de obra na seção "Atualizados recentemente".
 */
data class HomeRecentItemUiState(
    val animeId: CatalogAnimeId,
    val title: String,
    val coverUrl: String? = null,
    val status: LibraryStatus,
    val progressCurrent: Int,
    val progressTotal: Int? = null,
    val updatedAtEpochMillis: Long,
) {
    val progressLabel: String
        get() = progressTotal?.let { "$progressCurrent / $it" } ?: "$progressCurrent"
}

/**
 * Modelo de apresentação para obras em alta na seção "Tendências AniList".
 */
data class HomeTrendingItemUiState(
    val animeId: CatalogAnimeId,
    val title: String,
    val coverUrl: String? = null,
    val format: AnimeFormat? = null,
    val averageScore: Int? = null,
    val episodes: Int? = null,
)

/**
 * Estado agregado da tela Home híbrida.
 */
data class HomeUiState(
    val continueWatchingSection: HomeSectionState<List<HomeContinueWatchingItemUiState>> = HomeSectionState.Loading,
    val recentSection: HomeSectionState<List<HomeRecentItemUiState>> = HomeSectionState.Loading,
    val trendingSection: HomeSectionState<List<HomeTrendingItemUiState>> = HomeSectionState.Loading,
    val isRefreshing: Boolean = false,
) {
    /**
     * Indica se o usuário possui qualquer conteúdo pessoal cadastrado em sua biblioteca local.
     */
    val hasPersonalContent: Boolean
        get() = (continueWatchingSection as? HomeSectionState.Success)?.data?.isNotEmpty() == true ||
            (recentSection as? HomeSectionState.Success)?.data?.isNotEmpty() == true

    /**
     * Indica se a biblioteca pessoal está totalmente vazia (ambas as seções pessoais em Empty).
     */
    val isLibraryEmpty: Boolean
        get() = continueWatchingSection is HomeSectionState.Empty &&
            recentSection is HomeSectionState.Empty
}
