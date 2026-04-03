package com.meadow.core.ai.utils

import androidx.annotation.DrawableRes
import com.meadow.core.ai.R
import com.meadow.core.ai.domain.model.AiPersona

object PersonaUtils {

    @DrawableRes
    fun iconFor(persona: AiPersona): Int = when (persona) {
        AiPersona.Sprout -> R.drawable.ic_ai_sprout
        AiPersona.Bloom  -> R.drawable.ic_ai_bloom
        AiPersona.Petal  -> R.drawable.ic_ai_petal
        AiPersona.Meadow -> R.drawable.ic_ai_meadow
        AiPersona.Vine -> R.drawable.ic_ai_vine
        AiPersona.Bud -> R.drawable.ic_ai_bud

    }

    fun displayName(persona: AiPersona): String = when (persona) {
        AiPersona.Sprout -> "Sprout"
        AiPersona.Bloom  -> "Bloom"
        AiPersona.Petal  -> "Petal"
        AiPersona.Meadow -> "Meadow"
        AiPersona.Vine -> "Vine"
        AiPersona.Bud -> "Bud"

    }
}