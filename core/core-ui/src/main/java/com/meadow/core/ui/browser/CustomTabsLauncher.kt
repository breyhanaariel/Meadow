package com.meadow.core.ui.browser

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

object CustomTabsLauncher {

    fun open(context: Context, url: String) {
        val intent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()

        intent.launchUrl(context, Uri.parse(url))
    }
}
