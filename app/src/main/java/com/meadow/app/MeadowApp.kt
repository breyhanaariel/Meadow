package com.meadow.app

import android.app.Application
import androidx.work.Configuration
import com.meadow.app.sync.HiltWorkerFactoryWrapper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MeadowApp : Application(), Configuration.Provider {
    @Inject lateinit var workerFactoryWrapper: HiltWorkerFactoryWrapper

    override fun onCreate() {
        super.onCreate()
    }

    override fun getWorkManagerConfiguration() =
        androidx.work.Configuration.Builder()
            .setWorkerFactory(workerFactoryWrapper.hiltWorkerFactory)
            .build()
}
