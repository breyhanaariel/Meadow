package com.meadow.core.ui.components.tokens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.theme.MeadowBrand
import com.meadow.core.ui.theme.MeadowShapes
import com.meadow.core.ui.theme.Dimensions

object ComponentRoles {
    object Shape {

        val Sheet =
            RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        val RoundedSmall = MeadowShapes.RoundedSmall
        val RoundedMedium = MeadowShapes.RoundedMedium
        val RoundedLarge = MeadowShapes.RoundedLarge
        val BubbleSoft = MeadowShapes.BubbleSoft
        val BubblePlayful = MeadowShapes.BubblePlayful
        val BubbleFull = MeadowShapes.BubbleFull
        val Capsule = MeadowShapes.Capsule
        val Chip = MeadowShapes.TagPill
    }

    object Elevation {
        val None = 0.dp
        val Low = 2.dp
        val Medium = 6.dp
        val High = 12.dp
    }

    object TextSize {
        val Body = Dimensions.Body
        val Caption = Dimensions.Caption
        val Title = Dimensions.Title
        val Display = Dimensions.Display
    }
}