package com.example.datamanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.example.datamanager.ui.navigation.AppNavigation
import com.example.datamanager.ui.theme.DataManagerTheme
import com.example.datamanager.util.BiometricHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private var deviceCredentialSuccessCallback: (() -> Unit)? = null
    private var deviceCredentialErrorCallback: ((String) -> Unit)? = null

    private val deviceCredentialLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            deviceCredentialSuccessCallback?.invoke()
        } else {
            deviceCredentialErrorCallback?.invoke("Telefon kilit şifresi doğrulanmadı.")
        }
        deviceCredentialSuccessCallback = null
        deviceCredentialErrorCallback = null
    }

    fun launchDeviceCredentialIntent(
        intent: Intent,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        deviceCredentialSuccessCallback = onSuccess
        deviceCredentialErrorCallback = onError
        deviceCredentialLauncher.launch(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Secure screen against OS task-switcher thumbnails and screenshots
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        enableEdgeToEdge()

        setContent {
            DataManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        onBiometricRequested = { onAuthSuccess ->
                            BiometricHelper.showPrompt(
                                activity = this@MainActivity,
                                onSuccess = onAuthSuccess,
                                onError = { /* fallback to PIN */ }
                            )
                        },
                        onBiometricResetRequested = { onResetSuccess, onResetError ->
                            BiometricHelper.showBiometricResetPrompt(
                                activity = this@MainActivity,
                                onSuccess = onResetSuccess,
                                onError = { onResetError() }
                            )
                        },
                        onDeviceCredentialRequested = { title, subtitle, onSuccess, onError ->
                            BiometricHelper.showDeviceCredentialPrompt(
                                activity = this@MainActivity,
                                title = title,
                                subtitle = subtitle,
                                onSuccess = onSuccess,
                                onError = onError
                            )
                        }
                    )
                }
            }
        }
    }
}