package com.meadow.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.app.domain.usecase.GenerateIdeasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AiViewModel.kt
 *
 * Bridges Gemini API prompts (Sprout, Bloom, Meadow) with the UI.
 */

@HiltViewModel
class AiViewModel @Inject constructor(
    private val generateIdeas: GenerateIdeasUseCase
) : ViewModel() {

    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse

    fun generate(category: String, topic: String) {
        viewModelScope.launch {
            _aiResponse.value = generateIdeas(category, topic)
        }
    }
}
