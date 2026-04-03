package com.meadow.feature.script.domain.parser

data class ScriptBlock(
    val id: ScriptBlockId,
    val kind: BlockKind,
    val textRange: IntRange,
    val fingerprint: String,
    val orderKey: Long
)
