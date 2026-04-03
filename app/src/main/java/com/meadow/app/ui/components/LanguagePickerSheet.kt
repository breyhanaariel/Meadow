package com.meadow.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.locale.AppLanguage

@Composable
fun LanguagePickerSheet(
    current: AppLanguage,
    onSelect: (AppLanguage) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        AppLanguage.values().forEach { language ->
            ListItem(
                headlineContent = { Text(language.label) },
                trailingContent = {
                    if (language == current) {
                        Icon(Icons.Default.Check, null)
                    }
                },
                modifier = Modifier.clickable {
                    onSelect(language)
                }
            )
        }
    }
}
