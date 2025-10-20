package com.meadow.app.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.meadow.app.data.firebase.StorageHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * DownloadAssetWorker
 * - Pulls a remote Storage file to local app cache for offline use.
 */
@HiltWorker
class DownloadAssetWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val storageHelper: StorageHelper
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val remotePath = inputData.getString("remotePath") ?: return Result.failure()
        val localPath = inputData.getString("localPath") ?: return Result.failure()

        return try {
            storageHelper.downloadFile(remotePath, localPath)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}