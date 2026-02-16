package com.phonecluster.app.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

object FileUtils {

    fun getFileName(context: Context, uri: Uri): String {
        var name = "unknown_file"

        val cursor = context.contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }

        return name
    }
}
