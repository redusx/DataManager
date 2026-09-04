package com.example.datamanager.util

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.PersistableBundle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ClipboardHelper {

    private const val AUTO_CLEAR_DELAY_MS = 60_000L // 60 seconds
    private val scope = CoroutineScope(Dispatchers.Main)
    private var autoClearJob: Job? = null

    fun copyToClipboard(
        context: Context,
        label: String,
        text: String,
        isSensitive: Boolean = true
    ) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)

        if (isSensitive && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            clip.description.extras = PersistableBundle().apply {
                putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
            }
        }

        clipboard.setPrimaryClip(clip)

        if (isSensitive) {
            scheduleAutoClear(clipboard, text)
        }
    }

    private fun scheduleAutoClear(clipboard: ClipboardManager, copiedText: String) {
        autoClearJob?.cancel()
        autoClearJob = scope.launch {
            delay(AUTO_CLEAR_DELAY_MS)
            try {
                val currentClip = clipboard.primaryClip
                if (currentClip != null && currentClip.itemCount > 0) {
                    val currentText = currentClip.getItemAt(0).text?.toString()
                    if (currentText == copiedText) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            clipboard.clearPrimaryClip()
                        } else {
                            clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore any system/security exceptions
            }
        }
    }
}
