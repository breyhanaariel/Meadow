package com.meadow.feature.calendar.sync.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import com.meadow.feature.calendar.data.repository.CalendarRepository
import com.meadow.feature.calendar.sync.CalendarSyncRunner
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class CalendarSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val runner: CalendarSyncRunner,
    private val repo: CalendarRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dirtyIds = repo.getDirtyIds()

        return try {
            runner.runSync()
            Result.success()
        } catch (e: Exception) {
            val msg = e.message ?: e::class.java.simpleName
            if (dirtyIds.isNotEmpty()) {
                repo.markSyncFailure(dirtyIds, msg)
            }
            if (runAttemptCount >= 3) Result.failure() else Result.retry()
        }
    }
}
