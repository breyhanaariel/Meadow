package com.meadow.feature.calendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.feature.calendar.data.local.CalendarDao
import com.meadow.feature.calendar.data.mappers.CalendarEventMapper
import com.meadow.feature.calendar.domain.model.CalendarEvent
import com.meadow.feature.calendar.domain.model.CalendarScope
import com.meadow.feature.calendar.domain.repository.CalendarRepositoryContract
import com.meadow.feature.project.api.ProjectSelector
import com.meadow.feature.project.api.ProjectSelectorItem
import com.meadow.feature.project.api.SeriesSelector
import com.meadow.feature.project.api.SeriesSelectorItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CalendarEventViewModel @Inject constructor(
    private val repo: CalendarRepositoryContract,
    private val dao: CalendarDao,
    projectSelector: ProjectSelector,
    private val seriesSelector: SeriesSelector
) : ViewModel() {

    enum class RecurrencePreset { NONE, DAILY, WEEKLY, MONTHLY }

    data class UiState(
        val localId: String? = null,
        val title: String = "",
        val description: String = "",
        val location: String = "",
        val startAtMillis: Long = System.currentTimeMillis(),
        val endAtMillis: Long = System.currentTimeMillis() + 60 * 60 * 1000,
        val allDay: Boolean = false,
        val projectId: String? = null,
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun load(localId: String?, initialProjectId: String?) {
        if (localId == null) {
            if (initialProjectId != null) {
                _state.update { it.copy(projectId = initialProjectId) }
            }
            return
        }

        viewModelScope.launch {
            val existing = dao.getByLocalId(localId) ?: return@launch
            val d = CalendarEventMapper.toDomain(existing)

            _state.value = _state.value.copy(
                localId = d.localId,
                title = d.title,
                description = d.description.orEmpty(),
                location = d.location.orEmpty(),
                startAtMillis = d.startAtMillis,
                endAtMillis = d.endAtMillis,
                allDay = d.allDay,
                 projectId = d.projectId
            )
        }
    }

    fun setTitle(v: String) {
        _state.update { it.copy(title = v) }
    }

    fun setLocation(v: String) {
        _state.update { it.copy(location = v) }
    }

    fun setStartMillis(v: Long) {
        _state.update { it.copy(startAtMillis = v) }
    }

    fun setEndMillis(v: Long) {
        _state.update { it.copy(endAtMillis = v) }
    }

    fun setAllDay(v: Boolean) {
        _state.update { it.copy(allDay = v) }
    }

    fun extendEndByMinutes(minutes: Int) {
        _state.update {
            it.copy(endAtMillis = it.endAtMillis + minutes * 60_000)
        }
    }

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {

            val s = _state.value

            if (s.title.isBlank()) {
                _state.update { it.copy(error = "Title is required.") }
                return@launch
            }

            if (s.endAtMillis <= s.startAtMillis) {
                _state.update { it.copy(error = "End must be after start.") }
                return@launch
            }

            val id = s.localId ?: UUID.randomUUID().toString()

            val event = CalendarEvent(
                localId = id,
                googleEventId = null,
                googleCalendarId = "primary",
                title = s.title,
                description = s.description.takeIf { it.isNotBlank() },
                location = s.location.takeIf { it.isNotBlank() },
                startAtMillis = s.startAtMillis,
                endAtMillis = s.endAtMillis,
                allDay = s.allDay,
                timeZone = ZoneId.systemDefault().id,
                colorId = null,
                recurrence = null,
                reminderMinutes = null,
                scope = if (s.projectId != null)
                    CalendarScope.PROJECT
                else
                    CalendarScope.GLOBAL,
                projectId = s.projectId,
                seriesId = null,
                isDeleted = false,
                localUpdatedAt = System.currentTimeMillis(),
                remoteUpdatedAt = null,
                etag = null,
                isDirty = true,
                hasConflict = false,
                lastSyncError = null
            )

            repo.createOrUpdateEvent(event)
            onSuccess()
        }
    }

    fun delete(localId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repo.markDeleted(localId)
            onSuccess()
        }
    }
}