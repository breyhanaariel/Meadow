package com.meadow.feature.project.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.meadow.feature.project.domain.model.ProjectType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "project_feature_preferences"

private val Context.projectFeatureDataStore by preferencesDataStore(
    name = DATASTORE_NAME
)

class ProjectFeaturePreferences(
    private val context: Context,
    private val projectId: String,
    private val projectType: ProjectType
) {

    /* ─── INTERNAL KEY FACTORY ───────────────────── */

    private fun key(feature: String): Preferences.Key<Boolean> =
        booleanPreferencesKey("project_${projectId}_$feature")

    private val initialized = booleanPreferencesKey(
        "project_${projectId}_initialized"
    )

    /* ─── FEATURE KEYS (STRING MATCHES ProjectFeatureSpec.key) ───────── */

    private val catalog = key("catalog")
    private val script = key("script")
    private val wiki = key("wiki")
    private val mindmap = key("mindmap")
    private val storyboard = key("storyboard")
    private val plotCards = key("plot_cards")
    private val familyTree = key("family_tree")
    private val timeline = key("timeline")
    private val calendar = key("calendar")

    /* ─── ALLOWED FEATURES BY PROJECT TYPE ───────────────────── */

    val allowedKeys: List<Preferences.Key<Boolean>> =
        when (projectType) {

            ProjectType.NOVEL -> listOf(
                catalog,
                script,
                calendar,
                wiki,
                timeline,
                plotCards,
                mindmap,
                familyTree
            )

            ProjectType.MOVIE,
            ProjectType.TV_SHOW -> listOf(
                catalog,
                script,
                calendar,
                storyboard,
                timeline,
                plotCards,
                wiki,
                mindmap,
                familyTree
            )

            ProjectType.GAME -> listOf(
                catalog,
                script,
                calendar,
                timeline,
                wiki,
                mindmap,
                familyTree
            )

            else -> listOf(
                catalog,
                script,
                calendar,
                wiki,
                mindmap,
                storyboard,
                plotCards,
                familyTree,
                timeline
            )
        }

    /* ─── OBSERVE STATE ───────────────────── */

    val features: Flow<Map<Preferences.Key<Boolean>, Boolean>> =
        context.projectFeatureDataStore.data.map { prefs ->
            allowedKeys.associateWith { key ->
                prefs[key] ?: false
            }
        }

    /* ─── WRITE ───────────────────── */

    suspend fun setEnabled(
        key: Preferences.Key<Boolean>,
        enabled: Boolean
    ) {
        context.projectFeatureDataStore.edit { prefs ->
            prefs[key] = enabled
        }
    }

    /* ─── DEFAULTS ───────────────────── */

    suspend fun ensureDefaults() {
        context.projectFeatureDataStore.edit { prefs ->

            if (prefs[initialized] == true) return@edit

            allowedKeys.forEach { key ->
                prefs[key] = key == catalog
            }

            prefs[initialized] = true
        }
    }
}
