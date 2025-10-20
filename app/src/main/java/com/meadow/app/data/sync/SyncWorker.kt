package com.meadow.app.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.meadow.app.data.repo.MeadowRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repo: MeadowRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            val projects = repo.getAllProjects().first()
            for (p in projects) {
                repo.pushProject(p)
                val scripts = repo.getScriptsForProject(p.id).first()
                for (s in scripts) repo.pushScript(s)
                val catalog = repo.getCatalog(p.id).first()
                for (c in catalog) repo.pushCatalogItem(c)
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        fun enqueueOneTime(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val req = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueue(req)
        }
    }
}
