package br.com.mykytadu.web.diagnostics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.com.mykytadu.core.theme.AppDimensions
import br.com.mykytadu.core.theme.AppTheme
import br.com.mykytadu.domain.model.AnimeSummary
import br.com.mykytadu.domain.model.PagedResult
import br.com.mykytadu.domain.repository.AnimeRepository
import br.com.mykytadu.domain.result.RepositoryFailure
import br.com.mykytadu.domain.result.RepositoryResult
import br.com.mykytadu.presentation.components.AppButton
import br.com.mykytadu.presentation.components.AppCard
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatformTools

internal fun isWebNetworkSmokeEnabled(search: String): Boolean =
    search
        .removePrefix("?")
        .split('&')
        .any { it == "network-smoke=true" }

internal sealed interface WebNetworkDiagnosticState {
    data object Inactive : WebNetworkDiagnosticState
    data object Loading : WebNetworkDiagnosticState
    data class Success(
        val itemCount: Int,
        val firstId: Int?,
        val firstTitle: String?,
        val coverUrl: String?,
    ) : WebNetworkDiagnosticState

    data object Cancelled : WebNetworkDiagnosticState
    data class Failure(
        val category: String,
        val phase: WebDiagnosticPhase,
        val technicalDetails: WebDiagnosticTechnicalDetails,
    ) : WebNetworkDiagnosticState
}

internal enum class WebDiagnosticPhase(
    val displayName: String,
) {
    REPOSITORY_RESOLUTION("resolução do repository"),
    REQUEST_START("início da chamada"),
    HTTP_TRANSPORT("transporte HTTP"),
    RESPONSE_HANDLING("tratamento da resposta"),
    DOMAIN_MAPPING("mapeamento"),
    IMAGE_PROBE("sonda de imagem"),
}

internal data class WebDiagnosticCause(
    val className: String,
    val message: String,
)

internal data class WebDiagnosticTechnicalDetails(
    val causes: List<WebDiagnosticCause>,
    val chainTruncated: Boolean,
    val cycleDetected: Boolean,
)

internal sealed interface WebImageProbeState {
    data object Inactive : WebImageProbeState
    data object Loading : WebImageProbeState
    data class Loaded(val width: Int, val height: Int) : WebImageProbeState
    data object Unavailable : WebImageProbeState
}

internal class WebNetworkDiagnosticRunner(
    private val repository: AnimeRepository,
    private val scope: CoroutineScope,
    private val onStateChanged: (WebNetworkDiagnosticState) -> Unit,
) {
    private var currentJob: Job? = null
    private var activeRequestId = 0

    fun start() {
        if (currentJob?.isActive == true) return

        val requestId = ++activeRequestId
        onStateChanged(WebNetworkDiagnosticState.Loading)

        currentJob = scope.launch {
            try {
                val result = repository.searchAnime(
                    query = DIAGNOSTIC_QUERY,
                    page = DIAGNOSTIC_PAGE,
                    perPage = DIAGNOSTIC_PAGE_SIZE,
                )

                if (requestId != activeRequestId) return@launch

                onStateChanged(result.toDiagnosticState())
            } catch (cause: CancellationException) {
                if (requestId == activeRequestId) {
                    onStateChanged(WebNetworkDiagnosticState.Cancelled)
                }
                throw cause
            } catch (cause: Throwable) {
                if (requestId == activeRequestId) {
                    onStateChanged(
                        WebNetworkDiagnosticState.Failure(
                            category = "falha não classificada",
                            phase = WebDiagnosticPhase.REQUEST_START,
                            technicalDetails = cause.toWebDiagnosticTechnicalDetails(),
                        ),
                    )
                }
            } finally {
                if (requestId == activeRequestId) {
                    currentJob = null
                }
            }
        }
    }

    fun cancel() {
        val job = currentJob ?: return

        activeRequestId++
        currentJob = null
        job.cancel()
        onStateChanged(WebNetworkDiagnosticState.Cancelled)
    }

    private fun RepositoryResult<PagedResult<AnimeSummary>>.toDiagnosticState():
        WebNetworkDiagnosticState =
        when (this) {
            is RepositoryResult.Success -> {
                val firstItem = value.items.firstOrNull()

                WebNetworkDiagnosticState.Success(
                    itemCount = value.items.size,
                    firstId = firstItem?.id?.value,
                    firstTitle = firstItem?.titles?.english
                        ?: firstItem?.titles?.romaji
                        ?: firstItem?.titles?.native,
                    coverUrl = firstItem?.images?.coverLarge,
                )
            }

            is RepositoryResult.Failure -> toWebDiagnosticFailure()
        }

    private companion object {
        const val DIAGNOSTIC_QUERY = "Naruto"
        const val DIAGNOSTIC_PAGE = 1
        const val DIAGNOSTIC_PAGE_SIZE = 3
    }
}

