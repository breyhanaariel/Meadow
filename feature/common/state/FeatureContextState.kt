package com.meadow.feature.common.state

import androidx.lifecycle.ViewModel
import com.meadow.feature.common.api.FeatureContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class KebabAction(
    val label: String,
    val onClick: () -> Unit
)

@HiltViewModel
class FeatureContextState @Inject constructor() : ViewModel() {

    private val _context = MutableStateFlow(FeatureContext())
    val context: StateFlow<FeatureContext> = _context.asStateFlow()

    private val _kebabActions = MutableStateFlow<List<KebabAction>>(emptyList())
    val kebabActions: StateFlow<List<KebabAction>> = _kebabActions.asStateFlow()

    fun setContext(context: FeatureContext) {
        _context.value = context
    }

    fun clearProject() {
        _context.value = _context.value.copy(
            projectId = null,
            scriptId = null,
            catalogItemId = null
        )
        clearKebabActions()
    }

    fun setKebabActions(actions: List<KebabAction>) {
        _kebabActions.value = actions
    }

    fun clearKebabActions() {
        _kebabActions.value = emptyList()
    }
}
