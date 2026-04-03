package com.meadow.core.ai.ui.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.meadow.core.ai.R as R
import com.meadow.core.ui.R as CoreUiR
import kotlin.math.roundToInt

class MeadowTextToolbar(
    private val controller: SelectionAiController,
    private val scopeKeyProvider: () -> String?,
    private val valueProvider: () -> TextFieldValue,
    private val onValueChange: (TextFieldValue) -> Unit
) : TextToolbar {

    override var status: TextToolbarStatus = TextToolbarStatus.Hidden
        private set

    internal var isMenuVisible by mutableStateOf(false)
        private set

    internal var anchorRect: Rect? by mutableStateOf(null)
        private set

    private var onCopyRequested: (() -> Unit)? = null
    private var onPasteRequested: (() -> Unit)? = null
    private var onCutRequested: (() -> Unit)? = null
    private var onSelectAllRequested: (() -> Unit)? = null

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
        status = TextToolbarStatus.Shown
        anchorRect = rect
        isMenuVisible = true

        this.onCopyRequested = onCopyRequested
        this.onPasteRequested = onPasteRequested
        this.onCutRequested = onCutRequested
        this.onSelectAllRequested = onSelectAllRequested
    }

    override fun hide() {
        status = TextToolbarStatus.Hidden
        isMenuVisible = false
        anchorRect = null
    }

    internal fun invokeCopy() = onCopyRequested?.invoke()
    internal fun invokePaste() = onPasteRequested?.invoke()
    internal fun invokeCut() = onCutRequested?.invoke()
    internal fun invokeSelectAll() = onSelectAllRequested?.invoke()

    internal fun invokeAi(action: TextSelectionAiAction) {
        val current = valueProvider()
        controller.open(
            action = action,
            textFieldValue = current,
            scopeKey = scopeKeyProvider()
        )
        hide()
    }
}

@Composable
fun MeadowTextToolbarHost(
    toolbar: MeadowTextToolbar
) {
    if (!toolbar.isMenuVisible) return

    val rect = toolbar.anchorRect ?: return
    val density = LocalDensity.current

    val offset = with(density) {
        IntOffset(
            x = rect.left.roundToInt(),
            y = rect.bottom.roundToInt()
        )
    }

    Popup(
        offset = offset,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = { toolbar.hide() }
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
                    .widthIn(min = 220.dp)
            ) {

                MenuItem(stringResource(CoreUiR.string.action_cut)) {
                    toolbar.invokeCut(); toolbar.hide()
                }
                MenuItem(stringResource(CoreUiR.string.action_copy)) {
                    toolbar.invokeCopy(); toolbar.hide()
                }
                MenuItem(stringResource(CoreUiR.string.action_paste)) {
                    toolbar.invokePaste(); toolbar.hide()
                }
                MenuItem(stringResource(CoreUiR.string.action_select_all)) {
                    toolbar.invokeSelectAll(); toolbar.hide()
                }

                SpacerDivider()

                MenuItem(stringResource(R.string.ai_action_sprout)) {
                    toolbar.invokeAi(TextSelectionAiAction.SproutIdeas)
                }
                MenuItem(stringResource(R.string.ai_action_vine)) {
                    toolbar.invokeAi(TextSelectionAiAction.DraftWithVine)
                }
                MenuItem(stringResource(R.string.ai_action_petal)) {
                    toolbar.invokeAi(TextSelectionAiAction.CritiqueWithPetal)
                }
                MenuItem(stringResource(R.string.ai_action_bloom)) {
                    toolbar.invokeAi(TextSelectionAiAction.RewriteWithBloom)
                }
            }
        }
    }
}

@Composable
private fun MenuItem(
    label: String,
    onClick: () -> Unit
) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    )
}

@Composable
private fun SpacerDivider() {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 6.dp))
}