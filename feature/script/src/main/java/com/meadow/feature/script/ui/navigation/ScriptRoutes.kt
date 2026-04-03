package com.meadow.feature.script.ui.navigation

object ScriptRoutes {

    const val ArgProjectId = "projectId"
    const val ArgSeriesId = "seriesId"

    const val ArgScriptId = "scriptId"
    const val ArgVariantId = "variantId"

    const val SCRIPT_LIST_PROJECT = "script/list/project/{$ArgProjectId}"
    const val SCRIPT_EDITOR =
        "script/editor/{$ArgProjectId}/{$ArgScriptId}/variant/{$ArgVariantId}"

    fun projectScripts(projectId: String): String = "script/list/project/$projectId"
    fun editor(projectId: String, scriptId: String, variantId: String): String =
        "script/editor/$projectId/$scriptId/variant/$variantId"
}