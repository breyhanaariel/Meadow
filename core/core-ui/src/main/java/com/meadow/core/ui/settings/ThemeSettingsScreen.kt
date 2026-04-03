package com.meadow.core.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meadow.core.ui.R
import com.meadow.core.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun ThemeSettingsScreen() {

    val themeViewModel: ThemeViewModel = viewModel()

    val currentTheme by themeViewModel.theme.collectAsState()
    val effects by themeViewModel.effects.collectAsState()

    val themeList = MeadowThemeVariant.values()
    val currentIndex = themeList.indexOf(currentTheme)
    var sliderValue by remember { mutableStateOf(currentIndex.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = stringResource(R.string.theme_settings_select_theme),
            style = MaterialTheme.typography.bodyMedium
        )

        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = {
                val snapped = sliderValue.roundToInt().coerceIn(0, themeList.lastIndex)
                sliderValue = snapped.toFloat()
                themeViewModel.setTheme(themeList[snapped])
            },
            valueRange = 0f..themeList.lastIndex.toFloat(),
            steps = themeList.lastIndex - 1,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = themeList[sliderValue.roundToInt()].readableName(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Divider()

        Text(
            text = stringResource(R.string.theme_settings_ui_effects),
            style = MaterialTheme.typography.titleMedium
        )

        EffectToggle(
            label = stringResource(R.string.effect_soft_glow),
            value = effects.enableSoftGlow,
            onToggle = themeViewModel::setSoftGlow
        )
        EffectToggle(
            stringResource(R.string.effect_glitter),
            effects.enableGlitter,
            themeViewModel::setGlitter
        )
        EffectToggle(
            stringResource(R.string.effect_petals),
            effects.enablePetals,
            themeViewModel::setPetals
        )
        EffectToggle(
            stringResource(R.string.effect_floating_particles),
            effects.enableFloatingParticles,
            themeViewModel::setFloatingParticles
        )

        Divider()


        Text(
            text = stringResource(id = R.string.field_appearance),            style = MaterialTheme.typography.titleMedium
        )

        Text(text = stringResource(id = R.string.corner_style), style = MaterialTheme.typography.bodyLarge)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { themeViewModel.setFieldCornerStyle(1) },
                enabled = effects.fieldCornerStyle != 1
            ) { Text(text = stringResource(id = R.string.pill)) }
            Button(
                onClick = { themeViewModel.setFieldCornerStyle(2) },
                enabled = effects.fieldCornerStyle != 2
            ) { Text(text = stringResource(id = R.string.rounded)) }        }

        Spacer(Modifier.height(12.dp))

        Text(text = stringResource(id = R.string.fill_style), style = MaterialTheme.typography.bodyLarge)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { themeViewModel.setFieldFillStyle(1) },
                enabled = effects.fieldFillStyle != 1
            ) { Text(text = stringResource(id = R.string.solid)) }
            Button(
                onClick = { themeViewModel.setFieldFillStyle(2) },
                enabled = effects.fieldFillStyle != 2
            ) { Text(text = stringResource(id = R.string.gradient)) }        }
    }
}

@Composable
private fun EffectToggle(label: String, value: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = value, onCheckedChange = onToggle)
    }
}

@Composable
fun MeadowThemeVariant.readableName(): String =
    when (this) {
        MeadowThemeVariant.Lavender -> stringResource(R.string.theme_lavender)
        MeadowThemeVariant.Blush -> stringResource(R.string.theme_blush)
        MeadowThemeVariant.Periwinkle -> stringResource(R.string.theme_periwinkle)
        MeadowThemeVariant.Mint -> stringResource(R.string.theme_mint)
        MeadowThemeVariant.Peach -> stringResource(R.string.theme_peach)
        MeadowThemeVariant.Cream -> stringResource(R.string.theme_cream)
    }
