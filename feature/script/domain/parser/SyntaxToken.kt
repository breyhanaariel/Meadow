package com.meadow.feature.script.domain.parser

data class SyntaxToken(
    val start: Int,
    val end: Int,
    val style: SyntaxStyle
)
