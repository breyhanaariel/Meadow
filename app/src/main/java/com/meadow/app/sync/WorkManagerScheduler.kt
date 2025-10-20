package com.meadow.app.sync

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WorkManagerScheduler.kt
 *
 * Handles enqueuing sync tasks safely and with conditions.
 */
@Singleton
class WorkManagerScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workerFactory: HiltWorkerFactory
) {
    private val workManager = WorkManager.getInstance(context)

    fun scheduleSync(projectId: String, immediate: Boolean = false) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setInputData(
                workDataOf("projectId" to projectId)
            )
            .apply {
                if (!immediate) setInitialDelay(15, TimeUnit.MINUTES)
            }
            .build()

        workManager.enqueueUniqueWork(
            "sync_project_$projectId",
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}