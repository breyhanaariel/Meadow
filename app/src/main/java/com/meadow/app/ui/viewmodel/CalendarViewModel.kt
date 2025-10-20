package com.meadow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.data.repository.CalendarRepository
import com.meadow.app.data.room.entities.CalendarEventEntity
import com.meadow.app.sync.CalendarSyncHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repo: CalendarRepository,
    private val calendarSync: CalendarSyncHelper
) : ViewModel() {

    fun getEvents(projectId: String): Flow<List<CalendarEventEntity>> =
        repo.getEventsForProject(projectId)

    fun addEvent(projectId: String, title: String, desc: String, start: Long) {
        viewModelScope.launch {
            val event = CalendarEventEntity(
                projectId = projectId,
                title = title,
                description = desc,
                startTime = start,
                endTime = start + 3600000
            )
            repo.addEvent(event)
            try {
                calendarSync.init()
                calendarSync.addDeadlineEvent(title, desc, start, start + 3600000)
            } catch (_: Exception) {}
        }
    }

    fun formatDate(timestamp: Long): String =
        SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(Date(timestamp))
}
