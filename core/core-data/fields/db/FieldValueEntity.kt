package com.meadow.core.data.fields.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.meadow.core.data.fields.FieldValue

@Entity(tableName = "field_values")
data class FieldValueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val fieldId: String,
    val ownerItemId: String,
    val rawValue: String?,
    val extraJson: String,
    val parentFieldId: String?
) {
    fun toDomain(extraParser: (String) -> Map<String, String>): FieldValue =
        FieldValue(
            fieldId = fieldId,
            ownerItemId = ownerItemId,
            rawValue = rawValue,
            extra = extraParser(extraJson),
            parentFieldId = parentFieldId
        )
}
