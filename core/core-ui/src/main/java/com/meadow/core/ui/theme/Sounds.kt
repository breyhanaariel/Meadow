package com.meadow.core.ui.theme

object MeadowSounds {

    const val GlitterTap = "glitter_tap.ogg"
    const val SuccessPop = "success_pop.ogg"
    const val SoftChime = "soft_chime.ogg"
    const val PetalSwipe = "petal_swipe.ogg"
    const val CandyDust = "candy_dust_swipe.ogg"
}

object SoundRoles {

    val OnTap = MeadowSounds.GlitterTap
    val OnConfirm = MeadowSounds.SuccessPop
    val OnHover = MeadowSounds.SoftChime

    val OnSuccess = MeadowSounds.SuccessPop
    val OnError = MeadowSounds.PetalSwipe
    val OnCelebrate = MeadowSounds.CandyDust

    val OnOpenModal = MeadowSounds.SoftChime
    val OnCloseModal = MeadowSounds.PetalSwipe
}
