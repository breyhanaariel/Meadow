package com.meadow.core.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.meadow.core.ui.R

/* ─── GRADIENT HELPER ──────────────────── */
fun baseVerticalGradient(
    top: Color,
    mid: Color,
    bottom: Color
): Brush =
    Brush.verticalGradient(
        0f to top,
        0.5f to mid,
        1f to bottom
    )

fun baseLinearGradient(
    start: Color,
    end: Color,
    startOffset: Offset = Offset.Zero,
    endOffset: Offset = Offset(600f, 600f)
): Brush =
    Brush.linearGradient(
        colors = listOf(start, end),
        start = startOffset,
        end = endOffset
    )
data class GradientStops(
    val light: Color,
    val mid: Color,
    val dark: Color
)

fun stopsFor(theme: MeadowThemeVariant): GradientStops =
    when (theme) {
        MeadowThemeVariant.Lavender ->
            GradientStops(
                light = MeadowBrand.Lavender200,
                mid   = MeadowBrand.Lavender400,
                dark  = MeadowBrand.Lavender700
            )

        MeadowThemeVariant.Blush ->
            GradientStops(
                light = MeadowBrand.Blush150,
                mid   = MeadowBrand.Blush300,
                dark  = MeadowBrand.Blush500
            )

        MeadowThemeVariant.Periwinkle ->
            GradientStops(
                light = MeadowBrand.Periwinkle200,
                mid   = MeadowBrand.Periwinkle350,
                dark  = MeadowBrand.Periwinkle500
            )

        MeadowThemeVariant.Mint ->
            GradientStops(
                light = MeadowBrand.Mint150,
                mid   = MeadowBrand.Mint300,
                dark  = MeadowBrand.Mint500
            )

        MeadowThemeVariant.Peach ->
            GradientStops(
                light = MeadowBrand.Peach150,
                mid   = MeadowBrand.Peach300,
                dark  = MeadowBrand.Peach500
            )

        MeadowThemeVariant.Cream ->
            GradientStops(
                light = MeadowBrand.Cream200,
                mid   = MeadowBrand.Cream300,
                dark  = MeadowBrand.Cream450
            )
    }

object MeadowGradients {

    /* ─── Core theme gradients ─── */

    fun iconGradientFor(theme: MeadowThemeVariant): Brush {
        val s = stopsFor(theme)
        return Brush.linearGradient(
            colorStops = arrayOf(
                0.0f to s.light,
                0.55f to s.mid,
                1.0f to s.dark
            ),
            start = Offset(0f, 0f),
            end = Offset(600f, 600f)
        )
    }

    fun wordmarkGradientFor(theme: MeadowThemeVariant): Brush {
        val s = stopsFor(theme)
        return baseVerticalGradient(s.dark, s.mid, s.light)
    }

    fun buttonGradientFor(theme: MeadowThemeVariant): Brush {
        val s = stopsFor(theme)
        return baseVerticalGradient(s.mid, s.light, s.mid)
    }

    fun chipGradientFor(theme: MeadowThemeVariant): Brush {
        val s = stopsFor(theme)
        return baseLinearGradient(s.light, s.mid)
    }

    fun drawerGradientFor(theme: MeadowThemeVariant): Brush {
        val s = stopsFor(theme)
        return baseVerticalGradient(s.light, s.mid, s.dark)
    }

    fun cardGradientFor(theme: MeadowThemeVariant): Brush {
        val s = stopsFor(theme)
        return baseLinearGradient(
            start = s.light,
            end = s.mid,
            endOffset = Offset(800f, 400f)
        )
    }

    fun backgroundGradientFor(theme: MeadowThemeVariant): Brush {
        val s = stopsFor(theme)
        return baseVerticalGradient(s.light, s.mid, s.light)
    }

    /* ─── Static decorative gradients ─── */

    val SoftSurface = baseLinearGradient(
        MeadowBrand.SurfaceLight,
        MeadowBrand.SurfaceVariant,
        endOffset = Offset(300f, 600f)
    )

    val FloatingGlow = baseLinearGradient(
        MeadowBrand.GlowPink,
        MeadowBrand.GlowPurple,
        endOffset = Offset(0f, 1000f)
    )

    val PastelSheen = Brush.linearGradient(
        colors = listOf(
            MeadowBrand.Lavender100,
            MeadowBrand.BlushLight,
            MeadowBrand.MintLight
        ),
        start = Offset.Zero,
        end = Offset(1200f, 900f)
    )

    /* ─── PNG fallbacks (unchanged) ─── */

    object PNG {
        val AppBar = R.drawable.bg_appbar_gradient
        val LoadingBar = R.drawable.bg_loadingbar_gradient
        val Drawer = R.drawable.bg_drawer_gradient

        val Pastel = R.drawable.bg_gradient_pastel
        val Lavender = R.drawable.bg_gradient_lavender
        val MintLavender = R.drawable.bg_gradient_mint_lavender
        val PeachCream = R.drawable.bg_gradient_peach_cream

        val ButtonDefault = R.drawable.bg_button_gradient
        val ButtonGlitter = R.drawable.bg_button_glitter
        val Chip = R.drawable.bg_chip_gradient

        val Mint = R.drawable.bg_gradient_mint
        val Peach = R.drawable.bg_gradient_peach
        val PeachPink = R.drawable.bg_gradient_peach_pink
    }

    fun buttonForTheme(theme: MeadowThemeVariant): Int = when (theme) {
        MeadowThemeVariant.Lavender,
        MeadowThemeVariant.Blush,
        MeadowThemeVariant.Periwinkle,
        MeadowThemeVariant.Cream -> PNG.ButtonDefault

        MeadowThemeVariant.Peach -> PNG.PeachCream
        MeadowThemeVariant.Mint  -> PNG.MintLavender
    }

    fun chipForTheme(theme: MeadowThemeVariant): Int = PNG.Chip
    fun drawerForTheme(): Int = PNG.Drawer
}
