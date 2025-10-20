package com.meadow.app.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.meadow.app.data.firebase.StorageHelper
import com.meadow.app.data.firebase.DriveSyncHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * UploadAssetWorker
 * - Uploads local file to Firebase Storage
 * - Optionally mirrors to Google Drive (if enabled)
 */
@HiltWorker
class UploadAssetWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val storageHelper: StorageHelper,
    private val driveHelper: DriveSyncHelper,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val localPath = inputData.getString("localPath") ?: return Result.failure()
        val remotePath = inputData.getString("remotePath") ?: return Result.failure()
        val mirrorToDrive = inputData.getBoolean("mirrorDrive", false)

        return try {
            storageHelper.uploadFile(localPath, remotePath)
            if (mirrorToDrive) driveHelper.uploadFile(localPath, remotePath)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}