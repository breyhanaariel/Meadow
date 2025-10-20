package com.meadow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.data.repo.MeadowRepository
import com.meadow.app.data.room.entities.CatalogItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class CatalogViewModel @Inject constructor(private val repo: MeadowRepository) : ViewModel() {
    private val _items = MutableStateFlow<List<CatalogItemEntity>>(emptyList())
    val items: StateFlow<List<CatalogItemEntity>> = _items

    init {
        viewModelScope.launch {
            repo.getCatalog(null).collect { _items.value = it }
        }
    }

    fun search(query: String, type: String?) = viewModelScope.launch {
        val all = repo.getCatalog(null).first()
        val filtered = all.filter {
            (type == null || it.type == type) &&
                    (query.isBlank() || it.title.contains(query, true) || it.description.contains(query, true) || it.tagsCsv.contains(query, true))
        }
        _items.value = filtered
    }
}
