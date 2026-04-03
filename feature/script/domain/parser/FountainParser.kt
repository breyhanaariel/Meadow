package com.meadow.feature.script.domain.parser

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FountainParser @Inject constructor() : ScriptDialectParser {

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
            val kind = classifyLine(trimmed, options)

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
                BlockKind.FOUNTAIN_SCENE_HEADING -> {
                    tokens += SyntaxToken(lineStart, lineEnd, SyntaxStyle.SCENE)
                    if (trimmed.isNotBlank()) {
                        outline += OutlineItem(
                            title = trimmed,
                            blockId = blockId,
                            position = lineStart
                        )
                    }
                }

                BlockKind.FOUNTAIN_CHARACTER ->
                    tokens += SyntaxToken(lineStart, lineEnd, SyntaxStyle.CHARACTER)

                BlockKind.FOUNTAIN_DIALOGUE ->
                    tokens += SyntaxToken(lineStart, lineEnd, SyntaxStyle.DIALOGUE)

                BlockKind.FOUNTAIN_SECTION ->
                    tokens += SyntaxToken(lineStart, lineEnd, SyntaxStyle.SECTION)

                BlockKind.FOUNTAIN_METADATA ->
                    tokens += SyntaxToken(lineStart, lineEnd, SyntaxStyle.METADATA)

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
        line: String,
        options: ParseOptions
    ): BlockKind {

        if (line.isBlank()) return BlockKind.FOUNTAIN_ACTION
        if (line.startsWith("#")) return BlockKind.FOUNTAIN_SECTION
        if (line.startsWith("=")) return BlockKind.FOUNTAIN_SYNOPSIS
        if (options.extensionsEnabled && line.startsWith("@"))
            return BlockKind.FOUNTAIN_METADATA

        val upper = line.uppercase()

        val isScene =
            upper.startsWith("INT.") ||
                    upper.startsWith("EXT.") ||
                    upper.startsWith("INT/") ||
                    upper.startsWith("EXT/")

        if (isScene) return BlockKind.FOUNTAIN_SCENE_HEADING

        val isCharacter =
            line.length in 2..35 &&
                    line == upper &&
                    line.all {
                        it.isLetterOrDigit() ||
                                it == ' ' ||
                                it == '(' ||
                                it == ')' ||
                                it == '.' ||
                                it == '-'
                    }

        if (isCharacter) return BlockKind.FOUNTAIN_CHARACTER

        val isDialogue =
            line.startsWith("(") || line.startsWith("\"")

        if (isDialogue) return BlockKind.FOUNTAIN_DIALOGUE

        return BlockKind.FOUNTAIN_ACTION
    }

    private fun normalizeFingerprintBase(
        line: String
    ): String {
        return line.trim().take(64)
    }
}