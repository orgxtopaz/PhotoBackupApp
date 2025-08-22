package com.example.photobackup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        PhotoObserver.register(context)
        WorkManager.getInstance(context).enqueueUniqueWork(
            "scan-on-boot",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<FullScanWorker>().build()
        )
    }
}
