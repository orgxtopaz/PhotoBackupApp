package com.example.photobackup

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.work.Data

object Util {
    fun dataOf(vararg pairs: Pair<String, String>) = Data.Builder().apply {
        pairs.forEach { (k, v) -> putString(k, v) }
    }.build()

    fun filenameFromUri(ctx: Context, uri: Uri): String? {
        return when (uri.scheme) {
            ContentResolver.SCHEME_CONTENT -> queryName(ctx, uri)
            else -> uri.lastPathSegment
        }
    }

    private fun queryName(ctx: Context, uri: Uri): String? {
        var name: String? = null
        val c: Cursor? = ctx.contentResolver.query(uri, null, null, null, null)
        c?.use {
            val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx >= 0 && it.moveToFirst()) name = it.getString(idx)
        }
        return name
    }
}
