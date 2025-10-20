package com.meadow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.domain.model.FamilyNode
import com.meadow.app.data.repository.FamilyTreeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FamilyTreeViewModel(
    private val repository: FamilyTreeRepository
) : ViewModel() {

    private val _tree = MutableStateFlow<List<FamilyNode>>(emptyList())
    val tree = _tree.asStateFlow()

    private val _selectedNode = MutableStateFlow<FamilyNode?>(null)
    val selectedNode = _selectedNode.asStateFlow()

    init { loadTree() }

    fun loadTree() {
        viewModelScope.launch { _tree.value = repository.getAll() }
    }

    fun addNode(name: String, parentId: String?) {
        viewModelScope.launch {
            repository.insert(FamilyNode(name = name, parentId = parentId))
            loadTree()
        }
    }

    fun editNode(node: FamilyNode) { _selectedNode.value = node }

    fun updateNode(node: FamilyNode) {
        viewModelScope.launch {
            repository.update(node)
            loadTree()
        }
    }

    fun deleteNode(id: String) {
        viewModelScope.launch {
            repository.delete(id)
            loadTree()
        }
    }
}