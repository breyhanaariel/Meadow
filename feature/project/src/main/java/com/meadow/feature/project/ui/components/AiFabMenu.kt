package com.meadow.feature.project.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.R as CoreUiR
import com.meadow.feature.project.R as R

sealed class AiFabAction {
    data object Generate : AiFabAction()
    data object Improve : AiFabAction()
    data object Rewrite : AiFabAction()
    data object Expand : AiFabAction()
    data object Shorten : AiFabAction()
    data object ManageContext : AiFabAction()
    data object OpenChats : AiFabAction()
}

@Composable
fun AiFabMenu(
    visible: Boolean = true,
    onAction: (AiFabAction) -> Unit
) {
    if (!visible) return

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(16.dp)
        ) {
            AnimatedVisibility(expanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    ActionButton(label = stringResource(id = R.string.ai_mode_generate)) {
                        expanded = false
                        onAction(AiFabAction.Generate)
                    }

                    ActionButton(label = stringResource(id = R.string.ai_mode_improve)) {
                        expanded = false
                        onAction(AiFabAction.Improve)
                    }

                    ActionButton(label = stringResource(id = R.string.ai_mode_rewrite)) {
                        expanded = false
                        onAction(AiFabAction.Rewrite)
                    }
                    ActionButton(label = stringResource(id = R.string.ai_mode_expand)) {
                        expanded = false
                        onAction(AiFabAction.Expand)
                    }

                    ActionButton(label = stringResource(id = R.string.ai_mode_shorten)) {
                        expanded = false
                        onAction(AiFabAction.Shorten)
                    }
                    ActionButton(label = stringResource(id = R.string.ai_action_manage_context)) {
                        expanded = false
                        onAction(AiFabAction.ManageContext)
                    }

                    ActionButton(label = stringResource(id = R.string.ai_action_open_chats)) {
                        expanded = false
                        onAction(AiFabAction.OpenChats)
                    }
                }
            }

            Image(
                painter = painterResource(id = CoreUiR.drawable.ic_ai),
                contentDescription = null,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 4.dp
    ) {
        TextButton(onClick = onClick) {
            Text(text = label)
        }
    }
}
