package com.meadow.core.media.viewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

class PdfPageRenderer(
    context: Context,
    private val path: String
) {

    private val file = File(path)
    private val descriptor =
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    private val renderer = PdfRenderer(descriptor)

    val pageCount: Int get() = renderer.pageCount

    fun renderPage(index: Int): ImageBitmap? {
        renderer.openPage(index).use { page ->
            val bmp = Bitmap.createBitmap(
                page.width, page.height, Bitmap.Config.ARGB_8888
            )
            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            return bmp.asImageBitmap()
        }
    }

    fun renderThumbnail(index: Int): ImageBitmap? {
        renderer.openPage(index).use { page ->
            val bmp = Bitmap.createBitmap(
                page.width / 6,
                page.height / 6,
                Bitmap.Config.ARGB_8888
            )
            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            return bmp.asImageBitmap()
        }
    }
}
