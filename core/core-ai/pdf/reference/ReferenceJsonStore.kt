package com.meadow.core.ai.pdf.reference

import android.content.Context
import com.google.gson.Gson
import java.io.File

class ReferenceJsonStore(
    private val context: Context,
    private val gson: Gson = Gson()
) {
    private val folder: File by lazy {
        File(context.filesDir, "pdf/reference").apply { mkdirs() }
    }

    private val file: File get() = File(folder, "reference_library.json")

    fun exportLibrary(documents: List<ReferenceDocumentEntity>, chunks: List<ReferenceChunkEntity>) {
        val payload = ExportPayload(
            documents = documents,
            chunks = chunks.map {
                ExportChunk(
                    id = it.id,
                    documentId = it.documentId,
                    documentTitle = it.documentTitle,
                    documentPath = it.documentPath,
                    pageNumber = it.pageNumber,
                    text = it.text
                )
            }
        )
        file.writeText(gson.toJson(payload))
    }

    fun readExport(): ExportPayload? {
        if (!file.exists()) return null
        return gson.fromJson(file.readText(), ExportPayload::class.java)
    }

    data class ExportPayload(
        val documents: List<ReferenceDocumentEntity>,
        val chunks: List<ExportChunk>
    )

    data class ExportChunk(
        val id: String,
        val documentId: String,
        val documentTitle: String,
        val documentPath: String,
        val pageNumber: Int,
        val text: String
    )
}
