package com.example.photobackup

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SingleUploadWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val uriStr = inputData.getString("uri") ?: return Result.failure()
        val uri = Uri.parse(uriStr)
        setForeground(ForegroundHelper.create(applicationContext))
        return Uploader.uploadUri(applicationContext, uri)
    }
}

class FullScanWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        setForeground(ForegroundHelper.create(applicationContext))
        val resolver = applicationContext.contentResolver
        val projection = arrayOf(
            MediaStore.Images.Media._ID
        )
        val uris = mutableListOf<Uri>()
        withContext(Dispatchers.IO) {
            resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
            )?.use { c ->
                val idCol = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (c.moveToNext()) {
                    val id = c.getLong(idCol)
                    val u = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                    uris.add(u)
                }
            }
        }
        uris.forEach { u ->
            when (Uploader.uploadUri(applicationContext, u)) {
                is Result.Retry -> return Result.retry()
                else -> Unit
            }
        }
        return Result.success()
    }
}
