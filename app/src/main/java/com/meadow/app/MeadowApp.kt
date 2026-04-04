package com.meadow.app

import android.app.Application
import com.meadow.core.google.engine.GoogleAuthManager
import com.meadow.feature.common.state.FeaturePreferences
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltAndroidApp
class MeadowApp : Application() {

    @Inject
    lateinit var googleAuthManager: GoogleAuthManager

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        PDFBoxResourceLoader.init(applicationContext)

        val featurePrefs = FeaturePreferences(this)

        appScope.launch {
            featurePrefs.ensureDefaults()

            if (googleAuthManager.isSignedIn()) {
                googleAuthManager.refreshAccessToken(
                    clientId = BuildConfig.GOOGLE_WEB_CLIENT_ID,
                    clientSecret = BuildConfig.GOOGLE_CLIENT_SECRET
                )
            }
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}