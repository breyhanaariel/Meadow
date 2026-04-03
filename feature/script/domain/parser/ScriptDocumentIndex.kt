package com.meadow.feature.script.domain.parser

data class ScriptDocumentIndex(
    val blocks: List<ScriptBlock>,
    val tokens: List<SyntaxToken>,
    val outline: List<OutlineItem>,
    val issues: List<ParseIssue>
) {
    companion object {
        val Empty = ScriptDocumentIndex(
            blocks = emptyList(),
            tokens = emptyList(),
            outline = emptyList(),
            issues = emptyList()
        )
    }
}
