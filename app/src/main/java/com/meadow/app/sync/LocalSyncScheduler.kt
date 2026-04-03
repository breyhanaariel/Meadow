package com.meadow.app.sync

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import com.meadow.app.sync.SyncWorker

@Singleton
class LocalSyncScheduler @Inject constructor(
    private val context: Context
) {

    fun schedulePeriodicSync() {
        val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            6, TimeUnit.HOURS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "MeadowPeriodicSync",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }

    fun triggerManualSync() {
        val oneTime = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueue(oneTime)
    }

    fun cancelSync() {
        WorkManager.getInstance(context).cancelAllWorkByTag("MeadowPeriodicSync")
    }
}