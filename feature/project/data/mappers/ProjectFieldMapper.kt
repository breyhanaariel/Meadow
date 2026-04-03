package com.meadow.feature.project.data.mappers

import com.meadow.core.data.fields.FieldValue

object ProjectFieldMapper {

    fun text(
        projectId: String,
        fieldId: String,
        value: String?
    ): FieldValue =
        FieldValue(
            fieldId = fieldId,
            ownerItemId = projectId,
            rawValue = value
        )


    fun singleSelect(
        projectId: String,
        fieldId: String,
        selected: String?
    ): FieldValue =
        FieldValue(
            fieldId = fieldId,
            ownerItemId = projectId,
            rawValue = selected
        )


    fun multiSelect(
        projectId: String,
        fieldId: String,
        values: List<String>
    ): FieldValue =
        FieldValue(
            fieldId = fieldId,
            ownerItemId = projectId,
            rawValue = values.joinToString(",")
        )


    fun empty(projectId: String, fieldId: String): FieldValue =
        FieldValue(
            fieldId = fieldId,
            ownerItemId = projectId,
            rawValue = null
        )
}
