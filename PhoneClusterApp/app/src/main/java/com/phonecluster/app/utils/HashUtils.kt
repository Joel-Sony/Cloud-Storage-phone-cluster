package com.phonecluster.app.utils

import java.security.MessageDigest

object HashUtils {
    fun sha256(text: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(text.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
