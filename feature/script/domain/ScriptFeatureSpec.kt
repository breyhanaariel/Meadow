package com.meadow.feature.script.domain

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.meadow.feature.project.domain.model.ProjectFeatureSpec
import com.meadow.feature.script.R
import com.meadow.feature.script.ui.navigation.ScriptRoutes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScriptFeatureSpec @Inject constructor() : ProjectFeatureSpec {

    override val key: String = "script"

    override val titleRes: Int = R.string.feature_script

    override val icon: @Composable () -> Unit = {
        Icon(
            painter = painterResource(R.drawable.ic_script),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }

    override fun routeForProject(projectId: String): String {
        return ScriptRoutes.projectScripts(projectId)
    }
}
