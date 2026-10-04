package com.example.util

import android.net.Uri
import java.io.File

object ImageModelResolver {
    fun resolve(uriString: String?): Any? {
        if (uriString.isNullOrBlank()) return null
        return try {
            if (uriString.startsWith("file://")) {
                val parsed = Uri.parse(uriString)
                val path = parsed.path
                if (path != null && File(path).exists()) {
                    File(path)
                } else {
                    parsed
                }
            } else if (uriString.startsWith("/")) {
                val f = File(uriString)
                if (f.exists()) f else Uri.fromFile(f)
            } else if (uriString.startsWith("content://")) {
                Uri.parse(uriString)
            } else {
                uriString
            }
        } catch (_: Exception) {
            uriString
        }
    }
}
