package com.meadow.feature.common.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.core.domain.model.HistoryEntry
import com.meadow.core.domain.model.HistoryOwnerType
import com.meadow.core.domain.repository.HistoryRepositoryContract
import com.meadow.feature.common.api.HistoryLabelResolver
import com.meadow.feature.common.api.HistoryRestoreHandler
import com.meadow.feature.common.state.HistoryEntryUi
import com.meadow.feature.common.state.HistorySessionUi
import com.meadow.feature.common.state.HistoryUiState
import com.meadow.feature.common.ui.navigation.HistoryNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val historyRepository: HistoryRepositoryContract,
    private val labelResolvers: Set<@JvmSuppressWildcards HistoryLabelResolver>,
    private val restoreHandlers: Set<@JvmSuppressWildcards HistoryRestoreHandler>
) : ViewModel() {

    private val ownerId: String =
        requireNotNull(savedStateHandle[HistoryNavigation.OWNER_ID_ARG])

    private val ownerType: HistoryOwnerType =
        HistoryOwnerType.valueOf(
            requireNotNull(savedStateHandle[HistoryNavigation.OWNER_TYPE_ARG])
        )

    private val labelResolver: HistoryLabelResolver? =
        labelResolvers.firstOrNull { it.supports(ownerType) }

    private val restoreHandler: HistoryRestoreHandler? =
        restoreHandlers.firstOrNull { it.supports(ownerType) }

    private var cachedEntries: Map<String, HistoryEntry> = emptyMap()

    val uiState: StateFlow<HistoryUiState> =
        historyRepository.observeHistory(ownerId, ownerType)
            .map { entries ->
                cachedEntries = entries.associateBy { it.id }

                HistoryUiState(
                    isLoading = false,
                    sessions = entries
                        .sortedByDescending { it.timestamp }
                        .groupBy { it.sessionId }
                        .map { (sessionId, sessionEntries) ->
                            HistorySessionUi(
                                sessionId = sessionId,
                                timestamp = sessionEntries.maxOf { it.timestamp },
                                entries = sessionEntries
                                    .sortedBy { it.fieldId }
                                    .map { it.toUi() }
                            )
                        }
                        .sortedByDescending { it.timestamp }
                )
            }
            .onStart {
                emit(HistoryUiState(isLoading = true))
            }
            .catch { throwable ->
                emit(
                    HistoryUiState(
                        isLoading = false,
                        error = throwable.message
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HistoryUiState(isLoading = true)
            )

    fun restore(entryId: String) {
        val handler = restoreHandler ?: return
        val entry = cachedEntries[entryId] ?: return

        viewModelScope.launch {
            handler.restore(entry)
        }
    }

    private fun HistoryEntry.toUi(): HistoryEntryUi =
        HistoryEntryUi(
            id = id,
            fieldId = fieldId,
            label = labelResolver?.resolve(this) ?: fallbackLabel(fieldId),
            oldValue = oldValue,
            newValue = newValue,
            timestamp = timestamp,
            canRestore = restoreHandler != null
        )

    private fun fallbackLabel(fieldId: String): String =
        fieldId.substringAfterLast(".")
            .replace("_", " ")
            .replaceFirstChar { it.uppercase() }
}