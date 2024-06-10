package com.example.device

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileNotFoundException

class FileContentProvider : ContentProvider() {
    companion object {
        const val AUTHORITY = "com.example.myapp.fileprovider"
        const val BASE_PATH = "files"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$BASE_PATH")
    }

    override fun onCreate(): Boolean {
        return true
    }

    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val file = File(context?.filesDir, uri.lastPathSegment)
        if (!file.exists()) {
            throw FileNotFoundException("File not found: ${uri.toString()}")
        }
        val fileMode = ParcelFileDescriptor.MODE_READ_ONLY
        return ParcelFileDescriptor.open(file, fileMode)
    }

    override fun getType(uri: Uri): String? {
        return "text/plain"
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Not implemented")
    }
}