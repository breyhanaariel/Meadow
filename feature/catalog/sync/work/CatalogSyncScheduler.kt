package com.meadow.feature.catalog.sync.work

import android.content.Context
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Singleton
class CatalogSyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val workManager by lazy { WorkManager.getInstance(context) }

    private fun request(): OneTimeWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        return OneTimeWorkRequestBuilder<CatalogSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30, TimeUnit.SECONDS
            )
            .build()
    }

    fun triggerManual() {
        workManager.enqueueUniqueWork(
            "catalog_sync",
            ExistingWorkPolicy.REPLACE,
            request()
        )
    }

    fun ensurePeriodic() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodic = PeriodicWorkRequestBuilder<CatalogSyncWorker>(
            6, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30, TimeUnit.SECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "catalog_sync_periodic",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodic
        )
    }
}
