package com.meadow.core.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.meadow.core.data.fields.FieldDensity

enum class MeadowThemeVariant {
    Lavender, Blush, Periwinkle, Mint, Peach, Cream
}


data class FieldStyleTokens(

    val fieldSurface: Color,
    val sectionSurface: Color,
    val imageSurface: Color,

    val borderIdle: Color,
    val borderFocused: Color,

    val textPlaceholder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDisabled: Color,

    val selectSurface: Color,
    val selectBorder: Color,
    val selectText: Color,

    val controlUnchecked: Color,
    val controlChecked: Color,
    val controlDisabled: Color,

    val radiusField: Float,
    val radiusTextarea: Float,
    val radiusImage: Float,

    val paddingCompact: Dp,
    val paddingComfortable: Dp,
    val paddingExpressive: Dp,
    val minHeightCompact: Dp,
    val minHeightComfortable: Dp,
    val minHeightExpressive: Dp,
    val placeholderAlpha: Float,
    val dividerAlpha: Float
)

fun FieldStyleTokens.paddingFor(density: FieldDensity): Dp =
    when (density) {
        FieldDensity.Compact -> paddingCompact
        FieldDensity.Comfortable -> paddingComfortable
        FieldDensity.Expressive -> paddingExpressive
    }

fun FieldStyleTokens.minHeightFor(density: FieldDensity): Dp =
    when (density) {
        FieldDensity.Compact -> minHeightCompact
        FieldDensity.Comfortable -> minHeightComfortable
        FieldDensity.Expressive -> minHeightExpressive
    }

fun FieldStyleTokens.radiusFor(density: FieldDensity): Float =
    when (density) {
        FieldDensity.Compact -> radiusField
        FieldDensity.Comfortable -> radiusTextarea
        FieldDensity.Expressive -> radiusImage
    }

object MeadowFieldTokens {
    lateinit var current: FieldStyleTokens
}

data class MeadowStatusTokens(
    val success: Color,
    val warning: Color,
    val error: Color
)

val LocalMeadowStatusTokens = staticCompositionLocalOf {
    MeadowStatusTokens(
        success = MeadowBrand.Success,
        warning = MeadowBrand.Warning,
        error = MeadowBrand.Error
    )
}
object ThemeTokens {
    fun statusTokensFor(theme: MeadowThemeVariant): MeadowStatusTokens =
        when (theme) {
            MeadowThemeVariant.Lavender -> MeadowStatusTokens(
                success = MeadowBrand.Lavender450,
                warning = MeadowBrand.Lavender350,
                error = MeadowBrand.Lavender600
            )

            MeadowThemeVariant.Blush -> MeadowStatusTokens(
                success = MeadowBrand.Blush350,
                warning = MeadowBrand.Blush300,
                error = MeadowBrand.Blush450
            )

            MeadowThemeVariant.Periwinkle -> MeadowStatusTokens(
                success = MeadowBrand.Periwinkle350,
                warning = MeadowBrand.Periwinkle300,
                error = MeadowBrand.Periwinkle450
            )

            MeadowThemeVariant.Mint -> MeadowStatusTokens(
                success = MeadowBrand.Mint350,
                warning = MeadowBrand.Mint300,
                error = MeadowBrand.Mint450
            )

            MeadowThemeVariant.Peach -> MeadowStatusTokens(
                success = MeadowBrand.Peach350,
                warning = MeadowBrand.Peach300,
                error = MeadowBrand.Peach450
            )

            MeadowThemeVariant.Cream -> MeadowStatusTokens(
                success = MeadowBrand.Cream350,
                warning = MeadowBrand.Cream300,
                error = MeadowBrand.Cream400
            )
        }
    fun iconGradientFor(theme: MeadowThemeVariant): Brush =
        MeadowGradients.iconGradientFor(theme)

