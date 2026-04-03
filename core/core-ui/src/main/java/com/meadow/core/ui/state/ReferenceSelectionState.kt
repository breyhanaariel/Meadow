package com.meadow.core.ui.state

import com.meadow.core.data.fields.FieldWithValue

data class ReferenceSelectionState(
    val selectedIds: Set<String>
) {
    companion object {
        fun from(field: FieldWithValue, multi: Boolean): ReferenceSelectionState {
            val raw = field.value?.rawValue.orEmpty()

            val ids =
                raw.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .toSet()

            return ReferenceSelectionState(
                selectedIds = if (multi) ids else ids.take(1).toSet()
            )
        }
    }

    fun serialize(): String =
        selectedIds.joinToString(",")
}