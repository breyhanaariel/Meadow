package com.meadow.feature.common.ui.navigation

import androidx.navigation.NavBackStackEntry
object HistoryNavigation {
    const val OWNER_TYPE_ARG = "ownerType"
    const val OWNER_ID_ARG = "ownerId"
    const val TITLE_ARG = "title"

    const val ROUTE =
        "history/{$OWNER_TYPE_ARG}/{$OWNER_ID_ARG}?title={$TITLE_ARG}"

    fun createRoute(
        ownerType: String,
        ownerId: String,
        title: String? = null
    ): String {
        val encodedTitle = title?.trim().orEmpty()
        return "history/$ownerType/$ownerId?title=$encodedTitle"
    }

    fun readTitle(backStackEntry: NavBackStackEntry): String =
        backStackEntry.arguments?.getString(TITLE_ARG)
            ?.takeIf { it.isNotBlank() }
            ?: "History"
}