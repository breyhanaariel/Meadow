package com.meadow.core.google.api.drive.internal

object DriveAppPropertiesQuery {

    fun build(appProperties: Map<String, String>): String {
        return appProperties.entries.joinToString(" and ") { (k, v) ->
            "appProperties has { key='$k' and value='$v' }"
        }
    }
}
