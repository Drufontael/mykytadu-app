package br.com.mykytadu.web

internal enum class WebEntryMode {
    NORMAL,
    NETWORK_SMOKE,
    DESIGN_SYSTEM_SHOWCASE,
}

internal fun resolveWebEntryMode(search: String): WebEntryMode {
    val parameters = search.removePrefix("?").split('&')
        .asSequence()
        .mapNotNull { parameter ->
            val separator = parameter.indexOf('=')
            if (separator < 0) null
            else parameter.substring(0, separator) to parameter.substring(separator + 1)
        }

    val exactTrue = parameters.filter { (_, value) -> value == "true" }.map { (key, _) -> key }.toSet()
    return when {
        "network-smoke" in exactTrue -> WebEntryMode.NETWORK_SMOKE
        "design-system-showcase" in exactTrue -> WebEntryMode.DESIGN_SYSTEM_SHOWCASE
        else -> WebEntryMode.NORMAL
    }
}
