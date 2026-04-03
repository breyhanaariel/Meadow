package com.meadow.core.ai.engine.personas.bud

import com.meadow.core.ai.domain.contracts.PersonaContract
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ai.domain.model.FieldAiAction

class BudHelper(
    private val templateText: String
) : PersonaContract {

    override val persona: AiPersona = AiPersona.Bud

    private val projectTypeGuidance = mapOf(
        "NOVEL" to """
Tone: emotionally immersive, layered, character-driven.
Avoid gimmicks. Favor nuance and atmosphere.
""".trimIndent(),

        "COMIC" to """
Tone: visually striking, bold, dramatic.
Think in images, panels, splash moments.
""".trimIndent(),

        "TV_SHOW" to """
Tone: episodic, character-dynamic, tension-forward.
Hint at arcs and recurring conflict.
""".trimIndent(),

        "MOVIE" to """
Tone: cinematic, high-impact, marketable.
Clarity and hook are critical.
""".trimIndent(),

        "GAME" to """
Tone: experiential, interactive, world-aware.
Emphasize player perspective or mechanics subtly.
""".trimIndent()
    )

    // ---------------------------
    // Field Micro Prompts
    // ---------------------------

    private fun fieldGuidance(fieldLabel: String?): String? {
        val label = fieldLabel?.lowercase() ?: return null

        return when {
            "title" in label ->
                """
Field Constraint:
- 1–6 words preferred
- Strong concept or rhythm
- Avoid generic fantasy/sci-fi phrasing
""".trimIndent()

            "description" in label || "summary" in label ->
                """
Field Constraint:
- Hook in first sentence
- 2 short paragraphs max
- Clear conflict or focus
""".trimIndent()

            "logline" in label ->
                """
Field Constraint:
- One sentence
- Protagonist + goal + obstacle
- 35 words max
""".trimIndent()

            "character" in label ->
                """
Field Constraint:
- Highlight defining trait
- Avoid clichés
- Suggest internal tension
""".trimIndent()

            "tagline" in label ->
                """
Field Constraint:
- 5–12 words
- Punchy, marketable
""".trimIndent()

            else -> null
        }
    }

    // ---------------------------
    // Build Prompt
    // ---------------------------

    fun buildPrompt(
        userInput: String?,
        action: FieldAiAction,
        fieldLabel: String? = null,
        projectType: String? = null
    ): String {

        val projectKey = projectType?.uppercase()

        return buildString {

            appendLine(templateText)
            appendLine()

            projectKey?.let {
                appendLine("Project Type:")
                appendLine(it)
                appendLine()

                projectTypeGuidance[it]?.let { guidance ->
                    appendLine("Project Tone Guidance:")
                    appendLine(guidance)
                    appendLine()
                }
            }

            fieldLabel?.let {
                appendLine("Field:")
                appendLine(it.uppercase())
                appendLine()

                fieldGuidance(it)?.let { guidance ->
                    appendLine(guidance)
                    appendLine()
                }
            }

            appendLine("Action:")
            appendLine(action.name)
            appendLine()

            appendLine("Current content:")
            appendLine(userInput?.takeIf { it.isNotBlank() } ?: "(empty)")
        }
    }

    override fun buildPrompt(userInput: String, extraContext: String?): String {
        return buildString {
            appendLine(templateText)
            appendLine()
            appendLine(userInput)
            extraContext?.let {
                appendLine()
                appendLine(it)
            }
        }
    }
}
