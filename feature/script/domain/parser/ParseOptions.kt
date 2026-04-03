package com.meadow.feature.script.domain.parser

data class ParseOptions(
    val strict: Boolean,
    val extensionsEnabled: Boolean,
    val enabledExtensions: Set<String>
) {
    companion object {
        fun strictDefaults(): ParseOptions {
            return ParseOptions(
                strict = true,
                extensionsEnabled = false,
                enabledExtensions = emptySet()
            )
        }
    }
}
