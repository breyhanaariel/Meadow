package com.meadow.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.meadow.app.ui.shell.MeadowAppShell
import com.meadow.core.ui.locale.LanguageStore
import com.meadow.core.ui.locale.LocaleManager
import com.meadow.feature.common.ui.navigation.FeatureEntry
import com.meadow.feature.project.domain.model.ProjectFeatureSpec
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ProjectFeatureSpecEntryPoint {
        fun specs(): Set<ProjectFeatureSpec>
    }

    @Inject
    lateinit var featureEntries: Set<@JvmSuppressWildcards FeatureEntry>

    override fun attachBaseContext(newBase: Context) {
        val language = runBlocking {
            LanguageStore(newBase).language.first()
        }
        val localized = LocaleManager.apply(newBase, language)
        super.attachBaseContext(localized)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val featureSpecs = EntryPointAccessors.fromApplication(
            this,
            ProjectFeatureSpecEntryPoint::class.java
        ).specs()

        setContent {
            MeadowAppShell(
                activity = this,
                featureEntries = featureEntries,
                featureSpecs = featureSpecs
            )
        }
    }
}