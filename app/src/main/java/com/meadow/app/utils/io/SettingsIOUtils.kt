package com.meadow.app.utils.io

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * SettingsIOUtils.kt
 *
 * Handles import/export of app configuration as JSON files.
 * Users can back up or share settings easily.
 */

object SettingsIOUtils {

    @Serializable
    data class SettingsExport(
        val theme: String,
        val googleKeys: Map<String, String>,
        val prompts: Map<String, String>
    )

    suspend fun exportJsonFile(context: Context, json: String) {
        withContext(Dispatchers.IO) {
            try {
                val file = context.getExternalFilesDir(null)?.resolve("meadow_settings.json")
                file?.writeText(json)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Settings exported 🌸", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    suspend fun importJsonFile(context: Context): SettingsExport? {
        return withContext(Dispatchers.IO) {
            try {
                val file = context.getExternalFilesDir(null)?.resolve("meadow_settings.json")
                if (file != null && file.exists()) {
                    val text = BufferedReader(InputStreamReader(file.inputStream())).readText()
                    return@withContext Json.decodeFromString<SettingsExport>(text)
                }
                null
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                null
            }
        }
    }
}