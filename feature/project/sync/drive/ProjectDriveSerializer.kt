package com.meadow.feature.project.sync.drive

import com.google.gson.Gson
import com.meadow.feature.project.domain.model.Project
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectDriveSerializer @Inject constructor(
    private val gson: Gson
) {

    fun serialize(project: Project): String =
        gson.toJson(project)

    fun deserialize(json: String): List<Project> =
        gson.fromJson(json, Array<Project>::class.java).toList()
}
