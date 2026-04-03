package com.meadow.core.ui.theme

import androidx.compose.animation.core.*

object Motion {

    const val DurationShort = 120
    const val DurationMed = 220
    const val DurationLong = 420

    val EaseInOutStandard: AnimationSpec<Float> = tween(
        durationMillis = DurationMed,
        easing = androidx.compose.animation.core.EaseInOut
    )

    val SoftCurve: AnimationSpec<Float> = tween(
        durationMillis = DurationLong,
        easing = FastOutSlowInEasing
    )

    val PopSpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
}
object MotionRoles {
    val ButtonPress = Motion.PopSpring
    val ButtonHover = Motion.EaseInOutStandard
    val ButtonReveal = Motion.SoftCurve

    val CardEnter = Motion.SoftCurve
    val CardClick = Motion.PopSpring

    val DialogAppear = Motion.PopSpring
    val DialogDismiss = Motion.SoftCurve

    val ScreenEnter = Motion.SoftCurve
    val ScreenExit = Motion.EaseInOutStandard
}