    fun colorSchemeFor(variant: MeadowThemeVariant): ColorScheme =
        when (variant) {
            MeadowThemeVariant.Lavender -> lightColorScheme(
                primaryContainer = MeadowBrand.Lavender200,
                onPrimaryContainer = MeadowBrand.Lavender800,

                primary = MeadowBrand.Lavender500,
                onPrimary = MeadowBrand.Lavender800,

                secondary = MeadowBrand.Lavender300,
                onSecondary = MeadowBrand.Lavender800,

                background = MeadowBrand.Lavender50,
                onBackground = MeadowBrand.Lavender800,

                surface = MeadowBrand.Lavender75,
                onSurface = MeadowBrand.Lavender800,
                onSurfaceVariant = MeadowBrand.Lavender600,

                inverseSurface = MeadowBrand.Lavender700,
                inverseOnSurface = MeadowBrand.Lavender50,

                surfaceVariant = MeadowBrand.Lavender75,
                outline = MeadowBrand.Lavender200,
                outlineVariant = MeadowBrand.Lavender200,
                scrim = MeadowBrand.Lavender200.copy(alpha = 0.32f),

                surfaceContainerLow = MeadowBrand.Lavender100,
                surfaceContainer = MeadowBrand.Lavender150,
                surfaceContainerHigh = MeadowBrand.Lavender200,
                surfaceContainerHighest = MeadowBrand.Lavender225
            )

            MeadowThemeVariant.Blush -> lightColorScheme(
                primaryContainer = MeadowBrand.Blush150,
                onPrimaryContainer = MeadowBrand.Blush500,

                primary = MeadowBrand.Blush400,
                onPrimary = MeadowBrand.Blush500,

                secondary = MeadowBrand.Blush250,
                onSecondary = MeadowBrand.Blush500,

                background = MeadowBrand.Blush50,
                onBackground = MeadowBrand.Blush500,

                surface = MeadowBrand.Blush75,
                onSurface = MeadowBrand.Blush500,
                onSurfaceVariant = MeadowBrand.Blush450,

                inverseSurface = MeadowBrand.Blush450,
                inverseOnSurface = MeadowBrand.Blush50,

                surfaceVariant = MeadowBrand.Blush75,
                outline = MeadowBrand.Blush200,
                outlineVariant = MeadowBrand.Blush200,

                scrim = MeadowBrand.Blush200.copy(alpha = 0.32f),

                surfaceContainerLow = MeadowBrand.Blush150,
                surfaceContainer = MeadowBrand.Blush200,
                surfaceContainerHigh = MeadowBrand.Blush250,
                surfaceContainerHighest = MeadowBrand.Blush225

            )

            MeadowThemeVariant.Periwinkle -> lightColorScheme(
                primaryContainer = MeadowBrand.Periwinkle150,
                onPrimaryContainer = MeadowBrand.Periwinkle500,

                primary = MeadowBrand.Periwinkle300,
                onPrimary = MeadowBrand.Periwinkle500,

                secondary = MeadowBrand.Periwinkle200,
                onSecondary = MeadowBrand.Periwinkle500,

                background = MeadowBrand.Periwinkle50,
                onBackground = MeadowBrand.Periwinkle500,

                surface = MeadowBrand.Periwinkle75,
                onSurface = MeadowBrand.Periwinkle500,
                onSurfaceVariant = MeadowBrand.Periwinkle450,

                inverseSurface = MeadowBrand.Periwinkle450,
                inverseOnSurface = MeadowBrand.Periwinkle50,

                surfaceVariant = MeadowBrand.Periwinkle75,
                outline = MeadowBrand.Periwinkle200,
                outlineVariant = MeadowBrand.Periwinkle200,

                scrim = MeadowBrand.Periwinkle200.copy(alpha = 0.32f),

                surfaceContainerLow = MeadowBrand.Periwinkle150,
                surfaceContainer = MeadowBrand.Periwinkle200,
                surfaceContainerHigh = MeadowBrand.Periwinkle250,
                surfaceContainerHighest = MeadowBrand.Periwinkle225

            )

            MeadowThemeVariant.Mint -> lightColorScheme(
                primaryContainer = MeadowBrand.Mint150,
                onPrimaryContainer = MeadowBrand.Mint500,

                primary = MeadowBrand.Mint300,
                onPrimary = MeadowBrand.Mint500,

                secondary = MeadowBrand.Mint200,
                onSecondary = MeadowBrand.Mint500,

                background = MeadowBrand.Mint50,
                onBackground = MeadowBrand.Mint500,

                surface = MeadowBrand.Mint75,
                onSurface = MeadowBrand.Mint500,
                onSurfaceVariant = MeadowBrand.Mint450,

                inverseSurface = MeadowBrand.Mint450,
                inverseOnSurface = MeadowBrand.Mint50,

                surfaceVariant = MeadowBrand.Mint75,
                outline = MeadowBrand.Mint200,
                outlineVariant = MeadowBrand.Mint200,

                scrim = MeadowBrand.Mint200.copy(alpha = 0.32f),

                surfaceContainerLow = MeadowBrand.Mint150,
                surfaceContainer = MeadowBrand.Mint200,
                surfaceContainerHigh = MeadowBrand.Mint250,
                surfaceContainerHighest = MeadowBrand.Mint200
            )

            MeadowThemeVariant.Peach -> lightColorScheme(
                primaryContainer = MeadowBrand.Peach150,
                onPrimaryContainer = MeadowBrand.Peach550,

                primary = MeadowBrand.Peach300,
                onPrimary = MeadowBrand.Peach550,

                secondary = MeadowBrand.Peach200,
                onSecondary = MeadowBrand.Peach550,

                background = MeadowBrand.Peach50,
                onBackground = MeadowBrand.Peach550,

                surface = MeadowBrand.Peach75,
                onSurface = MeadowBrand.Peach550,
                onSurfaceVariant = MeadowBrand.Peach450,

                inverseSurface = MeadowBrand.Peach450,
                inverseOnSurface = MeadowBrand.Peach50,

                surfaceVariant = MeadowBrand.Peach75,
                outline = MeadowBrand.Peach200,
                outlineVariant = MeadowBrand.Peach200,

                scrim = MeadowBrand.Peach200.copy(alpha = 0.32f),

                surfaceContainerLow = MeadowBrand.Peach125,
                surfaceContainer = MeadowBrand.Peach150,
                surfaceContainerHigh = MeadowBrand.Peach200,
                surfaceContainerHighest = MeadowBrand.Peach225
            )

            MeadowThemeVariant.Cream -> lightColorScheme(
                primaryContainer = MeadowBrand.Cream150,
                onPrimaryContainer = MeadowBrand.Cream450,

                primary = MeadowBrand.Cream300,
                onPrimary = MeadowBrand.Cream450,

                secondary = MeadowBrand.Cream250,
                onSecondary = MeadowBrand.Cream450,

                background = MeadowBrand.Cream50,
                onBackground = MeadowBrand.Cream450,

                surface = MeadowBrand.Cream75,
                onSurface = MeadowBrand.Cream450,
                onSurfaceVariant = MeadowBrand.Cream400,

                inverseSurface = MeadowBrand.Cream400,
                inverseOnSurface = MeadowBrand.Cream50,

                surfaceVariant = MeadowBrand.Cream75,
                outline = MeadowBrand.Cream200,
                outlineVariant = MeadowBrand.Cream200,

                scrim = MeadowBrand.Cream200.copy(alpha = 0.32f),

                surfaceContainerLow = MeadowBrand.Cream125,
                surfaceContainer = MeadowBrand.Cream150,
                surfaceContainerHigh = MeadowBrand.Cream200,
                surfaceContainerHighest = MeadowBrand.Cream200

            )
        }

