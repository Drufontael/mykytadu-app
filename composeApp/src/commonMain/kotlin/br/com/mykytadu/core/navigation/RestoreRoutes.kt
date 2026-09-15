package br.com.mykytadu.core.navigation

import androidx.navigation3.runtime.NavKey

/** Keep retained entries in place so their ViewModelStore and saved state stay owned. */
internal fun MutableList<NavKey>.restoreRoutes(routes: List<AppRoute>) {
    require(routes.isNotEmpty()) { "Navigation requires a root route." }
    val commonSize = zip(routes).takeWhile { (current, restored) -> current == restored }.size
    while (size > commonSize) removeAt(lastIndex)
    addAll(routes.drop(commonSize))
}