internal fun RepositoryResult.Failure.toWebDiagnosticFailure(): WebNetworkDiagnosticState.Failure =
    WebNetworkDiagnosticState.Failure(
        category = reason.toDiagnosticCategory(),
        phase = reason.toDiagnosticPhase(cause),
        technicalDetails = cause.toWebDiagnosticTechnicalDetails(),
    )

private fun RepositoryFailure.toDiagnosticCategory(): String =
    when (this) {
        RepositoryFailure.InvalidInput -> "entrada inválida"
        RepositoryFailure.Unavailable -> "serviço indisponível"
        RepositoryFailure.Timeout -> "tempo limite"
        RepositoryFailure.RateLimited -> "limite de requisições"
        RepositoryFailure.InvalidData -> "dados inválidos"
        RepositoryFailure.RemoteFailure -> "falha remota"
        RepositoryFailure.Unknown -> "falha não classificada"
    }

private fun RepositoryFailure.toDiagnosticPhase(cause: Throwable?): WebDiagnosticPhase =
    when (this) {
        RepositoryFailure.InvalidInput -> WebDiagnosticPhase.REQUEST_START
        RepositoryFailure.Timeout,
        RepositoryFailure.Unavailable,
        RepositoryFailure.Unknown,
            -> WebDiagnosticPhase.HTTP_TRANSPORT

        RepositoryFailure.RateLimited,
        RepositoryFailure.RemoteFailure,
            -> WebDiagnosticPhase.RESPONSE_HANDLING

        RepositoryFailure.InvalidData ->
            if (cause is IllegalArgumentException) {
                WebDiagnosticPhase.DOMAIN_MAPPING
            } else {
                WebDiagnosticPhase.RESPONSE_HANDLING
            }
    }

@Composable
internal fun WebNetworkDiagnosticScreen() {
    val repositoryResolution = remember { resolveAnimeRepository() }
    val scope = rememberCoroutineScope()
    var state by remember(repositoryResolution) {
        mutableStateOf(repositoryResolution.initialState)
    }
    var imageState by remember { mutableStateOf<WebImageProbeState>(WebImageProbeState.Inactive) }
    val runner = (repositoryResolution as? WebRepositoryResolution.Available)?.repository?.let { repository ->
        remember(repository, scope) {
            WebNetworkDiagnosticRunner(repository, scope) { state = it }
        }
    }
    val coverUrl = (state as? WebNetworkDiagnosticState.Success)?.coverUrl

    DisposableEffect(coverUrl) {
        val disposeImageProbe = probeWebImage(coverUrl) { imageState = it }

        onDispose {
            disposeImageProbe?.invoke()
        }
    }

    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppDimensions.padding.lg),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacing.md),
        ) {
            Text(
                text = "Diagnóstico de rede Web",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "Modo temporário da W1.3. Não faz parte da navegação do aplicativo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = state.toDisplayText(),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = imageState.toDisplayText(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            AppButton(
                text = "Executar diagnóstico",
                onClick = { runner?.start() },
                enabled = runner != null,
                loading = state is WebNetworkDiagnosticState.Loading,
            )
            if (state is WebNetworkDiagnosticState.Loading) {
                AppButton(
                    text = "Cancelar",
                    onClick = { runner?.cancel() },
                )
            }
        }
    }
}

internal expect fun webLocationSearch(): String

internal expect fun probeWebImage(
    url: String?,
    onStateChanged: (WebImageProbeState) -> Unit,
): (() -> Unit)?

private sealed interface WebRepositoryResolution {
    val initialState: WebNetworkDiagnosticState

    data class Available(
        val repository: AnimeRepository,
    ) : WebRepositoryResolution {
        override val initialState = WebNetworkDiagnosticState.Inactive
    }

    data class Unavailable(
        val cause: Throwable,
    ) : WebRepositoryResolution {
        override val initialState = WebNetworkDiagnosticState.Failure(
            category = "repository indisponível",
            phase = WebDiagnosticPhase.REPOSITORY_RESOLUTION,
            technicalDetails = cause.toWebDiagnosticTechnicalDetails(),
        )
    }
}

