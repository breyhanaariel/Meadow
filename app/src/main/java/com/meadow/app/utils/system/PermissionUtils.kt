package com.meadow.app.utils.system

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * PermissionUtils.kt
 *
 * Checks runtime permissions for storage, Drive sync, etc.
 */
object PermissionUtils {

    fun ensureStoragePermission(activity: Activity): Boolean {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        return if (ContextCompat.checkSelfPermission(activity, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), 101)
            false
        } else true
    }
}
