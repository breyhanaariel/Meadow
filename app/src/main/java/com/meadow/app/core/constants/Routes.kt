package com.meadow.app.core.constants

/**
 * Routes.kt
 *
 * This file lists all navigation route names used in NavHost.
 * It’s better to centralize routes instead of typing strings directly in the navigation graph.
 */

object Routes {
    // Core routes
    const val LOADING = "loading"
    const val HOME = "home"
    const val SETTINGS = "settings"

    // Project-based routes
    const val PROJECT_DASHBOARD = "project/{projectId}"
    const val SCRIPT_EDITOR = "project/{projectId}/script/{scriptId}"
    const val CATALOG = "project/{projectId}/catalog"
    const val WIKI = "project/{projectId}/wiki"
    const val TIMELINE = "project/{projectId}/timeline"
    const val STORYBOARD = "project/{projectId}/storyboard"
    const val MINDMAP = "project/{projectId}/mindmap"
    const val FAMILY_TREE = "project/{projectId}/familytree"
    const val CALENDAR = "project/{projectId}/calendar"
}
