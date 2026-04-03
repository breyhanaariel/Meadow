package com.meadow.feature.script.ui.state

import com.meadow.feature.script.domain.model.ScriptDialect
import com.meadow.feature.script.domain.model.ScriptType
import androidx.compose.ui.text.input.TextFieldValue
import com.meadow.feature.script.domain.parser.ScriptDocumentIndex

data class ScriptEditorUiState(
    val scriptId: String? = null,
    val variantId: String? = null,
    val type: ScriptType? = null,
    val dialect: ScriptDialect = ScriptDialect.FOUNTAIN,
    val mode: ScriptEditorMode = ScriptEditorMode.STRUCTURE,
    val textFieldValue: TextFieldValue = TextFieldValue(""),
    val index: ScriptDocumentIndex = ScriptDocumentIndex.Empty,
    val isSaving: Boolean = false
)