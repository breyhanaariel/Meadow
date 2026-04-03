package com.meadow.core.ai.utils

import com.meadow.core.ai.domain.model.FieldAiAction

object FieldAiPromptBuilder {

    fun build(
        action: FieldAiAction,
        fieldKey: String,
        fieldValue: String?
    ): String =
        when (action) {

            FieldAiAction.GENERATE ->
                "Generate content for the field \"$fieldKey\"."

            FieldAiAction.IMPROVE ->
                "Improve the following content for \"$fieldKey\":\n\n${fieldValue.orEmpty()}"

            FieldAiAction.REWRITE ->
                "Rewrite the following content for \"$fieldKey\":\n\n${fieldValue.orEmpty()}"
        }
}
