package com.meadow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.domain.model.WikiEntry
import com.meadow.app.data.repository.WikiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WikiViewModel(
    private val repository: WikiRepository
) : ViewModel() {

    private val _wikiEntries = MutableStateFlow<List<WikiEntry>>(emptyList())
    val wikiEntries = _wikiEntries.asStateFlow()

    private val _selectedEntry = MutableStateFlow<WikiEntry?>(null)
    val selectedEntry = _selectedEntry.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    init {
        loadEntries()
    }

    fun loadEntries() {
        viewModelScope.launch { _wikiEntries.value = repository.getAll() }
    }

    fun editEntry(entry: WikiEntry) {
        _selectedEntry.value = entry
    }

    fun addEntry(title: String, content: String) {
        viewModelScope.launch {
            _isSaving.value = true
            repository.insert(WikiEntry(title = title, content = content))
            loadEntries()
            _isSaving.value = false
        }
    }

    fun updateEntry(entry: WikiEntry) {
        viewModelScope.launch {
            _isSaving.value = true
            repository.update(entry)
            loadEntries()
            _isSaving.value = false
        }
    }

    fun deleteEntry(id: String) {
        viewModelScope.launch {
            repository.delete(id)
            loadEntries()
        }
    }
}