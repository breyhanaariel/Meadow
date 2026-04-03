package com.meadow.feature.script.domain.parser

import com.meadow.feature.script.domain.model.ScriptDialect
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScriptParserDispatcher @Inject constructor(
    private val fountainParser: FountainParser,
    private val renPyParser: RenPyParser
) {

    fun parse(
        dialect: ScriptDialect,
        raw: String,
        options: ParseOptions
    ): ParseResult {
        return when (dialect) {
            ScriptDialect.FOUNTAIN -> fountainParser.parse(raw, options)
            ScriptDialect.RENPY -> renPyParser.parse(raw, options)
        }
    }
}
