package com.meadow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.data.repository.CalendarRepository
import com.meadow.app.data.room.entities.CalendarEventEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * CalendarViewModel.kt
 *
 * Handles deadlines, reminders, and recurring events.
 */

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repo: CalendarRepository
) : ViewModel() {

    private val _events = MutableStateFlow<List<CalendarEventEntity>>(emptyList())
    val events: StateFlow<List<CalendarEventEntity>> = _events.asStateFlow()

    fun loadEvents(projectId: String) {
        viewModelScope.launch {
            repo.getEvents(projectId).collect { _events.value = it }
        }
    }

    fun addEvent(event: CalendarEventEntity) {
        viewModelScope.launch { repo.saveEvent(event) }
    }

    fun deleteEvent(event: CalendarEventEntity) {
        viewModelScope.launch { repo.deleteEvent(event) }
    }
}