    fun fieldTokensFor(
        theme: MeadowThemeVariant,
        effects: EffectSettings
    ): FieldStyleTokens {

        val baseRadius = when (effects.fieldCornerStyle) {
            1 -> 20f
            2 -> 12f
            else -> 16f
        }

        return when (theme) {

            MeadowThemeVariant.Lavender -> FieldStyleTokens(
                fieldSurface = MeadowBrand.Lavender75,
                sectionSurface = MeadowBrand.Lavender100,
                imageSurface = MeadowBrand.Lavender50,

                borderIdle = MeadowBrand.Lavender200,
                borderFocused = MeadowBrand.Lavender400,

                textPrimary      = MeadowBrand.Lavender800,
                textSecondary    = MeadowBrand.Lavender600,
                textPlaceholder  = MeadowBrand.Lavender450,
                textDisabled     = MeadowBrand.Lavender300,

                selectSurface = MeadowBrand.Lavender150,
                selectBorder  = MeadowBrand.Lavender300,
                selectText    = MeadowBrand.Lavender500,

                controlUnchecked = MeadowBrand.Lavender300,
                controlChecked   = MeadowBrand.Lavender500,
                controlDisabled  = MeadowBrand.Lavender200,

                radiusField = baseRadius,
                radiusTextarea = baseRadius + 4f,
                radiusImage = 18f,

                paddingCompact = 10.dp,
                paddingComfortable = 14.dp,
                paddingExpressive = 18.dp,

                minHeightCompact = 48.dp,
                minHeightComfortable = 96.dp,
                minHeightExpressive = 160.dp,

                placeholderAlpha = 0.6f,
                dividerAlpha = 0.15f
            )

            MeadowThemeVariant.Blush -> FieldStyleTokens(
                fieldSurface = MeadowBrand.Blush75,
                sectionSurface = MeadowBrand.Blush150,
                imageSurface = MeadowBrand.Blush50,

                borderIdle = MeadowBrand.Blush200,
                borderFocused = MeadowBrand.Blush400,

                radiusField = baseRadius,
                radiusTextarea = baseRadius + 4f,
                radiusImage = 18f,

                textPrimary      = MeadowBrand.Blush500,
                textSecondary    = MeadowBrand.Blush400,
                textPlaceholder  = MeadowBrand.Blush300,
                textDisabled     = MeadowBrand.Blush200,

                selectSurface = MeadowBrand.Blush150,
                selectBorder = MeadowBrand.Blush300,
                selectText = MeadowBrand.Blush500,

                controlUnchecked = MeadowBrand.Blush300,
                controlChecked   = MeadowBrand.Blush500,
                controlDisabled  = MeadowBrand.Blush200,

                paddingCompact = 10.dp,
                paddingComfortable = 14.dp,
                paddingExpressive = 18.dp,

                minHeightCompact = 48.dp,
                minHeightComfortable = 96.dp,
                minHeightExpressive = 160.dp,

                placeholderAlpha = 0.6f,
                dividerAlpha = 0.15f
            )

            MeadowThemeVariant.Periwinkle -> FieldStyleTokens(
                fieldSurface = MeadowBrand.Periwinkle75,
                sectionSurface = MeadowBrand.Periwinkle150,
                imageSurface = MeadowBrand.Periwinkle50,

                borderIdle = MeadowBrand.Periwinkle200,
                borderFocused = MeadowBrand.Periwinkle400,

                radiusField = baseRadius,
                radiusTextarea = baseRadius + 4f,
                radiusImage = 18f,

                textPrimary      = MeadowBrand.Periwinkle500,
                textSecondary    = MeadowBrand.Periwinkle450,
                textPlaceholder  = MeadowBrand.Periwinkle300,
                textDisabled     = MeadowBrand.Periwinkle200,

                selectSurface = MeadowBrand.Periwinkle150,
                selectBorder  = MeadowBrand.Periwinkle300,
                selectText    = MeadowBrand.Periwinkle500,

                controlUnchecked = MeadowBrand.Periwinkle300,
                controlChecked   = MeadowBrand.Periwinkle500,
                controlDisabled  = MeadowBrand.Periwinkle200,

                paddingCompact = 10.dp,
                paddingComfortable = 14.dp,
                paddingExpressive = 18.dp,

                minHeightCompact = 48.dp,
                minHeightComfortable = 96.dp,
                minHeightExpressive = 160.dp,

                placeholderAlpha = 0.6f,
                dividerAlpha = 0.15f
            )

            MeadowThemeVariant.Mint -> FieldStyleTokens(
                fieldSurface = MeadowBrand.Mint75,
                sectionSurface = MeadowBrand.Mint150,
                imageSurface = MeadowBrand.Mint50,

                borderIdle = MeadowBrand.Mint200,
                borderFocused = MeadowBrand.Mint400,

                radiusField = baseRadius,
                radiusTextarea = baseRadius + 4f,
                radiusImage = 18f,

                textPrimary      = MeadowBrand.Mint500,
                textSecondary    = MeadowBrand.Mint400,
                textPlaceholder  = MeadowBrand.Mint300,
                textDisabled     = MeadowBrand.Mint200,

                selectSurface = MeadowBrand.Mint150,
                selectBorder  = MeadowBrand.Mint300,
                selectText    = MeadowBrand.Mint500,

                controlUnchecked = MeadowBrand.Mint300,
                controlChecked   = MeadowBrand.Mint500,
                controlDisabled  = MeadowBrand.Mint200,

                paddingCompact = 10.dp,
                paddingComfortable = 14.dp,
                paddingExpressive = 18.dp,

                minHeightCompact = 48.dp,
                minHeightComfortable = 96.dp,
                minHeightExpressive = 160.dp,

                placeholderAlpha = 0.6f,
                dividerAlpha = 0.15f
            )

            MeadowThemeVariant.Peach -> FieldStyleTokens(
                fieldSurface = MeadowBrand.Peach75,
                sectionSurface = MeadowBrand.Peach150,
                imageSurface = MeadowBrand.Peach50,

                borderIdle = MeadowBrand.Peach200,
                borderFocused = MeadowBrand.Peach400,

                radiusField = baseRadius,
                radiusTextarea = baseRadius + 4f,
                radiusImage = 18f,

                textPrimary      = MeadowBrand.Peach550,
                textSecondary    = MeadowBrand.Peach400,
                textPlaceholder  = MeadowBrand.Peach300,
                textDisabled     = MeadowBrand.Peach200,

                selectSurface = MeadowBrand.Peach150,
                selectBorder  = MeadowBrand.Peach300,
                selectText    = MeadowBrand.Peach550,

                controlUnchecked = MeadowBrand.Peach300,
                controlChecked   = MeadowBrand.Peach500,
                controlDisabled  = MeadowBrand.Peach200,

                paddingCompact = 10.dp,
                paddingComfortable = 14.dp,
                paddingExpressive = 18.dp,

                minHeightCompact = 48.dp,
                minHeightComfortable = 96.dp,
                minHeightExpressive = 160.dp,

                placeholderAlpha = 0.6f,
                dividerAlpha = 0.15f
            )

            MeadowThemeVariant.Cream -> FieldStyleTokens(
                fieldSurface = MeadowBrand.Cream75,
                sectionSurface = MeadowBrand.Cream150,
                imageSurface = MeadowBrand.Cream50,

                borderIdle = MeadowBrand.Cream200,
                borderFocused = MeadowBrand.Cream400,

                radiusField = baseRadius,
                radiusTextarea = baseRadius + 4f,
                radiusImage = 18f,

                textPrimary      = MeadowBrand.Cream450,
                textSecondary    = MeadowBrand.Cream350,
                textPlaceholder  = MeadowBrand.Cream300,
                textDisabled     = MeadowBrand.Cream200,

                selectSurface = MeadowBrand.Cream150,
                selectBorder  = MeadowBrand.Cream300,
                selectText    = MeadowBrand.Cream450,

                controlUnchecked = MeadowBrand.Cream300,
                controlChecked   = MeadowBrand.Cream50,
                controlDisabled  = MeadowBrand.Cream200,

                paddingCompact = 10.dp,
                paddingComfortable = 14.dp,
                paddingExpressive = 18.dp,

                minHeightCompact = 48.dp,
                minHeightComfortable = 96.dp,
                minHeightExpressive = 160.dp,

                placeholderAlpha = 0.6f,
                dividerAlpha = 0.15f
            )
        }
    }
}