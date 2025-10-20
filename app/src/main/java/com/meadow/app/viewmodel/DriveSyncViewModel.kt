package com.meadow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.data.firebase.DriveSyncHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * DriveSyncViewModel.kt
 *
 * Coordinates Google Drive backup and restore.
 */

@HiltViewModel
class DriveSyncViewModel @Inject constructor(
    private val drive: DriveSyncHelper
) : ViewModel() {

    fun uploadBackup(localPath: String, projectName: String) {
        viewModelScope.launch {
            drive.uploadBackup(localPath, projectName)
        }
    }
}
