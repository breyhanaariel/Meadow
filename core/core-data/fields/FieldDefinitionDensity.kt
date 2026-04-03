package com.meadow.core.data.fields

fun FieldDefinition.density(): FieldDensity =
    when (kind) {
        FieldKind.TEXT,
        FieldKind.NUMBER,
        FieldKind.BOOLEAN,
        FieldKind.DATE,
        FieldKind.TIME,
        FieldKind.DATETIME,
        FieldKind.EMAIL,
        FieldKind.PHONE,
        FieldKind.CURRENCY,
        FieldKind.SINGLE_SELECT,
        FieldKind.MULTI_SELECT,
        FieldKind.TAGS,
        FieldKind.RELATION,
        FieldKind.REFERENCE,
        FieldKind.COLOR,
        FieldKind.RATING,
        FieldKind.URL ->
            FieldDensity.Compact
        FieldKind.MULTILINE_TEXT,
        FieldKind.LOCATION ->
            FieldDensity.Comfortable
        FieldKind.IMAGE,
        FieldKind.VIDEO,
        FieldKind.AUDIO,
        FieldKind.PDF,
        FieldKind.FILE,
        FieldKind.GROUP,
        FieldKind.SUBTITLE ->
            FieldDensity.Expressive
    }
