package com.meadow.core.export

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object EpubExporter {

    suspend fun export(
        title: String,
        author: String,
        chapters: List<String>,
        outFile: File
    ) = withContext(Dispatchers.IO) {

        ZipOutputStream(outFile.outputStream()).use { zip ->

            val mimetypeEntry = ZipEntry("mimetype")
            mimetypeEntry.method = ZipEntry.STORED
            val mimeBytes = "application/epub+zip".toByteArray()
            mimetypeEntry.size = mimeBytes.size.toLong()
            mimetypeEntry.crc = java.util.zip.CRC32().apply { update(mimeBytes) }.value
            zip.putNextEntry(mimetypeEntry)
            zip.write(mimeBytes)
            zip.closeEntry()

            zip.putNextEntry(ZipEntry("META-INF/container.xml"))
            zip.write(
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
                    <rootfiles>
                        <rootfile full-path="OEBPS/content.opf" media-type="application/oebps-package+xml"/>
                    </rootfiles>
                </container>
                """.trimIndent().toByteArray()
            )
            zip.closeEntry()

            chapters.forEachIndexed { index, xhtml ->
                zip.putNextEntry(ZipEntry("OEBPS/chapter$index.xhtml"))
                zip.write(xhtml.toByteArray())
                zip.closeEntry()
            }

            val manifestItems = chapters.indices.joinToString("\n") { i ->
                """<item id="chap$i" href="chapter$i.xhtml" media-type="application/xhtml+xml"/>"""
            }

            val spineItems = chapters.indices.joinToString("\n") { i ->
                """<itemref idref="chap$i"/>"""
            }

            zip.putNextEntry(ZipEntry("OEBPS/content.opf"))
            zip.write(
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <package version="3.0"
                         xmlns="http://www.idpf.org/2007/opf"
                         unique-identifier="bookid">

                    <metadata xmlns:dc="http://purl.org/dc/elements/1.1/">
                        <dc:identifier id="bookid">urn:uuid:${java.util.UUID.randomUUID()}</dc:identifier>
                        <dc:title>$title</dc:title>
                        <dc:creator>$author</dc:creator>
                        <dc:language>en</dc:language>
                    </metadata>

                    <manifest>
                        $manifestItems
                    </manifest>

                    <spine>
                        $spineItems
                    </spine>

                </package>
                """.trimIndent().toByteArray()
            )
            zip.closeEntry()
        }
    }
}