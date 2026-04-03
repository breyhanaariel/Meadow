package com.meadow.feature.calendar.ui.navigation

object CalendarRoutes {

    const val ArgLocalId = "localId"
    const val ArgProjectId = "projectId"

    const val CALENDAR_HOME = "calendar/home"
    const val CALENDAR_HOME_PROJECT = "calendar/home/project/{$ArgProjectId}"

    const val CALENDAR_EDIT = "calendar/edit/{$ArgLocalId}"
    const val CALENDAR_CREATE = "calendar/create?$ArgProjectId={$ArgProjectId}"

    fun projectCalendar(projectId: String) = "calendar/home/project/$projectId"

    fun editRoute(localId: String) = "calendar/edit/$localId"

    fun createRoute(projectId: String?): String =
        if (projectId.isNullOrBlank()) "calendar/create"
        else "calendar/create?$ArgProjectId=$projectId"
}
