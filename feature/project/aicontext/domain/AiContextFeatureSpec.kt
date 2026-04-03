package com.meadow.feature.aicontext.domain

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.meadow.core.ui.R as CoreUiR
import com.meadow.feature.project.R as R
import com.meadow.feature.project.domain.model.ProjectFeatureSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiContextFeatureSpec @Inject constructor() : ProjectFeatureSpec {

    override val key: String = "ai_context"

    override val titleRes: Int = R.string.ai_context_title

    override val icon: @Composable () -> Unit = {
        Icon(
            painter = painterResource(CoreUiR.drawable.ic_ai),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }

    override fun routeForProject(projectId: String): String {
        return "project/$projectId/ai-context"
    }
}
