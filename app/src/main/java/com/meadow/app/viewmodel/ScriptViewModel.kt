package com.meadow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.data.repository.ScriptRepository
import com.meadow.app.data.room.entities.ScriptEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ScriptViewModel.kt
 *
 * Manages scripts, auto-saving, and version history.
 */
@HiltViewModel
class ScriptViewModel @Inject constructor(
    private val scriptRepo: ScriptRepository
) : ViewModel() {

    private val _scripts = MutableStateFlow<List<ScriptEntity>>(emptyList())
    val scripts = _scripts.asStateFlow()

    private val _activeScript = MutableStateFlow<ScriptEntity?>(null)
    val activeScript = _activeScript.asStateFlow()

    private var autosaveJob: Job? = null

    fun loadScripts(projectId: String) {
        viewModelScope.launch {
            scriptRepo.getScriptsByProject(projectId).collect {
                _scripts.value = it
            }
        }
    }

    fun selectScript(script: ScriptEntity) {
        _activeScript.value = script
    }

    fun createScript(projectId: String, title: String, format: String = "fountain") {
        viewModelScope.launch {
            val newScript = ScriptEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                title = title,
                format = format,
                content = ""
            )
            scriptRepo.saveScript(newScript)
            _activeScript.value = newScript
        }
    }

    fun updateScriptContent(scriptId: String, content: String) {
        val current = _activeScript.value ?: return
        val updated = current.copy(content = content, lastModified = System.currentTimeMillis())
        _activeScript.value = updated
        startAutoSave(updated)
    }

    private fun startAutoSave(script: ScriptEntity) {
        autosaveJob?.cancel()
        autosaveJob = viewModelScope.launch {
            delay(10_000) // every 10s
            scriptRepo.saveScript(script)
            scriptRepo.saveVersion(script.id, script.content)
        }
    }
}