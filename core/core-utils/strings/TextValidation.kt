package com.meadow.core.utils.strings

object TextValidation {
    fun isEmail(text: String): Boolean =
        text.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
}