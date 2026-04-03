package com.meadow.core.data.fields.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.meadow.core.data.fields.FieldDefinition
import com.meadow.core.data.fields.FieldKind

@Entity(tableName = "field_definitions")
data class FieldDefinitionEntity(
    @PrimaryKey val id: String,
    val owner: String,
    val key: String,
    val labelKey: String,
    val hintKey: String?,
    val descriptionKey: String?,
    val kind: FieldKind,
    val group: String?,
    val order: Int,
    val isRequired: Boolean,
    val isReadOnly: Boolean,
    val defaultValue: String?,
    val metadataJson: String
) {

    fun toDomain(
        metadataParser: (String) -> Map<String, String>
    ): FieldDefinition =
        FieldDefinition(
            id = id,
            owner = owner,
            key = key,
            labelKey = labelKey,
            hintKey = hintKey,
            descriptionKey = descriptionKey,
            kind = kind,
            group = group,
            order = order,
            isRequired = isRequired,
            isReadOnly = isReadOnly,
            defaultValue = defaultValue,
            metadata = metadataParser(metadataJson)
        )
}
