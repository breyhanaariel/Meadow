package com.meadow.feature.catalog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.core.ui.events.UiMessage
import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.meadow.core.ui.R as CoreUiR

data class CatalogItemUiState(
    val isLoading: Boolean = true,
    val itemId: String? = null,
    val title: String? = null,
    val item: CatalogItem? = null,
    val fields: List<com.meadow.core.data.fields.FieldWithValue> = emptyList()
)

@HiltViewModel
class CatalogItemViewModel @Inject constructor(
    private val repo: CatalogRepositoryContract
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogItemUiState())
    val uiState: StateFlow<CatalogItemUiState> = _uiState.asStateFlow()

    private val _uiMessages = MutableSharedFlow<UiMessage>(replay = 0, extraBufferCapacity = 1)
    val uiMessages = _uiMessages.asSharedFlow()

    private val _navigateBack = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateBack = _navigateBack.asSharedFlow()

    private var currentItem: CatalogItem? = null
    private var observingJobStartedFor: String? = null

    fun load(itemId: String) {
        if (observingJobStartedFor == itemId) return
        observingJobStartedFor = itemId

        _uiState.value = CatalogItemUiState(isLoading = true)

        viewModelScope.launch {
            repo.observeCatalogItem(itemId).collect { item ->
                currentItem = item

                if (item == null) {
                    _uiState.value = CatalogItemUiState(
                        isLoading = false,
                        itemId = null,
                        title = null,
                        item = null,
                        fields = emptyList()
                    )
                    _uiMessages.tryEmit(UiMessage.Snackbar(CoreUiR.string.load_failed))
                    return@collect
                }

                _uiState.value = CatalogItemUiState(
                    isLoading = false,
                    itemId = item.id,
                    title = item.primaryText?.takeIf { it.isNotBlank() } ?: item.id,
                    item = item,
                    fields = item.fields
                )
            }
        }
    }

    fun delete() {
        val item = currentItem ?: return

        viewModelScope.launch {
            try {
                repo.deleteCatalogItem(item.id)
                _uiMessages.emit(UiMessage.Snackbar(CoreUiR.string.delete_success))
                _navigateBack.emit(Unit)
            } catch (e: Exception) {
                _uiMessages.emit(UiMessage.Snackbar(CoreUiR.string.delete_failed))
            }
        }
    }


}
