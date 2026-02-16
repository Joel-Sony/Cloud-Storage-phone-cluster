package com.phonecluster.app.extraction

import android.content.Context
import android.net.Uri
import org.apache.poi.xwpf.usermodel.XWPFDocument

class DocxTextExtractor : TextExtractor {

    override fun extract(context: Context, uri: Uri): String {
        context.contentResolver.openInputStream(uri).use { input ->
            val doc = XWPFDocument(input)
            return doc.paragraphs.joinToString("\n") { it.text }
        }
    }
}
