package com.meadow.core.ai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ai.ui.settings.AiSettingsVM
import com.meadow.core.ui.components.MeadowButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaPromptBottomSheet(
    persona: AiPersona,
    onDismiss: () -> Unit,
    viewModel: AiSettingsVM = viewModel()
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        var text by remember(persona) {
            mutableStateOf(viewModel.corePrompts[persona] ?: "")
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "${persona.displayName} Prompt",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    viewModel.updatePrompt(persona, it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MeadowButton(
                    text = "Reset",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.resetPersona(persona)
                        text = viewModel.corePrompts[persona] ?: ""
                    }
                )

                MeadowButton(
                    text = "Save",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.save()
                        onDismiss()
                    }
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
