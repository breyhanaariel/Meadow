package com.meadow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.domain.model.MindMapNode
import com.meadow.app.data.repository.MindMapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MindMapViewModel(
    private val repository: MindMapRepository
) : ViewModel() {

    private val _nodes = MutableStateFlow<List<MindMapNode>>(emptyList())
    val nodes = _nodes.asStateFlow()

    init { loadNodes() }

    fun loadNodes() {
        viewModelScope.launch { _nodes.value = repository.getAll() }
    }

    fun addNode(title: String) {
        viewModelScope.launch {
            repository.insert(MindMapNode(title = title))
            loadNodes()
        }
    }

    fun linkNodes(fromId: String, toId: String) {
        viewModelScope.launch {
            repository.linkNodes(fromId, toId)
            loadNodes()
        }
    }

    fun deleteNode(id: String) {
        viewModelScope.launch {
            repository.delete(id)
            loadNodes()
        }
    }
}