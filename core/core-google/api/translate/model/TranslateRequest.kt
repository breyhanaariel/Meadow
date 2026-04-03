package com.meadow.core.google.api.translate.model

data class TranslateRequest(
    val q: String,
    val target: String
)