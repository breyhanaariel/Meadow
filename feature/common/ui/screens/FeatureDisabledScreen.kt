package com.meadow.feature.common.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meadow.core.ui.R as CoreUiR


@Composable
fun FeatureDisabledScreen(
    featureName: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(
                CoreUiR.string.feature_disabled_message,
                featureName
            )
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = onBack) {
            Text(stringResource(CoreUiR.string.action_go_back))
        }

    }
}
