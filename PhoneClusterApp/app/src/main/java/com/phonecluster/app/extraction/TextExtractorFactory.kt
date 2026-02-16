package com.phonecluster.app.extraction

import android.content.Context
import android.net.Uri

object TextExtractorFactory {

    fun getExtractor(context: Context, uri: Uri): TextExtractor {
        val mime = context.contentResolver.getType(uri) ?: ""

        return when {
            mime.contains("pdf") -> PdfTextExtractor()
            mime.contains("word") || mime.contains("docx") -> DocxTextExtractor()
            mime.contains("text") -> TxtTextExtractor()
            else -> throw IllegalArgumentException("Unsupported file type: $mime")
        }
    }
}
