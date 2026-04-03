package com.meadow.feature.project.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarHostState
import com.meadow.core.data.fields.FieldValue
import com.meadow.core.data.fields.FieldWithValue
import com.meadow.core.ui.components.MeadowField

/* ─── AI FIELD HOST ───────────────────── */

@Composable
fun AiFieldHost(
    scope: AiFieldHelperScope,
    fields: List<FieldWithValue>,
    snackbarHostState: SnackbarHostState,
    onApplyFieldValue: (FieldValue) -> Unit,
    onOpenChats: () -> Unit,
    onOpenContextEditor: () -> Unit,
    content: @Composable (AiFieldHelperController) -> Unit
) {

    val controller = rememberAiFieldHelperController(
        scope = scope,
        fields = fields,
        snackbarHostState = snackbarHostState,
        onApplyFieldValue = onApplyFieldValue,
        onOpenChats = onOpenChats,
        onOpenContextEditor = onOpenContextEditor
    )

    Box {

        content(controller)

        AiFabMenu(
            visible = true,
            onAction = { action ->
                when (action) {
                    AiFabAction.Generate -> controller.generate()
                    AiFabAction.Improve -> controller.improve()
                    AiFabAction.Rewrite -> controller.rewrite()
                    AiFabAction.Expand -> controller.expand()
                    AiFabAction.Shorten -> controller.shorten()
                    AiFabAction.ManageContext -> controller.openContext()
                    AiFabAction.OpenChats -> controller.openChats()
                }
            }
        )
    }
}

/* ─── AI AWARE FIELD ───────────────────── */

@Composable
fun AiAwareField(
    field: FieldWithValue,
    controller: AiFieldHelperController,
    onValueChange: (FieldValue) -> Unit
) {

    MeadowField(
        field = field,
        onValueChange = onValueChange
    )

    LaunchedEffect(field.definition.id) {
        controller.onFieldFocused(field)
    }
}