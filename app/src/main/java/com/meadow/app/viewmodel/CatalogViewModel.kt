package com.meadow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.data.repository.CatalogRepository
import com.meadow.app.data.room.entities.CatalogItemEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * CatalogViewModel.kt
 *
 * Manages catalog items (characters, props, locations, etc.)
 */
@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val catalogRepo: CatalogRepository
) : ViewModel() {

    private val _catalogItems = MutableStateFlow<List<CatalogItemEntity>>(emptyList())
    val catalogItems: StateFlow<List<CatalogItemEntity>> = _catalogItems.asStateFlow()

    fun loadCatalog(projectId: String) {
        viewModelScope.launch {
            catalogRepo.getCatalogForProject(projectId).collect {
                _catalogItems.value = it
            }
        }
    }

    fun saveItem(projectId: String, type: String, name: String, payloadJson: String?) {
        viewModelScope.launch {
            val item = CatalogItemEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                type = type,
                name = name,
                payloadJson = payloadJson
            )
            catalogRepo.saveItem(item)
        }
    }

    fun deleteItem(item: CatalogItemEntity) {
        viewModelScope.launch {
            catalogRepo.deleteItem(item)
        }
    }
}