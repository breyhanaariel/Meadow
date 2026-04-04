package com.meadow.app.ui.shell

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class AppShellState {

    var isAiChatOpen by mutableStateOf(false)
        private set

    var showLanguagePicker by mutableStateOf(false)
        private set

    var showProjectPicker by mutableStateOf(false)
        private set

    fun openAiChat() {
        isAiChatOpen = true
    }

    fun closeAiChat() {
        isAiChatOpen = false
    }

    fun showLanguagePicker() {
        showLanguagePicker = true
    }

    fun hideLanguagePicker() {
        showLanguagePicker = false
    }

    fun showProjectPicker() {
        showProjectPicker = true
    }

    fun hideProjectPicker() {
        showProjectPicker = false
    }
}