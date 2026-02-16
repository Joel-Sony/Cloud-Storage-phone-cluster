package com.phonecluster.app.extraction

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class PdfTextExtractor : TextExtractor {
    override fun extract(context: Context, uri: Uri): String {
        PDFBoxResourceLoader.init(context)
        context.contentResolver.openInputStream(uri).use { input ->
            val document = PDDocument.load(input)
            val stripper = PDFTextStripper()
            val text = stripper.getText(document)
            document.close()
            return text
        }
    }
}
