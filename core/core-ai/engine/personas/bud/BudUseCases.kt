package com.meadow.core.ai.engine.personas.bud

import com.meadow.core.ai.data.context.AiContextRepository
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ai.domain.model.AiResponse
import com.meadow.core.ai.domain.model.FieldAiAction
import com.meadow.core.ai.engine.gemini.GeminiClient
import javax.inject.Inject

class BudUseCases @Inject constructor(
    private val contextRepo: AiContextRepository,
    private val geminiClient: GeminiClient
) {

    suspend fun runFieldAction(
        currentText: String?,
        action: FieldAiAction,
        fieldLabel: String? = null,
        scopeKey: String? = null,
        projectTypeKey: String? = null,
        extraContextAdditions: String? = null
    ): AiResponse {

        val personaContexts =
            contextRepo.resolveInjectedContexts(
                persona = AiPersona.Bud,
                scopeKey = scopeKey,
                includeScoped = true
            )

        val contextBlock = contextRepo.buildContextBlock(personaContexts)

        val projectTypeMicro = buildProjectTypeMicroPrompt(projectTypeKey)

        val prompt =
            BudHelper(BudPrompt.template.text)
                .buildPrompt(
                    userInput = currentText,
                    action = action,
                    fieldLabel = fieldLabel
                )
                .let { base ->
                    buildString {
                        appendLine(base.trim())
                        if (contextBlock.isNotBlank()) {
                            appendLine()
                            appendLine(contextBlock)
                        }
                        if (projectTypeMicro.isNotBlank()) {
                            appendLine()
                            appendLine(projectTypeMicro)
                        }
                        extraContextAdditions?.takeIf { it.isNotBlank() }?.let {
                            appendLine()
                            appendLine(it.trim())
                        }
                    }.trim()
                }

        val output = geminiClient.generateText(
            prompt = prompt,
            persona = AiPersona.Bud
        )


        return AiResponse(
            persona = AiPersona.Bud,
            prompt = prompt,
            content = output
        )
    }

    private fun buildProjectTypeMicroPrompt(projectTypeKey: String?): String {
        val key = projectTypeKey?.trim()?.lowercase().orEmpty()
        if (key.isBlank() || key == "unknown") return ""

        return """
PROJECT TYPE MICRO-PROMPT:
- The user is working on a $key project.
- Keep outputs appropriate for this medium and the specific field type.
""".trim()
    }
}
