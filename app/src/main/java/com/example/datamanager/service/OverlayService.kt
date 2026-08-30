package com.example.datamanager.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.datamanager.MainActivity
import com.example.datamanager.R
import com.example.datamanager.data.model.DataEntry
import com.example.datamanager.data.repository.DataRepository
import com.example.datamanager.ui.theme.DataManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OverlayService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    companion object {
        private const val CHANNEL_ID = "datamanager_overlay_channel"
        private const val NOTIFICATION_ID = 1001
        private const val PREFS_SETTINGS = "datamanager_ui_settings"
        private const val KEY_OVERLAY_ENABLED = "overlay_enabled"

        fun start(context: Context) {
            val intent = Intent(context, OverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, OverlayService::class.java))
        }
    }

    @Inject
    lateinit var dataRepository: DataRepository

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val entriesState = MutableStateFlow<List<DataEntry>>(emptyList())

    // Lifecycle & SavedState
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private var windowManager: WindowManager? = null
    private var bubbleView: View? = null
    private var panelContainer: FrameLayout? = null
    private var isExpanded = false

    private var bubbleParams: WindowManager.LayoutParams? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        // Collect database entries
        serviceScope.launch {
            dataRepository.getAllEntries().collectLatest {
                entriesState.value = it
            }
        }

        showBubble()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val prefs = getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)
        val isPersistentEnabled = prefs.getBoolean(KEY_OVERLAY_ENABLED, false)

        return if (isPersistentEnabled) START_STICKY else START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val prefs = getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)
        val isPersistentEnabled = prefs.getBoolean(KEY_OVERLAY_ENABLED, false)

        // If user has NOT explicitly enabled the persistent overlay setting in Settings,
        // stop the overlay service when the app is closed from recent tasks.
        if (!isPersistentEnabled) {
            stopSelf()
        }

        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        removeBubble()
        removePanel()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.overlay_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.overlay_channel_desc)
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.overlay_notification_title))
            .setContentText(getString(R.string.overlay_notification_text))
            .setSmallIcon(R.drawable.ic_shield)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun getOverlayType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }

    private fun showBubble() {
        if (bubbleView != null) return

        val imageView = android.widget.ImageView(this).apply {
            setImageResource(R.drawable.ic_shield)
            setPadding(24, 24, 24, 24)
            setBackgroundResource(android.R.drawable.dialog_holo_dark_frame)
            elevation = 16f
        }

        bubbleParams = WindowManager.LayoutParams(
            160,
            160,
            getOverlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 20
            y = 400
        }

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isDragging = false

        imageView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = bubbleParams?.x ?: 0
                    initialY = bubbleParams?.y ?: 0
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isDragging = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - initialTouchX
                    val dy = event.rawY - initialTouchY
                    if (dx * dx + dy * dy > 100) {
                        isDragging = true
                    }
                    bubbleParams?.x = initialX + dx.toInt()
                    bubbleParams?.y = initialY + dy.toInt()
                    if (bubbleView != null && bubbleParams != null) {
                        windowManager?.updateViewLayout(bubbleView, bubbleParams)
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!isDragging) {
                        // Toggle expand floating panel
                        toggleExpanded()
                    }
                    true
                }
                else -> false
            }
        }

        bubbleView = imageView
        windowManager?.addView(bubbleView, bubbleParams)
    }

    private fun removeBubble() {
        bubbleView?.let {
            try {
                windowManager?.removeView(it)
            } catch (e: Exception) {
                // Ignore
            }
        }
        bubbleView = null
    }

    private fun toggleExpanded() {
        if (isExpanded) {
            collapseToBubble()
        } else {
            expandToPanel()
        }
    }

    private fun expandToPanel() {
        isExpanded = true
        removeBubble()

        val displayMetrics = resources.displayMetrics
        val panelWidth = (displayMetrics.widthPixels * 0.92f).toInt().coerceAtMost(1000)

        val params = WindowManager.LayoutParams(
            panelWidth,
            WindowManager.LayoutParams.WRAP_CONTENT,
            getOverlayType(),
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        // Custom container FrameLayout that intercepts touches outside the card
        val container = object : FrameLayout(this) {
            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                if (ev.action == MotionEvent.ACTION_OUTSIDE) {
                    collapseToBubble()
                    return true
                }
                return super.dispatchTouchEvent(ev)
            }
        }

        // Set ViewTree owners on container (the root view added to WindowManager)
        container.setViewTreeLifecycleOwner(this)
        container.setViewTreeViewModelStoreOwner(this)
        container.setViewTreeSavedStateRegistryOwner(this)

        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@OverlayService)
            setViewTreeViewModelStoreOwner(this@OverlayService)
            setViewTreeSavedStateRegistryOwner(this@OverlayService)

            setContent {
                DataManagerTheme {
                    val entries by entriesState.collectAsState()
                    OverlayPanel(
                        entries = entries,
                        onMinimize = { collapseToBubble() },
                        onCopiedAndMinimize = { collapseToBubble() },
                        onOpenMainApp = {
                            collapseToBubble()
                            val launchIntent = Intent(this@OverlayService, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                            startActivity(launchIntent)
                        }
                    )
                }
            }
        }

        container.addView(composeView)
        panelContainer = container

        try {
            windowManager?.addView(panelContainer, params)
        } catch (e: Exception) {
            // Fallback
            showBubble()
            isExpanded = false
        }
    }

    private fun collapseToBubble() {
        isExpanded = false
        removePanel()
        showBubble()
    }

    private fun removePanel() {
        panelContainer?.let {
            try {
                windowManager?.removeView(it)
            } catch (e: Exception) {
                // Ignore
            }
        }
        panelContainer = null
    }
}
