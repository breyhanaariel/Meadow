package com.meadow.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Brush

val LocalIconGradient = staticCompositionLocalOf<Brush> {
    MeadowGradients.iconGradientFor(MeadowThemeVariant.Lavender)
}

val LocalWordmarkGradient = staticCompositionLocalOf<Brush> {
    MeadowGradients.wordmarkGradientFor(MeadowThemeVariant.Lavender)
}

val LocalMeadowThemeVariant = staticCompositionLocalOf<MeadowThemeVariant> {
    MeadowThemeVariant.Lavender
}


@Composable
fun MeadowTheme(content: @Composable () -> Unit) {

    val vm: ThemeViewModel = viewModel()

    val theme = vm.theme.collectAsState().value
    val effects = vm.effects.collectAsState().value

    MeadowFieldTokens.current = ThemeTokens.fieldTokensFor(theme, effects)

    val statusTokens = ThemeTokens.statusTokensFor(theme)

    MaterialTheme(
        colorScheme = ThemeTokens.colorSchemeFor(theme),
        typography = MeadowTypography,
        shapes = MeadowShapes.materialShapes(),
    ) {
        val iconGradient = MeadowGradients.iconGradientFor(theme)
        val wordmarkGradient = MeadowGradients.wordmarkGradientFor(theme)

        CompositionLocalProvider(
            LocalMeadowThemeVariant provides theme,
            LocalMeadowStatusTokens provides statusTokens,
            LocalIconGradient provides iconGradient,
            LocalWordmarkGradient provides wordmarkGradient
        ) {
            MeadowVisualEffects(settings = effects) {
                ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                    content()
                }
            }
        }
    }
}
