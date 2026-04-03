package com.meadow.feature.project.aicontext.domain

import androidx.annotation.StringRes
import com.meadow.core.ai.domain.model.FieldAiAction
import com.meadow.feature.project.R as R

enum class AiMode {
    GENERATE,
    IMPROVE,
    REWRITE,
    EXPAND,
    SHORTEN;

    fun toFieldAiAction(): FieldAiAction =
        when (this) {
            GENERATE -> FieldAiAction.GENERATE
            IMPROVE -> FieldAiAction.IMPROVE
            REWRITE -> FieldAiAction.REWRITE
            EXPAND -> FieldAiAction.EXPAND
            SHORTEN -> FieldAiAction.SHORTEN
        }

    @StringRes
    fun labelResId(): Int =
        when (this) {
            GENERATE -> R.string.ai_mode_generate
            IMPROVE -> R.string.ai_mode_improve
            REWRITE -> R.string.ai_mode_rewrite
            EXPAND -> R.string.ai_mode_expand
            SHORTEN -> R.string.ai_mode_shorten
        }
}
