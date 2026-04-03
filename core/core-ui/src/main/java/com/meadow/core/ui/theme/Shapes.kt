package com.meadow.core.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

@Immutable
object MeadowShapes {

    val RoundedTiny: CornerBasedShape = RoundedCornerShape(4.dp)
    val RoundedSmall: CornerBasedShape = RoundedCornerShape(8.dp)
    val RoundedMedium: CornerBasedShape = RoundedCornerShape(16.dp)
    val RoundedLarge: CornerBasedShape = RoundedCornerShape(24.dp)

    val BubbleSoft: CornerBasedShape = RoundedCornerShape(
        topStart = 16.dp, topEnd = 16.dp,
        bottomStart = 16.dp, bottomEnd = 8.dp
    )

    val BubblePlayful: CornerBasedShape = RoundedCornerShape(
        topStart = 20.dp, topEnd = 20.dp,
        bottomStart = 20.dp, bottomEnd = 0.dp
    )

    val BubbleFull: CornerBasedShape = RoundedCornerShape(
        topStart = 999.dp, topEnd = 999.dp,
        bottomStart = 999.dp, bottomEnd = 999.dp
    )

    val Capsule: CornerBasedShape = RoundedCornerShape(50)
    val TagPill: CornerBasedShape = RoundedCornerShape(50.dp)
    val ActionChip: CornerBasedShape = RoundedCornerShape(20.dp)

    val BubbleSmall: CornerBasedShape = RoundedCornerShape(Dimensions.RadiusSmall)
    val BubbleMed: CornerBasedShape = RoundedCornerShape(Dimensions.RadiusMed)
    val BubbleLarge: CornerBasedShape = RoundedCornerShape(Dimensions.RadiusLarge)

    fun materialShapes(): Shapes = Shapes(
        small = RoundedSmall,
        medium = RoundedMedium,
        large = RoundedLarge
    )
}

@Immutable
object ShapeRoles {

    val PrimaryButton = MeadowShapes.RoundedMedium
    val IconButton = MeadowShapes.RoundedSmall
    val FloatingActionButton = MeadowShapes.BubbleFull

    val Chip = MeadowShapes.TagPill
    val FilterChip = MeadowShapes.Capsule
    val StatusChip = MeadowShapes.BubbleSmall

    val ContentCard = MeadowShapes.BubbleSoft
    val ActionCard = MeadowShapes.BubbleMed
    val AlertCard = MeadowShapes.RoundedMedium
    val DialogCard = MeadowShapes.RoundedLarge

    val Tooltip = MeadowShapes.BubblePlayful
    val Snackbar = MeadowShapes.Capsule
    val ToastBubble = MeadowShapes.BubbleSmall
    val MascotReaction = MeadowShapes.BubbleFull

    val Avatar = MeadowShapes.BubbleFull
    val Badge = MeadowShapes.BubbleSmall
}