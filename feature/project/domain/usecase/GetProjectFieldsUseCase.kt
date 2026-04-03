package com.meadow.feature.project.domain.usecase

import com.meadow.core.data.fields.FieldWithValue

class GetProjectFieldsUseCase {
    operator fun invoke(fields: List<FieldWithValue>): List<FieldWithValue> =
        fields.sortedBy { it.definition.order }
}
