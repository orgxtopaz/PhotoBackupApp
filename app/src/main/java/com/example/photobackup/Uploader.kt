package com.example.photobackup

import android.content.Context
import android.net.Uri
import androidx.work.ListenableWorker.Result
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

object Uploader {
    private const val SERVER_URL = "https://lolu.alwaysdata.net/upload.php"
    private const val AUTH_TOKEN = "7fa8b92c63a42ff5e0d37e92d65a10cbe56dc9d2384f8b1b8f9a8d1f92c7f002"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun upload(bytes: ByteArray, filename: String?): Result {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                filename ?: "photo.jpg",
                bytes.toRequestBody("image/*".toMediaType())
            )
            .build()

        val req = Request.Builder()
            .url(SERVER_URL)
            .header("Authorization", "Bearer $AUTH_TOKEN")
            .post(body)
            .build()

        client.newCall(req).execute().use { resp ->
            return if (resp.isSuccessful) Result.success() else Result.retry()
        }
    }

    suspend fun uploadUri(ctx: Context, uri: Uri): Result {
        return try {
            ctx.contentResolver.openInputStream(uri)?.use { stream ->
                val bytes = stream.readBytes()
                upload(bytes, Util.filenameFromUri(ctx, uri))
            } ?: Result.failure()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
