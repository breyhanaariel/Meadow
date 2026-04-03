package com.meadow.feature.script.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

import com.meadow.core.ui.events.UiMessage
import com.meadow.feature.script.R
import com.meadow.feature.script.domain.model.ScriptDialect
import com.meadow.feature.script.domain.model.ScriptType
import com.meadow.feature.script.domain.repository.ScriptRepositoryContract
import com.meadow.feature.script.ui.navigation.ScriptRoutes
import com.meadow.feature.script.ui.state.ScriptListItem
import com.meadow.feature.script.ui.state.ScriptListUiState

@HiltViewModel
class ScriptListViewModel @Inject constructor(
    private val repo: ScriptRepositoryContract
) : ViewModel() {

    private val uiMessagesFlow = MutableSharedFlow<UiMessage>()
    val uiMessages = uiMessagesFlow.asSharedFlow()

    private val navRoutesFlow = MutableSharedFlow<String>()
    val navRoutes = navRoutesFlow.asSharedFlow()

    private val stateFlow = MutableStateFlow(ScriptListUiState())
    val uiState: StateFlow<ScriptListUiState> = stateFlow.asStateFlow()
    private var currentProjectId: String? = null
    fun bind(
        projectId: String
    ) {
        currentProjectId = projectId
        observeScripts(projectId)
    }

    private fun observeScripts(
        projectId: String
    ) {
        viewModelScope.launch {
            stateFlow.value = stateFlow.value.copy(isLoading = true)
            repo.observeScriptsByProject(projectId)
                .map { scripts ->
                    ScriptListUiState(
                        isLoading = false,
                        items = scripts.map { s ->
                            ScriptListItem(
                                scriptId = s.scriptId,
                                type = s.type,
                                dialect = s.dialect,
                                updatedAt = s.updatedAt.toEpochMilli()
                            )
                        }
                    )
                }
                .collectLatest { newState ->
                    stateFlow.value = newState
                }
        }
    }

    fun createDefaultScript(
        projectId: String
    ) {
        viewModelScope.launch {
            runCatching {
                val scriptId = repo.createScriptForProject(
                    projectId = projectId,
                    type = ScriptType.NOVEL,
                    dialect = ScriptDialect.FOUNTAIN,
                    canonicalLanguage = "en"
                )
                val variantId = repo.ensureCanonicalVariant(scriptId = scriptId, language = "en")
                navRoutesFlow.emit(ScriptRoutes.editor(projectId, scriptId, variantId))
            }.onFailure {
                uiMessagesFlow.emit(UiMessage.Snackbar(R.string.script_create_failed))
            }
        }
    }

    fun openScript(
        scriptId: String,
        language: String = "en"
    ) {
        viewModelScope.launch {
            val projectId = currentProjectId ?: return@launch
            val variantId = repo.ensureCanonicalVariant(scriptId = scriptId, language = language)
            navRoutesFlow.emit(ScriptRoutes.editor(projectId, scriptId, variantId))
        }
    }
}