package com.meadow.core.ai.ui.selection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.meadow.core.ai.engine.manager.AiManager
import com.meadow.core.ai.domain.model.AiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class TextSelectionAiAction {
    SproutIdeas,
    DraftWithVine,
    CritiqueWithPetal,
    RewriteWithBloom
}

class TextSelectionAiRouter(
    private val aiManager: AiManager
) {
    suspend fun route(
        action: TextSelectionAiAction,
        selectedText: String,
        scopeKey: String?
    ): AiResponse {
        return when (action) {
            TextSelectionAiAction.SproutIdeas ->
                aiManager.sprout.generateIdea(selectedText, scopeKey)

            TextSelectionAiAction.DraftWithVine ->
                aiManager.vine.draft(selectedText, scopeKey)

            TextSelectionAiAction.CritiqueWithPetal ->
                aiManager.petal.critique(selectedText, scopeKey)

            TextSelectionAiAction.RewriteWithBloom ->
                aiManager.bloom.rewrite(selectedText, scopeKey)
        }
    }
}

class SelectionAiController(
    private val router: TextSelectionAiRouter,
    private val scope: CoroutineScope
) {
    var showSheet by mutableStateOf(false)
        private set

    var loading by mutableStateOf(false)
        private set

    var aiResult by mutableStateOf<String?>(null)
        private set

    private var selectedRange: TextRange? = null
    private var selectedText: String = ""

    fun open(
        action: TextSelectionAiAction,
        textFieldValue: TextFieldValue,
        scopeKey: String?
    ) {
        val range = textFieldValue.selection
        if (range.start == range.end) return

        selectedRange = range
        selectedText = textFieldValue.text.substring(range.start, range.end)

        showSheet = true
        aiResult = null

        scope.launch {
            loading = true
            try {
                val response = router.route(
                    action = action,
                    selectedText = selectedText,
                    scopeKey = scopeKey
                )

                aiResult = response.content
            } catch (t: Throwable) {
                aiResult = t.message
            } finally {
                loading = false
            }
        }
    }

    fun applyReplace(current: TextFieldValue): TextFieldValue {
        val range = selectedRange ?: return current
        val result = aiResult ?: return current

        val newText = current.text.replaceRange(range.start, range.end, result)
        showSheet = false

        return current.copy(
            text = newText,
            selection = TextRange(range.start + result.length)
        )
    }

    fun applyInsertBelow(current: TextFieldValue): TextFieldValue {
        val range = selectedRange ?: return current
        val result = aiResult ?: return current

        val insertAt = range.end
        val insertBlock = "\n\n$result"

        val newText = buildString {
            append(current.text)
            insert(insertAt, insertBlock)
        }

        showSheet = false

        return current.copy(
            text = newText,
            selection = TextRange(insertAt + insertBlock.length)
        )
    }

    fun dismiss() {
        showSheet = false
        aiResult = null
    }
}