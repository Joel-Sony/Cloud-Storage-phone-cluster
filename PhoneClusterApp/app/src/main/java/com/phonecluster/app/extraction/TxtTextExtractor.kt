package com.phonecluster.app.extraction

import android.content.Context
import android.net.Uri

class TxtTextExtractor : TextExtractor {
    override fun extract(context: Context, uri: Uri): String {
        return context.contentResolver.openInputStream(uri)
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: ""
    }
}

