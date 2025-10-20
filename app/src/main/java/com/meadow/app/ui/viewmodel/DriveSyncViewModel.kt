package com.meadow.app.ui.viewmodel

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.sync.DriveSyncHelper
import kotlinx.coroutines.launch

class DriveSyncViewModel(private val helper: DriveSyncHelper) : ViewModel() {
    fun signIn(activity: Activity) {
        viewModelScope.launch { helper.signIn(activity) }
    }
    fun uploadFile(name: String, mime: String, uri: Uri) {
        viewModelScope.launch { helper.uploadFile(name, mime, uri) }
    }
}
