package com.example.datamanager

import android.app.Application
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.example.datamanager.service.OverlayService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DataManagerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // If user enabled persistent floating button in Settings, auto-start overlay service
        val prefs = getSharedPreferences("datamanager_ui_settings", Context.MODE_PRIVATE)
        val isOverlayPersistent = prefs.getBoolean("overlay_enabled", false)

        if (isOverlayPersistent) {
            val hasPerm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(this)
            } else {
                true
            }
            if (hasPerm) {
                OverlayService.start(this)
            }
        }
    }
}
