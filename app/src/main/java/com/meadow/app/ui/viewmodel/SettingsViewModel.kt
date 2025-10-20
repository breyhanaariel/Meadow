package com.meadow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val ds: SettingsDataStore) : ViewModel() {
    val themeIndex = ds.themeIndex.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
    val syncEnabled = ds.syncEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun toggleSync(enabled: Boolean) = viewModelScope.launch { ds.setSync(enabled) }
    fun updateTheme(index: Int) = viewModelScope.launch { ds.setTheme(index) }
}
