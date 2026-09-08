package br.com.mykytadu.domain.model

/**
 * Componentes independentes, sem completar ou corrigir datas.
 * Valida somente os intervalos de mês e dia; não valida o calendário nem limita o ano.
 */
data class PartialDate(
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null,
) {
    init {
        require(month == null || month in 1..12) { "Month must be between 1 and 12 when present." }
        require(day == null || day in 1..31) { "Day must be between 1 and 31 when present." }
    }
}
