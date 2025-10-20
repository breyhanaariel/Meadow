package com.meadow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.domain.model.TimelineEvent
import com.meadow.app.data.repository.TimelineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimelineViewModel(
    private val repository: TimelineRepository
) : ViewModel() {

    private val _timelineEvents = MutableStateFlow<List<TimelineEvent>>(emptyList())
    val timelineEvents = _timelineEvents.asStateFlow()

    init { loadEvents() }

    fun loadEvents() {
        viewModelScope.launch { _timelineEvents.value = repository.getAll() }
    }

    fun addEvent(title: String, description: String, date: String) {
        viewModelScope.launch {
            repository.insert(TimelineEvent(title = title, description = description, date = date))
            loadEvents()
        }
    }

    fun editEvent(event: TimelineEvent) {
        viewModelScope.launch {
            repository.update(event)
            loadEvents()
        }
    }

    fun deleteEvent(id: String) {
        viewModelScope.launch {
            repository.delete(id)
            loadEvents()
        }
    }
}