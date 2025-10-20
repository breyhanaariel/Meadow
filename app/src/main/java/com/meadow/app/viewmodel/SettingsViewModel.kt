package com.meadow.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.utils.system.EncryptionUtils
import com.meadow.app.utils.io.SettingsIOUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * SettingsViewModel.kt
 *
 * Manages user settings: appearance, integrations, encryption, AI prompts, and multi-user configs.
 */

class SettingsViewModel : ViewModel() {

    private val _currentTheme = MutableStateFlow("Lavender Dream")
    val currentTheme = _currentTheme.asStateFlow()

    private val _googleSettings = MutableStateFlow(
        mutableMapOf(
            "maps" to "",
            "drive" to "",
            "calendar" to "",
            "gemini" to "",
            "translate" to ""
        )
    )
    val googleSettings = _googleSettings.asStateFlow()

    private val _geminiPrompts = MutableStateFlow(
        mutableMapOf(
            "sprout" to "",
            "bloom" to "",
            "meadow" to ""
        )
    )
    val geminiPrompts = _geminiPrompts.asStateFlow()

    private val _collaborators = MutableStateFlow<List<UserCollaborator>>(emptyList())
    val collaborators = _collaborators.asStateFlow()

    private val _currentProject = MutableStateFlow("Global")
    val currentProject = _currentProject.asStateFlow()

    fun cycleTheme() {
        val themes = listOf("Lavender Dream", "Mint Cloud", "Peach Glow", "Rose Quartz")
        val currentIndex = themes.indexOf(_currentTheme.value)
        _currentTheme.value = themes[(currentIndex + 1) % themes.size]
    }

    fun updateGoogleApiKey(service: String, key: String) {
        _googleSettings.value[service] = key
    }

    fun updateGeminiPrompt(type: String, prompt: String) {
        _geminiPrompts.value[type] = prompt
    }

    fun saveAllSettings() {
        // Persist settings (e.g., DataStore or Firestore)
        // This is stubbed; implement your DataStore save here
    }

    fun exportSettingsJson(context: Context) {
        viewModelScope.launch {
            val json = Json.encodeToString(
                SettingsIOUtils.SettingsExport.serializer(),
                SettingsIOUtils.SettingsExport(
                    theme = _currentTheme.value,
                    googleKeys = _googleSettings.value.mapValues { EncryptionUtils.decrypt(it.value) },
                    prompts = _geminiPrompts.value
                )
            )
            SettingsIOUtils.exportJsonFile(context, json)
        }
    }

    fun importSettingsJson(context: Context) {
        viewModelScope.launch {
            val imported = SettingsIOUtils.importJsonFile(context)
            imported?.let {
                _currentTheme.value = it.theme
                it.googleKeys.forEach { (service, key) ->
                    _googleSettings.value[service] = EncryptionUtils.encrypt(key)
                }
                _geminiPrompts.value = it.prompts.toMutableMap()
            }
        }
    }

    fun testGoogleServiceConnection(service: String): Boolean {
        // TODO: Implement real API connectivity tests
        return true // Simulated success
    }

    fun inviteCollaborator() {
        // TODO: Implement Firebase/Firestore collaboration invites
    }

    fun triggerBackup() {
        // TODO: Implement Drive or Firebase backup
    }

    fun linkPromptsToProject(projectId: String) {
        // TODO: Save per-project prompt mapping
    }
}

@kotlinx.serialization.Serializable
data class UserCollaborator(
    val name: String,
    val email: String
)