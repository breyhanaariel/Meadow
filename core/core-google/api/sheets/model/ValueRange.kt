package com.meadow.core.google.api.sheets.model

data class ValueRange(
    val range: String? = null,
    val values: List<List<String>> = emptyList()
)