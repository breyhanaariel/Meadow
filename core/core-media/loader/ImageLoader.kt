package com.meadow.core.media.loader

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.meadow.core.media.R

@Composable
fun ImageLoader(
    url: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    description: String? = null
) {
    if (url.isNullOrBlank()) {
        Text(stringResource(R.string.media_image_failed))
        return
    }

    val painter = rememberAsyncImagePainter(model = url)
    val state = painter.state

    when (state) {
        is AsyncImagePainter.State.Loading ->
            Text(stringResource(R.string.media_image_loading))

        is AsyncImagePainter.State.Error ->
            Text(stringResource(R.string.media_image_failed))

        else ->
            Image(
                painter = painter,
                contentDescription = description,
                contentScale = contentScale,
                modifier = modifier
            )
    }
}