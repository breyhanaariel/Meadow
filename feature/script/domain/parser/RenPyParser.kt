package com.meadow.feature.script.domain.parser

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RenPyParser @Inject constructor() : ScriptDialectParser {

    override fun parse(
        raw: String,
        options: ParseOptions
    ): ParseResult {

        val blocks = mutableListOf<ScriptBlock>()
        val tokens = mutableListOf<SyntaxToken>()
        val outline = mutableListOf<OutlineItem>()
        val issues = mutableListOf<ParseIssue>()

        var cursor = 0
        var orderKey = 0L
        val lines = raw.lines()

        for (line in lines) {

            val lineStart = cursor
            val lineEnd = cursor + line.length
            val trimmed = line.trimEnd()
            val kind = classifyLine(trimmed)

            val fingerprint = Hashing.sha1("$kind|${normalizeFingerprintBase(trimmed)}")
            val blockId = Hashing.sha1("$fingerprint|$orderKey")
            val range = IntRange(lineStart, lineEnd)

            blocks += ScriptBlock(
                id = blockId,
                kind = kind,
                textRange = range,
                fingerprint = fingerprint,
                orderKey = orderKey
            )

            when (kind) {

                BlockKind.RENPY_LABEL -> {
                    tokens += SyntaxToken(lineStart, lineEnd, SyntaxStyle.KEYWORD)

                    val labelName = trimmed
                        .removePrefix("label")
                        .trim()
                        .removeSuffix(":")

                    if (labelName.isNotBlank()) {
                        outline += OutlineItem(
                            title = labelName,
                            blockId = blockId,
                            position = lineStart
                        )
                    }
                }

                BlockKind.RENPY_MENU,
                BlockKind.RENPY_JUMP,
                BlockKind.RENPY_CALL,
                BlockKind.RENPY_SCENE,
                BlockKind.RENPY_SHOW ->
                    tokens += SyntaxToken(lineStart, lineEnd, SyntaxStyle.KEYWORD)

                BlockKind.RENPY_DIALOGUE ->
                    tokens += SyntaxToken(lineStart, lineEnd, SyntaxStyle.DIALOGUE)

                BlockKind.RENPY_NARRATION ->
                    tokens += SyntaxToken(lineStart, lineEnd, SyntaxStyle.DIALOGUE)

                else -> Unit
            }

            cursor = lineEnd + 1
            orderKey++
        }

        return ParseResult(
            index = ScriptDocumentIndex(
                blocks = blocks,
                tokens = tokens,
                outline = outline,
                issues = issues
            )
        )
    }

    private fun classifyLine(
        line: String
    ): BlockKind {

        val t = line.trim()

        if (t.isBlank()) return BlockKind.RAW
        if (t.startsWith("label ")) return BlockKind.RENPY_LABEL
        if (t == "menu:" || t.startsWith("menu:")) return BlockKind.RENPY_MENU
        if (t.startsWith("jump ")) return BlockKind.RENPY_JUMP
        if (t.startsWith("call ")) return BlockKind.RENPY_CALL
        if (t.startsWith("scene ")) return BlockKind.RENPY_SCENE
        if (t.startsWith("show ")) return BlockKind.RENPY_SHOW
        if (t.startsWith("if ") || t.startsWith("elif ") || t.startsWith("else:"))
            return BlockKind.RAW

        val hasQuoted = t.contains('"')

        if (hasQuoted) {
            val startsWithQuote = t.startsWith('"')
            return if (startsWithQuote)
                BlockKind.RENPY_NARRATION
            else
                BlockKind.RENPY_DIALOGUE
        }

        return BlockKind.RAW
    }

    private fun normalizeFingerprintBase(
        line: String
    ): String {
        return line.trim().take(64)
    }
}