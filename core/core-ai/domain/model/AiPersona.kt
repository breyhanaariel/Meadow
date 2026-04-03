package com.meadow.core.ai.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meadow.core.ai.R

enum class AiPersona(
    val displayName: String,
    val descriptionText: String,
    @DrawableRes val iconRes: Int,
    @StringRes val placeholderRes: Int
) {
    Bud(
        displayName = "Bud — Field Helper",
        descriptionText = "Generates and refines short field text.",
        iconRes = R.drawable.ic_ai_bud,
        placeholderRes = R.string.ai_placeholder_bud
    ),

    Sprout(
        displayName = "Sprout — Idea Generator",
        descriptionText = "Generates ideas to kickstart creativity.",
        iconRes = R.drawable.ic_ai_sprout,
        placeholderRes = R.string.ai_placeholder_sprout
    ),

    Petal(
        displayName = "Petal — Chapter Critique",
        descriptionText = "Critiques a chapter with inspiring insights.",
        iconRes = R.drawable.ic_ai_petal,
        placeholderRes = R.string.ai_placeholder_petal
    ),

    Bloom(
        displayName = "Bloom — Enhancing Editor",
        descriptionText = "Enhances writing with polished rewrites.",
        iconRes = R.drawable.ic_ai_bloom,
        placeholderRes = R.string.ai_placeholder_bloom
    ),

    Vine(
        displayName = "Vine — Chapter Drafter",
        descriptionText = "Drafts full scenes and chapters.",
        iconRes = R.drawable.ic_ai_vine,
        placeholderRes = R.string.ai_placeholder_vine
    ),

    Meadow(
        displayName = "Meadow — Writing Assistant",
        descriptionText = "Guides writing with deeper understanding.",
        iconRes = R.drawable.ic_ai_meadow,
        placeholderRes = R.string.ai_placeholder_meadow
    );

    @Composable
    fun placeholderText(): String = stringResource(placeholderRes)
}
