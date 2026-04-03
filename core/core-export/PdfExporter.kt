package com.meadow.core.export

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
object PdfExporter {

    suspend fun textToPdf(
        context: Context,
        text: String,
        outFile: File
    ) = withContext(Dispatchers.IO) {

        val pdf = PdfDocument()

        val pageWidth = 595
        val pageHeight = 842
        val margin = 40

        val paint = Paint().apply {
            textSize = 14f
            isAntiAlias = true
        }

        val lines = wrapText(text, paint, pageWidth - margin * 2)

        var currentLine = 0
        var pageNumber = 1

        while (currentLine < lines.size) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            val page = pdf.startPage(pageInfo)
            val canvas = page.canvas

            var y = margin.toFloat()

            while (currentLine < lines.size && y + paint.textSize < pageHeight - margin) {
                canvas.drawText(lines[currentLine], margin.toFloat(), y, paint)
                currentLine++
                y += paint.textSize + 6
            }

            pdf.finishPage(page)
            pageNumber++
        }

        outFile.outputStream().use { output ->
            pdf.writeTo(output)
        }

        pdf.close()
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else currentLine.toString() + " " + word
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine = StringBuilder(testLine)
            } else {
                lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            }
        }

        if (currentLine.isNotEmpty()) lines.add(currentLine.toString())

        return lines
    }
}