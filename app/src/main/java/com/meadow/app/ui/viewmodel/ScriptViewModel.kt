package com.meadow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.data.repo.MeadowRepository
import com.meadow.app.data.room.entities.ScriptEntity
import com.meadow.app.data.room.entities.ScriptVersionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class ScriptViewModel @Inject constructor(private val repo: MeadowRepository) : ViewModel() {
    private val _script = MutableStateFlow<ScriptEntity?>(null)
    val script: StateFlow<ScriptEntity?> = _script
    var versions = repo.getVersions("") // placeholder; call loadVersions(scriptId)

    fun loadScript(scriptId: String) {
        viewModelScope.launch {
            repo.getScript(scriptId).collect { s -> _script.value = s }
            versions = repo.getVersions(scriptId)
        }
    }

    fun saveScriptDraft(scriptId: String, content: String) = viewModelScope.launch {
        val cur = _script.value ?: return@launch
        val updated = cur.copy(content = content, lastModified = System.currentTimeMillis())
        repo.upsertScript(updated)
    }

    fun saveScriptFinal(scriptId: String, content: String) = saveScriptDraft(scriptId, content)

    fun applyWrapToSelection(wrapper: String) {
        val cur = _script.value ?: return
        val newContent = cur.content + wrapper
        viewModelScope.launch { repo.upsertScript(cur.copy(content = newContent)) }
    }

    fun insertAtCursor(text: String) {
        val cur = _script.value ?: return
        val newContent = cur.content + text
        viewModelScope.launch { repo.upsertScript(cur.copy(content = newContent)) }
    }

    fun restoreVersion(v: ScriptVersionEntity) {
        val cur = _script.value ?: return
        viewModelScope.launch { repo.upsertScript(cur.copy(content = v.content, lastModified = System.currentTimeMillis())) }
    }
}
