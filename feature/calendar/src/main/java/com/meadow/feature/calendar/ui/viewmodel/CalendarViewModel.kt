package com.meadow.feature.calendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.feature.calendar.domain.model.CalendarEvent
import com.meadow.feature.calendar.domain.model.CalendarScope
import com.meadow.feature.calendar.domain.repository.CalendarRepositoryContract
import com.meadow.feature.calendar.domain.usecase.SyncCalendarUseCase
import com.meadow.feature.project.api.ProjectSelector
import com.meadow.feature.project.api.ProjectSelectorItem
import com.meadow.feature.project.api.SeriesSelector
import com.meadow.feature.project.api.SeriesSelectorItem
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.*
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repo: CalendarRepositoryContract,
    private val syncCalendar: SyncCalendarUseCase,
    projectSelector: ProjectSelector,
    seriesSelector: SeriesSelector
) : ViewModel() {

    enum class CalendarViewMode { MONTH, WEEK, AGENDA }
    enum class CalendarFilter { ALL, GLOBAL, SERIES, PROJECTS, THIS_PROJECT }

    data class UiState(
        val viewMode: CalendarViewMode = CalendarViewMode.MONTH,
        val filter: CalendarFilter = CalendarFilter.ALL,
        val projectContextId: String? = null,
        val selectedProjectId: String? = null,
        val selectedSeriesId: String? = null,
        val selectedDate: LocalDate = LocalDate.now(),
        val isSyncing: Boolean = false,
        val events: List<CalendarEvent> = emptyList(),
        val projects: List<ProjectSelectorItem> = emptyList(),
        val series: List<SeriesSelectorItem> = emptyList(),
        val error: String? = null,
        val visibleMonth: YearMonth = YearMonth.now(),
        val eventsByDay: Map<LocalDate, List<CalendarEvent>> = emptyMap()
    )

    private val viewModeFlow = MutableStateFlow(CalendarViewMode.MONTH)
    private val filterFlow = MutableStateFlow(CalendarFilter.ALL)
    private val projectContextFlow = MutableStateFlow<String?>(null)
    private val projectIdFlow = MutableStateFlow<String?>(null)
    private val seriesIdFlow = MutableStateFlow<String?>(null)
    private val selectedDateFlow = MutableStateFlow(LocalDate.now())
    private val syncingFlow = MutableStateFlow(false)
    private val errorFlow = MutableStateFlow<String?>(null)

    private val projectsFlow =
        projectSelector.observeAvailableProjects()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val seriesFlow =
        projectIdFlow.flatMapLatest { pid ->
            seriesSelector.observeAvailableSeries(projectId = pid)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val eventsFlow: Flow<List<CalendarEvent>> =
        selectedDateFlow.flatMapLatest { date ->
            val start = date.minusMonths(2).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val end = date.plusMonths(4).plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            repo.observeEventsBetween(start, end)
        }

    private val filteredEvents =
        combine(eventsFlow, filterFlow, projectContextFlow, projectIdFlow, seriesIdFlow) { events, filter, projectCtx, pid, sid ->
            when (filter) {
                CalendarFilter.ALL -> events
                CalendarFilter.GLOBAL -> events.filter { it.scope == CalendarScope.GLOBAL }
                CalendarFilter.SERIES -> {
                    events.filter { it.seriesId != null && (sid == null || it.seriesId == sid) }
                }
                CalendarFilter.PROJECTS -> {
                    events.filter { it.projectId != null && (pid == null || it.projectId == pid) }
                }
                CalendarFilter.THIS_PROJECT -> {
                    val target = projectCtx ?: return@combine emptyList()
                    events.filter { it.projectId == target }
                }
            }
        }

    val state: StateFlow<UiState> =
        combine(
            viewModeFlow,
            filterFlow,
            projectContextFlow,
            projectIdFlow,
            seriesIdFlow,
            selectedDateFlow,
            syncingFlow,
            filteredEvents,
            projectsFlow,
            seriesFlow,
            errorFlow
        ) { values ->

            val mode = values[0] as CalendarViewMode
            val filter = values[1] as CalendarFilter
            val projectCtx = values[2] as String?
            val pid = values[3] as String?
            val sid = values[4] as String?
            val date = values[5] as LocalDate
            val syncing = values[6] as Boolean
            val events = values[7] as List<CalendarEvent>
            val projects = values[8] as List<ProjectSelectorItem>
            val series = values[9] as List<SeriesSelectorItem>
            val err = values[10] as String?

            val visibleMonth = YearMonth.from(date)

            val eventsByDay =
                events.groupBy {
                    Instant.ofEpochMilli(it.startAtMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                }

            UiState(
                viewMode = mode,
                filter = filter,
                projectContextId = projectCtx,
                selectedProjectId = pid,
                selectedSeriesId = sid,
                selectedDate = date,
                isSyncing = syncing,
                events = events,
                projects = projects,
                series = series,
                error = err,
                visibleMonth = visibleMonth,
                eventsByDay = eventsByDay
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())
    fun setProjectContext(projectId: String?) {
        projectContextFlow.value = projectId
        if (projectId != null) {
            filterFlow.value = CalendarFilter.THIS_PROJECT
            projectIdFlow.value = projectId
        }
    }

    fun setViewMode(mode: CalendarViewMode) {
        viewModeFlow.value = mode
    }

    fun setFilter(filter: CalendarFilter) {
        filterFlow.value = filter
        when (filter) {
            CalendarFilter.ALL, CalendarFilter.GLOBAL -> {
                projectIdFlow.value = null
                seriesIdFlow.value = null
            }
            CalendarFilter.SERIES -> {
                projectIdFlow.value = null
            }
            CalendarFilter.PROJECTS -> {
                seriesIdFlow.value = null
            }
            CalendarFilter.THIS_PROJECT -> {
                val ctx = projectContextFlow.value
                projectIdFlow.value = ctx
                seriesIdFlow.value = null
            }
        }
    }

    fun setProject(id: String?) {
        projectIdFlow.value = id
    }

    fun setSeries(id: String?) {
        seriesIdFlow.value = id
    }

    fun setSelectedDate(date: LocalDate) {
        selectedDateFlow.value = date
    }

    fun goToToday() {
        selectedDateFlow.value = LocalDate.now()
    }

    fun deleteEvent(localId: String) {
        viewModelScope.launch {
            repo.deleteEvent(localId)
        }
    }
    fun syncNow() {
        if (syncingFlow.value) return
        viewModelScope.launch {
            syncingFlow.value = true
            errorFlow.value = null
            try {
                val ok = syncCalendar()
                if (!ok) {
                    errorFlow.value = "Calendar sync failed. Check event sync errors."
                }
            } catch (e: Exception) {
                errorFlow.value = e.message ?: e::class.java.simpleName
            } finally {
                syncingFlow.value = false
            }
        }
    }
}
