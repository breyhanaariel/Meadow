package com.meadow.feature.script.ui.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.core.ui.events.UiMessage
import com.meadow.feature.script.R
import com.meadow.feature.script.domain.model.ScriptDialect
import com.meadow.feature.script.domain.parser.ParseOptions
import com.meadow.feature.script.domain.parser.ScriptParserDispatcher
import com.meadow.feature.script.ui.state.ScriptEditorMode
import com.meadow.feature.script.ui.state.ScriptEditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ScriptEditorViewModel @Inject constructor(
    private val repo: com.meadow.feature.script.domain.repository.ScriptRepositoryContract,
    private val parserDispatcher: ScriptParserDispatcher
) : ViewModel() {

    private val uiMessagesFlow = MutableSharedFlow<UiMessage>()
    val uiMessages = uiMessagesFlow.asSharedFlow()

    private val stateFlow = MutableStateFlow(ScriptEditorUiState())
    val uiState: StateFlow<ScriptEditorUiState> = stateFlow.asStateFlow()

    private var parseJob: Job? = null
    private var saveJob: Job? = null

    private val parseOptions = ParseOptions.strictDefaults()

    fun bind(
        scriptId: String,
        variantId: String
    ) {
        val current = stateFlow.value
        if (current.scriptId == scriptId && current.variantId == variantId) return
        stateFlow.value = current.copy(scriptId = scriptId, variantId = variantId)
        load(scriptId = scriptId, variantId = variantId)
    }

    private fun load(
        scriptId: String,
        variantId: String
    ) {
        viewModelScope.launch {
            val script = repo.getScript(scriptId)
            val variant = repo.getVariant(variantId)
            val dialect = script?.dialect ?: ScriptDialect.FOUNTAIN
            val content = variant?.content ?: ""

            stateFlow.value = stateFlow.value.copy(
                type = script?.type,
                dialect = dialect,
                textFieldValue = TextFieldValue(content)
            )

            scheduleParse(content, dialect)
        }
    }

    fun onDialectChanged(
        dialect: ScriptDialect
    ) {
        stateFlow.value = stateFlow.value.copy(dialect = dialect)
        scheduleParse(stateFlow.value.textFieldValue.text, dialect)
    }

    fun onModeChanged(
        mode: ScriptEditorMode
    ) {
        stateFlow.value = stateFlow.value.copy(mode = mode)
    }

    fun onTextChanged(
        value: TextFieldValue
    ) {
        stateFlow.value = stateFlow.value.copy(textFieldValue = value)
        scheduleParse(value.text, stateFlow.value.dialect)
        scheduleSave(value.text)
    }

    private fun scheduleParse(
        text: String,
        dialect: ScriptDialect
    ) {
        parseJob?.cancel()
        parseJob = viewModelScope.launch {
            delay(400)
            val result = parserDispatcher.parse(
                dialect = dialect,
                raw = text,
                options = parseOptions
            )
            stateFlow.value = stateFlow.value.copy(index = result.index)
        }
    }

    private fun scheduleSave(
        text: String
    ) {
        val variantId = stateFlow.value.variantId ?: return
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(800)
            stateFlow.value = stateFlow.value.copy(isSaving = true)
            repo.upsertVariantContent(variantId = variantId, content = text)
            stateFlow.value = stateFlow.value.copy(isSaving = false)
            uiMessagesFlow.emit(UiMessage.Snackbar(R.string.script_saved))
        }
    }
}