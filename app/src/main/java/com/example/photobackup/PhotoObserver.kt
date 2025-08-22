package com.example.photobackup

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

object PhotoObserver {
    private var observer: ContentObserver? = null

    fun register(ctx: Context) {
        if (observer != null) return
        val resolver: ContentResolver = ctx.contentResolver
        observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                uri?.let { enqueueUpload(ctx, it) }
            }
        }
        resolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observer as ContentObserver
        )
    }

    private fun enqueueUpload(ctx: Context, uri: Uri) {
        val req = OneTimeWorkRequestBuilder<SingleUploadWorker>()
            .setInputData(Util.dataOf("uri" to uri.toString()))
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()
        WorkManager.getInstance(ctx).enqueue(req)
    }
}
