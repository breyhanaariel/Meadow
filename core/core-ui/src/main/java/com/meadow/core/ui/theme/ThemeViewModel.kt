package com.meadow.core.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _theme = MutableStateFlow(MeadowThemeVariant.Lavender)
    val theme: StateFlow<MeadowThemeVariant> = _theme

    private val _effects = MutableStateFlow(EffectSettings())
    val effects: StateFlow<EffectSettings> = _effects

    init {
        ThemePersistence.themeFlow(context)
            .onEach { _theme.value = it }
            .launchIn(viewModelScope)

        ThemePersistence.effectFlow(context)
            .onEach { _effects.value = it }
            .launchIn(viewModelScope)
    }

    fun setTheme(theme: MeadowThemeVariant) = viewModelScope.launch {
        ThemePersistence.saveTheme(context, theme)
    }

    fun setSoftGlow(v: Boolean) = updateEffect { it.copy(enableSoftGlow = v) }
    fun setGlitter(v: Boolean) = updateEffect { it.copy(enableGlitter = v) }
    fun setPetals(v: Boolean) = updateEffect { it.copy(enablePetals = v) }
    fun setFloatingParticles(v: Boolean) = updateEffect { it.copy(enableFloatingParticles = v) }

    fun setFieldCornerStyle(style: Int) = updateEffect { it.copy(fieldCornerStyle = style) }
    fun setFieldFillStyle(style: Int) = updateEffect { it.copy(fieldFillStyle = style) }

    private fun updateEffect(block: (EffectSettings) -> EffectSettings) {
        val updated = block(_effects.value)
        _effects.value = updated

        viewModelScope.launch {
            ThemePersistence.saveEffect(context, updated)
        }
    }
}
