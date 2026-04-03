package com.meadow.feature.project.ui.navigation

object ProjectRoutes {
    const val PROJECT_LIST = "project_list"
    fun projectList(): String = PROJECT_LIST

    const val PROJECT_DASHBOARD = "project_dashboard/{projectId}"
    fun projectDashboard(projectId: String) = "project_dashboard/$projectId"

    const val PROJECT_CREATE = "project_create"
    fun projectCreate(): String = PROJECT_CREATE

    const val PROJECT_EDIT = "project_edit/{projectId}"
    fun projectEdit(projectId: String): String =
        "project_edit/$projectId"
    const val PROJECT_HISTORY = "project_history/{projectId}"
    fun projectHistory(projectId: String) = "project_history/$projectId"

    const val SERIES_LIST = "series_list"
    fun seriesList(): String = SERIES_LIST

    const val ArgProjectId = "projectId"
    const val ArgSeriesId = "seriesId"


}