private fun resolveAnimeRepository(): WebRepositoryResolution =
    try {
        WebRepositoryResolution.Available(
            KoinPlatformTools.defaultContext().get().get<AnimeRepository>(),
        )
    } catch (cause: Throwable) {
        WebRepositoryResolution.Unavailable(cause)
    }

internal fun Throwable?.toWebDiagnosticTechnicalDetails(): WebDiagnosticTechnicalDetails {
    if (this == null) {
        return WebDiagnosticTechnicalDetails(
            causes = emptyList(),
            chainTruncated = false,
            cycleDetected = false,
        )
    }

    val causes = mutableListOf<WebDiagnosticCause>()
    val seen = mutableListOf<Throwable>()
    var current: Throwable? = this
    var cycleDetected = false

    while (current != null && causes.size < MAX_DIAGNOSTIC_CAUSES) {
        if (seen.any { it === current }) {
            cycleDetected = true
            break
        }

        seen += current
        causes += WebDiagnosticCause(
            className = current::class.simpleName ?: "Throwable",
            message = current.message.toSanitizedDiagnosticMessage(),
        )
        current = current.cause
    }

    return WebDiagnosticTechnicalDetails(
        causes = causes,
        chainTruncated = current != null && !cycleDetected,
        cycleDetected = cycleDetected,
    )
}

internal fun String?.toSanitizedDiagnosticMessage(): String {
    val normalized = this.orEmpty().replace(WHITESPACE_PATTERN, " ").trim()

    if (normalized.isEmpty()) return "mensagem técnica indisponível"
    if (GRAPHQL_BODY_PATTERN.containsMatchIn(normalized)) return "conteúdo GraphQL omitido"

    val redacted = normalized
        .replace(BEARER_PATTERN, "Bearer [redigido]")
        .replace(SENSITIVE_VALUE_PATTERN, "$1=[redigido]")

    return if (redacted.length <= MAX_DIAGNOSTIC_MESSAGE_LENGTH) {
        redacted
    } else {
        redacted.take(MAX_DIAGNOSTIC_MESSAGE_LENGTH).trimEnd() + "…"
    }
}

private fun WebNetworkDiagnosticState.toDisplayText(): String =
    when (this) {
        WebNetworkDiagnosticState.Inactive -> "Diagnóstico inativo."
        WebNetworkDiagnosticState.Loading -> "Consultando o catálogo."
        WebNetworkDiagnosticState.Cancelled -> "Consulta cancelada."
        is WebNetworkDiagnosticState.Failure -> buildString {
            append("Diagnóstico falhou: $category. Fase: ${phase.displayName}. ")
            append(technicalDetails.toDisplayText())
        }
        is WebNetworkDiagnosticState.Success ->
            "Sucesso: $itemCount itens; primeiro ID: ${firstId ?: "indisponível"}; título: ${firstTitle ?: "indisponível"}."
    }

private fun WebImageProbeState.toDisplayText(): String =
    when (this) {
        WebImageProbeState.Inactive -> "Imagem: aguardando uma capa retornada pela AniList."
        WebImageProbeState.Loading -> "Imagem: carregando capa remota."
        WebImageProbeState.Unavailable ->
            "Imagem: indisponível. Fase: ${WebDiagnosticPhase.IMAGE_PROBE.displayName}; causa técnica indisponível."
        is WebImageProbeState.Loaded -> "Imagem carregada: ${width} × ${height}."
    }

private fun WebDiagnosticTechnicalDetails.toDisplayText(): String =
    if (causes.isEmpty()) {
        "Causa técnica indisponível."
    } else {
        buildString {
            append("Causa: ")
            append(causes.joinToString(" → ") { cause -> "${cause.className}: ${cause.message}" })
            if (chainTruncated) append(" → cadeia limitada.")
            if (cycleDetected) append(" → ciclo interrompido.")
        }
    }

private const val MAX_DIAGNOSTIC_CAUSES = 3
private const val MAX_DIAGNOSTIC_MESSAGE_LENGTH = 180
private val WHITESPACE_PATTERN = Regex("\\s+")
private val BEARER_PATTERN = Regex("bearer\\s+[^\\s,;]+", RegexOption.IGNORE_CASE)
private val SENSITIVE_VALUE_PATTERN = Regex(
    "\\b(authorization|cookie|set-cookie|token|access[_-]?token)\\b\\s*[:=]\\s*[^\\s,;]+",
    RegexOption.IGNORE_CASE,
)
private val GRAPHQL_BODY_PATTERN = Regex(
    "(?:\\{|\\[)\\s*\"?(query|variables)\"?\\s*:",
    RegexOption.IGNORE_CASE,
)
