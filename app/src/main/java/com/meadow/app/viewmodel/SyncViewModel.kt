package com.meadow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.data.firebase.DriveSyncHelper
import com.meadow.app.data.firebase.FirestoreHelper
import com.meadow.app.data.repository.MeadowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SyncViewModel.kt
 *
 * Manages synchronization between local Room and Firebase.
 * Handles manual sync, Drive uploads, and real-time sync indicators.
 */

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val firestoreHelper: FirestoreHelper,
    private val driveSyncHelper: DriveSyncHelper,
    private val meadowRepository: MeadowRepository
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncStatus = MutableStateFlow("Idle")
    val lastSyncStatus: StateFlow<String> = _lastSyncStatus.asStateFlow()

    fun startSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            _lastSyncStatus.value = "Syncing..."

            try {
                val localProjects = meadowRepository.getAllProjects()
                firestoreHelper.uploadProjects(localProjects)
                driveSyncHelper.backupDatabase()
                _lastSyncStatus.value = "Sync Complete ✨"
            } catch (e: Exception) {
                _lastSyncStatus.value = "Sync Failed ❌"
            } finally {
                _isSyncing.value = false
            }
        }
    }
}