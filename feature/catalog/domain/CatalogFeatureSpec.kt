package com.meadow.feature.catalog.domain

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.meadow.core.ui.R as CoreUiR
import com.meadow.feature.catalog.R
import com.meadow.feature.catalog.ui.navigation.CatalogRoutes
import com.meadow.feature.project.domain.model.ProjectFeatureSpec
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CatalogFeatureSpec @Inject constructor() : ProjectFeatureSpec {

    override val key: String = "catalog"

    override val titleRes: Int = CoreUiR.string.feature_catalog

    override val icon: @Composable () -> Unit = {
        Icon(
            painter = painterResource(R.drawable.ic_catalog),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }

    override fun routeForProject(projectId: String): String {
        return CatalogRoutes.projectCatalog(projectId)
    }
}