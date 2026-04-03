package com.meadow.core.ai.ui.settings

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meadow.core.ai.R
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ai.ui.components.AiContextEditor
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.MeadowButton
import com.meadow.core.utils.files.JsonUtils
import kotlinx.serialization.json.*



class AiSettingsVM(app: Application) : AndroidViewModel(app) {

    private val context = app.applicationContext
    private val assetsPath = "prompts.json"
    private val userPath = "prompts_user.json"

    var corePrompts by mutableStateOf<Map<AiPersona, String>>(emptyMap())
        private set

    init { load() }

    private fun load() {
        val userJson =
            JsonUtils.readJson<Map<String, JsonElement>>(context, userPath)

        val json =
            userJson ?: JsonUtils.readJson(context, assetsPath) ?: return

        val coreObj = json["core_prompts"]?.jsonObject ?: return

        corePrompts =
            coreObj.mapValues { it.value.jsonPrimitive.content }
                .mapKeys { (key, _) ->
                    AiPersona.values()
                        .firstOrNull { it.name.equals(key, true) }
                        ?: AiPersona.Sprout
                }
    }

    fun updatePrompt(persona: AiPersona, text: String) {
        corePrompts = corePrompts + (persona to text)
    }

    fun resetPersona(persona: AiPersona) {
        val assets =
            JsonUtils.readJson<Map<String, JsonElement>>(context, assetsPath)
                ?: return

        val default =
            assets["core_prompts"]
                ?.jsonObject
                ?.get(persona.name)
                ?.jsonPrimitive
                ?.content
                ?: return

        corePrompts = corePrompts + (persona to default)
        save()
    }

    fun save() {
        val json = mapOf(
            "core_prompts" to corePrompts.mapKeys { it.key.name }
        )
        JsonUtils.saveJson(context, userPath, json)
    }
}


@Composable
fun AiSettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: AiSettingsVM = viewModel()
) {

    var selectedMode by remember { mutableStateOf<SettingsMode>(SettingsMode.Persona(AiPersona.Sprout)) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        /* ───── CHIP DROPDOWN ───── */

        Box {

            AssistChip(
                onClick = { dropdownExpanded = true },
                label = {
                    Text(
                        when (val mode = selectedMode) {
                            is SettingsMode.Persona -> mode.persona.displayName
                            SettingsMode.ContextLibrary ->
                                stringResource(R.string.ai_context_library)
                        }
                    )
                }
            )

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {

                AiPersona.values().forEach { persona ->
                    DropdownMenuItem(
                        text = { Text(persona.displayName) },
                        onClick = {
                            selectedMode = SettingsMode.Persona(persona)
                            dropdownExpanded = false
                        }
                    )
                }

                Divider()

                DropdownMenuItem(
                    text = {
                        Text(stringResource(R.string.ai_context_library))
                    },
                    onClick = {
                        selectedMode = SettingsMode.ContextLibrary
                        dropdownExpanded = false
                    }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        when (val mode = selectedMode) {

            is SettingsMode.ContextLibrary -> {
                AiContextEditor(
                    scopeKey = null,
                    onDismiss = { }
                )
            }

            is SettingsMode.Persona -> {

                Text(
                    text = mode.persona.displayName,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(12.dp))

                var text by remember(mode.persona) {
                    mutableStateOf(
                        viewModel.corePrompts[mode.persona] ?: ""
                    )
                }

                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        viewModel.updatePrompt(mode.persona, it)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = {
                        Text(
                            stringResource(R.string.ai_settings_core_prompt_label)
                        )
                    },
                    minLines = 12,
                    maxLines = Int.MAX_VALUE
                )

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    MeadowButton(
                        text = stringResource(R.string.ai_settings_reset_default),
                        onClick = { viewModel.resetPersona(mode.persona) }
                    )

                    MeadowButton(
                        text = stringResource(CoreUiR.string.action_save),
                        onClick = { viewModel.save() }
                    )
                }
            }
        }
    }
}


private sealed class SettingsMode {
    data class Persona(val persona: AiPersona) : SettingsMode()
    object ContextLibrary : SettingsMode()
}
