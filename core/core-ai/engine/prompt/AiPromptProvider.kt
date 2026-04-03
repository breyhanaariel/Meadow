package com.meadow.core.ai.engine.prompt

import android.content.Context
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ai.domain.model.ChatScope
import com.meadow.core.utils.files.JsonUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


@Singleton
class AiPromptProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val userPath = "prompts_user.json"
    private val assetPath = "prompts.json"


    private fun mapKeysToPersona(map: Map<String, String>): Map<AiPersona, String> =
        map.mapKeys { (key, _) ->
            AiPersona.values().firstOrNull {
                it.name.equals(key, ignoreCase = true)
            } ?: AiPersona.Sprout // safe fallback
        }

    fun getPrompt(persona: AiPersona): String {

        val userJson: Map<String, JsonElement>? =
            JsonUtils.readJson(context, userPath)

        val userPrompt = userJson
            ?.get("core_prompts")
            ?.jsonObject
            ?.get(persona.name)
            ?.jsonPrimitive
            ?.content

        if (!userPrompt.isNullOrBlank()) {
            return userPrompt
        }

        val assetJson: Map<String, JsonElement>? =
            JsonUtils.readJson(context, assetPath)

        val assetPrompt = assetJson
            ?.get("core_prompts")
            ?.jsonObject
            ?.get(persona.name)
            ?.jsonPrimitive
            ?.content

        if (!assetPrompt.isNullOrBlank()) {
            return assetPrompt
        }

        return AiPromptTemplates.forPersona(persona).text
    }

}
