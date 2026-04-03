package com.meadow.feature.project.internal.template

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class ProjectOptionsSourceLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun load(sourceKey: String): List<String> {
        val json = readAsset("project_templates/$sourceKey.json") ?: return emptyList()
        val obj = JSONObject(json)
        return extractOptions(obj) ?: emptyList()
    }

    private fun extractOptions(obj: JSONObject): List<String>? {

        obj.optJSONArray("options")?.let { return jsonArrayToStrings(it) }

        obj.optString("options", null)?.let {
            if (it.isNotBlank()) return csvToList(it)
        }

        obj.optJSONArray("flatOptions")?.let { return jsonArrayToStrings(it) }

        val metadata = obj.optJSONObject("metadata") ?: return null

        metadata.optJSONArray("flatOptions")?.let {
            return jsonArrayToStrings(it)
        }

        metadata.optString("options", null)?.let {
            if (it.isNotBlank()) return csvToList(it)
        }

        val grouped = metadata.optJSONObject("groupedOptions")
        if (grouped != null) {
            val out = mutableListOf<String>()
            val keys = grouped.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                grouped.optJSONArray(key)?.let {
                    out += jsonArrayToStrings(it)
                }
            }
            return out.distinct()
        }

        return null
    }

    private fun jsonArrayToStrings(arr: JSONArray): List<String> =
        List(arr.length()) { idx -> arr.optString(idx).trim() }
            .filter { it.isNotBlank() }

    private fun csvToList(csv: String): List<String> =
        csv.split(",").map { it.trim() }.filter { it.isNotBlank() }

    private fun readAsset(path: String): String? =
        try {
            context.assets.open(path).bufferedReader().use { it.readText() }
        } catch (_: Throwable) {
            null
        }
}