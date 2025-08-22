package com.example.photobackup

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startAutoBackup()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ensurePermissionsAndStart()
    }

    private fun ensurePermissionsAndStart() {
        val perm = if (Build.VERSION.SDK_INT >= 33)
            Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) {
            startAutoBackup()
        } else {
            permissionLauncher.launch(perm)
        }
    }

    private fun startAutoBackup() {
        val scan = OneTimeWorkRequestBuilder<FullScanWorker>().build()
        WorkManager.getInstance(this).enqueueUniqueWork(
            "full-scan-upload",
            ExistingWorkPolicy.KEEP,
            scan
        )
        lifecycleScope.launch { PhotoObserver.register(applicationContext) }
        finish()
    }
}
