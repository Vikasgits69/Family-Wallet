package com.example.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object AttachmentFileManager {

    private const val ATTACHMENTS_DIR_NAME = "vault_attachments"

    fun getAttachmentsDir(context: Context): File {
        val dir = File(context.filesDir, ATTACHMENTS_DIR_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Copies a user-selected Uri (Image or PDF) into private internal storage.
     * Returns the relative file path (e.g. "vault_attachments/uuid.pdf").
     */
    fun copyUriToInternalStorage(context: Context, sourceUri: Uri): String? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(sourceUri)
            
            // Determine file extension
            var extension = if (mimeType != null) {
                MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            } else {
                null
            }

            if (extension.isNullOrBlank()) {
                // Try getting extension from display name
                val displayName = queryDisplayName(context, sourceUri)
                if (displayName != null && displayName.contains(".")) {
                    extension = displayName.substringAfterLast(".", "bin")
                } else if (mimeType == "application/pdf") {
                    extension = "pdf"
                } else {
                    extension = "jpg"
                }
            }

            val targetFileName = "${UUID.randomUUID()}.$extension"
            val targetFile = File(getAttachmentsDir(context), targetFileName)

            val inputStream: InputStream? = contentResolver.openInputStream(sourceUri)
            if (inputStream != null) {
                inputStream.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                "$ATTACHMENTS_DIR_NAME/$targetFileName"
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Resolves a relative or absolute path to a File in internal storage.
     */
    fun getFile(context: Context, path: String): File {
        return if (path.startsWith(context.filesDir.absolutePath)) {
            File(path)
        } else if (path.startsWith(ATTACHMENTS_DIR_NAME)) {
            File(context.filesDir, path)
        } else {
            File(getAttachmentsDir(context), path.substringAfterLast("/"))
        }
    }

    fun isPdf(file: File): Boolean {
        return file.name.endsWith(".pdf", ignoreCase = true)
    }

    fun isImage(file: File): Boolean {
        val name = file.name.lowercase()
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".webp")
    }

    fun deleteAttachment(context: Context, path: String): Boolean {
        val file = getFile(context, path)
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    fun getAllAttachmentFiles(context: Context): List<File> {
        val dir = getAttachmentsDir(context)
        return dir.listFiles()?.toList() ?: emptyList()
    }

    private fun queryDisplayName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        return it.getString(nameIndex)
                    }
                }
            }
        }
        return uri.path?.substringAfterLast('/')
    }
}
