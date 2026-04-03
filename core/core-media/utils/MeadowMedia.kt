package com.meadow.core.media.utils

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.meadow.core.media.player.AudioPlayer
import com.meadow.core.media.player.VideoPlayer
import com.meadow.core.media.viewer.PdfPageRenderer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.launch
import java.io.File

data class MeadowMediaConfig(
    val folder: String,
    val mimeType: String
)

fun mediaConfig(kind: String): MeadowMediaConfig =
    when (kind) {
        "IMAGE" -> MeadowMediaConfig("images", "image/*")
        "VIDEO" -> MeadowMediaConfig("videos", "video/*")
        "AUDIO" -> MeadowMediaConfig("audios", "audio/*")
        "PDF" -> MeadowMediaConfig("pdfs", "application/pdf")
        "SUBTITLE" -> MeadowMediaConfig("subtitles", "text/*")
        else -> MeadowMediaConfig("files", "*/*")
    }

fun saveMedia(
    context: Context,
    uri: Uri,
    folder: String
): String =
    MediaUtils.copyToAppStorage(
        context = context,
        uri = uri,
        folder = folder,
        extension = null
    )

@Composable
fun MeadowMediaPreview(
    path: String,
    kind: String
) {

    val context = LocalContext.current

    when (kind) {

        "IMAGE" ->
            AsyncImage(
                model = path,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )

        "VIDEO" -> {

            val scope = rememberCoroutineScope()

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                factory = { ctx ->

                    val playerView = PlayerView(ctx)

                    val controller = VideoPlayer(
                        context = ctx,
                        playerView = playerView
                    )

                    scope.launch {
                        controller.play(path)
                    }

                    playerView
                }
            )
        }

        "AUDIO" -> {

            val scope = rememberCoroutineScope()

            LaunchedEffect(path) {

                val controller = AudioPlayer(context)

                scope.launch {
                    controller.play(path)
                }
            }
        }

        "PDF" ->
            PdfPageRenderer(
                context = context,
                path = path
            )

        else -> {}
    }
}