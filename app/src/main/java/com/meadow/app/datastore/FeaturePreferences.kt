package com.meadow.app.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.meadow.app.R
import com.meadow.core.common.preferences.FeaturePreferencesProvider
import com.meadow.core.ui.R as CoreUiR
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
/* ─── DATASTORE ───────────────────────────── */

private const val DATASTORE_NAME = "feature_preferences"

private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

/* ─── GLOBAL FEATURE KEYS ─────────────────── */

object FeaturePreferencesKeys {

    val PROJECT = booleanPreferencesKey("feature_project")

    val CALENDAR  = booleanPreferencesKey("feature_calendar")
    val CATALOG = booleanPreferencesKey("feature_catalog")
    val SCRIPT  = booleanPreferencesKey("feature_script")

    //     val AI_CONTEXT  = booleanPreferencesKey("feature_aicontext")
    val PLOT_CARDS = booleanPreferencesKey("feature_plot_cards")
    val TIMELINE = booleanPreferencesKey("feature_timeline")

    val STORYBOARD = booleanPreferencesKey("feature_storyboard")
    val SHOT_LIST = booleanPreferencesKey("feature_shotlist")

    val FAMILY_TREE = booleanPreferencesKey("feature_family_tree")
    val WIKI = booleanPreferencesKey("feature_wiki")
    val MINDMAP = booleanPreferencesKey("feature_mindmap")


    val ALL = listOf(
        PROJECT,
        CALENDAR,
        CATALOG,
        SCRIPT,
        // AI_CONTEXT,
        PLOT_CARDS,
        TIMELINE,
        STORYBOARD,
        SHOT_LIST,
        FAMILY_TREE,
        WIKI,
        MINDMAP
    )

    fun labelFor(key: Preferences.Key<Boolean>): Int =
        when (key) {
            PROJECT -> CoreUiR.string.feature_project
            CALENDAR -> CoreUiR.string.feature_calendar
            CATALOG -> CoreUiR.string.feature_catalog
            SCRIPT -> CoreUiR.string.feature_script
            // AI_CONTEXT -> CoreUiR.string.feature_ai_context
            PLOT_CARDS -> CoreUiR.string.feature_plot_cards
            TIMELINE -> CoreUiR.string.feature_timeline
            STORYBOARD -> CoreUiR.string.feature_storyboard
            SHOT_LIST -> CoreUiR.string.feature_shotlist
            FAMILY_TREE -> CoreUiR.string.feature_family_tree
            WIKI -> CoreUiR.string.feature_wiki
            MINDMAP -> CoreUiR.string.feature_mindmap
            else -> CoreUiR.string.feature_unknown
        }
}

/* ─── GLOBAL FEATURE PREFERENCES ───────────── */

class FeaturePreferences @Inject constructor(
    private val context: Context
) : FeaturePreferencesProvider {

    /* ─── ALL FEATURE STATES ─────────────── */

    val features: Flow<Map<Preferences.Key<Boolean>, Boolean>> =
        context.dataStore.data.map { prefs ->
            FeaturePreferencesKeys.ALL.associateWith { key ->
                prefs[key] ?: false
            }
        }

    /* ─── ENABLED FEATURE KEYS ───────────── */

    override val enabledFeatures: Flow<Set<String>> =
        features.map { map ->
            map.filterValues { it }
                .keys
                .map { it.name.removePrefix("feature_") }
                .toSet()
        }

    /* ─── WRITE ─────────────────────────── */

    suspend fun setEnabled(
        key: Preferences.Key<Boolean>,
        enabled: Boolean
    ) {
        context.dataStore.edit { prefs ->
            prefs[key] = enabled
        }
    }

    /* ─── DEFAULTS ──────────────────────── */

    suspend fun ensureDefaults() {
        val existing = context.dataStore.data.first()

        context.dataStore.edit { prefs ->
            FeaturePreferencesKeys.ALL.forEach { key ->
                if (!existing.contains(key)) {
                    prefs[key] =
                        key == FeaturePreferencesKeys.PROJECT // ||
                              //  key == FeaturePreferencesKeys.CATALOG ||
                               // key == FeaturePreferencesKeys.SCRIPT ||
                               // key == FeaturePreferencesKeys.CALENDAR

                }
            }
        }
    }
}
