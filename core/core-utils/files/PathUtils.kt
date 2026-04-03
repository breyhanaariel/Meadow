package com.meadow.core.utils.files

fun String.asSafeFileName(): String =
    replace("[^A-Za-z0-9_.-]".toRegex(), "_")