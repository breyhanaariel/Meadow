package com.meadow.feature.script.domain.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.meadow.feature.script.domain.parser.SyntaxStyle
import com.meadow.feature.script.domain.parser.SyntaxToken

class ScriptSyntaxVisualTransformation(
    private val tokens: List<SyntaxToken>,
    private val colors: ScriptSyntaxColors
) : VisualTransformation {

    override fun filter(
        text: AnnotatedString
    ): TransformedText {
        val annotated = buildAnnotatedString {
            append(text)
            for (token in tokens) {
                val start = token.start.coerceIn(0, text.length)
                val end = token.end.coerceIn(0, text.length)
                if (start >= end) continue
                addStyle(
                    style = styleFor(token.style),
                    start = start,
                    end = end
                )
            }
        }

        return TransformedText(
            annotated,
            OffsetMapping.Identity
        )
    }

    private fun styleFor(
        style: SyntaxStyle
    ): SpanStyle {
        return when (style) {
            SyntaxStyle.SCENE -> SpanStyle(color = colors.scene)
            SyntaxStyle.CHARACTER -> SpanStyle(color = colors.character)
            SyntaxStyle.DIALOGUE -> SpanStyle(color = colors.dialogue)
            SyntaxStyle.SECTION -> SpanStyle(color = colors.section)
            SyntaxStyle.METADATA -> SpanStyle(color = colors.metadata)
            SyntaxStyle.KEYWORD -> SpanStyle(color = colors.keyword)
            SyntaxStyle.COMMENT -> SpanStyle(color = colors.comment)
        }
    }
}

data class ScriptSyntaxColors(
    val scene: Color,
    val character: Color,
    val dialogue: Color,
    val section: Color,
    val metadata: Color,
    val keyword: Color,
    val comment: Color
)
