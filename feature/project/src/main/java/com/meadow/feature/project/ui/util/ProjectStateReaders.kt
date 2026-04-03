package com.meadow.feature.project.ui.util

import com.meadow.feature.project.domain.model.Project

/* ─── PROJECT STATE READERS ─────────────────── */

fun Project.isArchived(): Boolean =
    fields.readFirstTextByKeys(
        "archived",
        "field_project_archived"
    ) == "true"

fun Project.isCompleted(): Boolean =
    fields.readFirstTextByKeys(
        "completed",
        "field_project_completed"
    ) == "true"