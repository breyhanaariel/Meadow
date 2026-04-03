package com.meadow.core.ai.ui.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.meadow.core.ai.R as R
import com.meadow.core.ui.R as CoreUiR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextSelectionAiSheet(
    controller: SelectionAiController,
    textFieldValue: TextFieldValue,
    onApply: (TextFieldValue) -> Unit
) {
    if (!controller.showSheet) return

    ModalBottomSheet(
        onDismissRequest = { controller.dismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                Text(
                    text = stringResource(R.string.ai_selection_result_title),
                    style = MaterialTheme.typography.titleMedium
                )

            if (controller.loading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = stringResource(
                        R.string.ai_selection_failed,
                        controller.aiResult ?: stringResource(CoreUiR.string.unknown_error)
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            val updated = controller.applyReplace(textFieldValue)
                            onApply(updated)
                        }
                    ) {
                        Text(stringResource(R.string.ai_selection_replace))
                    }

                    OutlinedButton(
                        onClick = {
                            val updated = controller.applyInsertBelow(textFieldValue)
                            onApply(updated)
                        }
                    ) {
                        Text(stringResource(R.string.ai_selection_insert_below))
                    }
                }
            }
        }
    }
}