package com.meadow.feature.script.domain.parser

interface ScriptDialectParser {
    fun parse(
        raw: String,
        options: ParseOptions
    ): ParseResult
}
