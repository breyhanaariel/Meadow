package com.meadow.feature.project.ui.state

import androidx.annotation.StringRes
import com.meadow.core.ui.R as CoreUiR
import com.meadow.feature.project.R as R
import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.model.Series

/* ─── ENUMS ───────────────────── */
enum class ListSearchMode { PROJECTS, SERIES, BOTH }
enum class ListViewMode { LIST, GRID }
enum class SeriesListSort(@StringRes val labelRes: Int) {
    TITLE_ASC(CoreUiR.string.sort_title_asc),
    TITLE_DESC(CoreUiR.string.sort_title_desc),
    PROJECT_COUNT_ASC(R.string.sort_project_count_asc),
    PROJECT_COUNT_DESC(R.string.sort_project_count_desc);
}
enum class ProjectListSort(@StringRes val labelRes: Int) {
    TITLE_ASC(CoreUiR.string.sort_title_asc),
    TITLE_DESC(CoreUiR.string.sort_title_desc),
    CREATED_ASC(CoreUiR.string.sort_created_asc),
    CREATED_DESC(CoreUiR.string.sort_created_desc),
    UPDATED_ASC(CoreUiR.string.sort_updated_asc),
    UPDATED_DESC(CoreUiR.string.sort_updated_desc);
}
/* ─── DATAS ───────────────────── */
data class ListUiState(
    val isLoading: Boolean = false,

    /* raw data */
    val allProjects: List<Project> = emptyList(),
    val allSeries: List<Series> = emptyList(),

    /* merged list */
    val items: List<ListItem> = emptyList(),

    /* search */
    val searchQuery: String = "",

    /* view */
    val viewMode: ListViewMode = ListViewMode.GRID,

    /* mode */
    val searchMode: ListSearchMode = ListSearchMode.BOTH,

    /* sorting */
    val projectSort: ProjectListSort = ProjectListSort.UPDATED_DESC,
    val seriesSort: SeriesListSort = SeriesListSort.TITLE_ASC,

    /* filters */
    val projectFilters: ProjectListFilters = ProjectListFilters(),
    val filterOptions: ProjectListFilterOptions = ProjectListFilterOptions(),

    /* visibility */
    val visibility: ProjectListVisibility = ProjectListVisibility(),

    /* pending operations */
    val pendingDeleteProject: Project? = null,
    val pendingDeleteSeries: Series? = null,
    val pendingRenameSeries: Series? = null,

    /* series editing */
    val editingSeries: Series? = null,
    val editingSeriesAiContext: Series? = null,
    val projectsInSeries: List<Project> = emptyList(),
    val availableProjects: List<Project> = emptyList(),
    val projectSearchQuery: String = "",

    /* sync */
    val globalSync: ProjectSyncUiState = ProjectSyncUiState(),
    val syncByProjectId: Map<String, ProjectSyncUiState> = emptyMap(),
    val errorMessage: String? = null
)
data class ProjectListVisibility(
    val showArchived: Boolean = false,
    val showCompleted: Boolean = false
)
data class ProjectListFilters(
    val projectTypes: Set<String> = emptySet(),
    val audiences: Set<String> = emptySet(),
    val genres: Set<String> = emptySet(),
    val elements: Set<String> = emptySet(),
    val ratings: Set<String> = emptySet(),
    val warnings: Set<String> = emptySet(),
    val statuses: Set<String> = emptySet(),
    val formats: Set<String> = emptySet()
)
data class ProjectListFilterState(
    val query: String = "",
    val filters: ProjectListFilters = ProjectListFilters(),
)
fun ProjectListFilterState.isActive(): Boolean =
    query.isNotBlank() || filters != ProjectListFilters()

fun List<com.meadow.core.data.fields.FieldDefinition>.optionsFor(
    vararg keysOrIds: String
): List<String> {
    val keySet = keysOrIds.toSet()
    return firstOrNull { def ->
        def.id in keySet || def.key in keySet
    }?.metadata
        ?.get("options")
        ?.let { it as? List<*> }
        ?.mapNotNull { it?.toString() }
        ?.filter { it.isNotBlank() }
        ?: emptyList()
}
data class ProjectListFilterOptions(
    val projectTypes: List<String> = emptyList(),
    val audiences: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val elements: List<String> = emptyList(),
    val ratings: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val statuses: List<String> = emptyList(),
    val formats: List<String> = emptyList()
)
sealed class ListItem {
    data class ProjectItem(
        val project: Project
    ) : ListItem()
    data class SeriesItem(
        val series: Series
    ) : ListItem()
}