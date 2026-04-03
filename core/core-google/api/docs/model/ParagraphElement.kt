package com.meadow.core.google.api.docs.model

data class ParagraphElement(
    val textRun: TextRun? = null,
    val startIndex: Int,
    val endIndex: Int
)

data class TextRun(
    val content: String? = null,
    val textStyle: TextStyle? = null
)

data class TextStyle(
    val bold: Boolean? = null,
    val italic: Boolean? = null
)
