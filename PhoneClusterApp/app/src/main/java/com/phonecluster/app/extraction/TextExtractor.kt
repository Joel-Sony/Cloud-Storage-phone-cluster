package com.phonecluster.app.extraction

import android.content.Context
import android.net.Uri

interface TextExtractor {
    fun extract(context: Context, uri: Uri): String
}
