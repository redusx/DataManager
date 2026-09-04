package com.redusx.floatvault.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import com.redusx.floatvault.data.database.AppDatabase
import com.redusx.floatvault.data.repository.AuthRepository
import com.redusx.floatvault.data.repository.DataRepository
import com.redusx.floatvault.data.security.CryptoManager
import com.redusx.floatvault.data.security.KeystoreManager
import com.redusx.floatvault.service.OverlayService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class SettingsUiState(
    val isBiometricEnabled: Boolean = false,
    val isOverlayEnabled: Boolean = false,
    val autoLockMinutes: Int = 1,
    val showChangePinDialog: Boolean = false,
    val showDeleteDataDialog: Boolean = false,
    val changePinOldPin: String = "",
    val changePinNewPin: String = "",
    val changePinConfirmPin: String = "",
    val changePinError: String? = null,
    val changePinSuccess: Boolean = false,
    val deleteDataSuccess: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository,
    private val dataRepository: DataRepository,
    private val keystoreManager: KeystoreManager,
    private val cryptoManager: CryptoManager
) : ViewModel() {

    companion object {
        private const val PREFS_SETTINGS = "floatvault_ui_settings"
        private const val LEGACY_PREFS_SETTINGS = "datamanager_ui_settings"
        private const val KEY_OVERLAY_ENABLED = "overlay_enabled"
    }

    private val settingsPrefs: SharedPreferences by lazy {
        val prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_OVERLAY_ENABLED)) {
            val legacy = context.getSharedPreferences(LEGACY_PREFS_SETTINGS, Context.MODE_PRIVATE)
            if (legacy.contains(KEY_OVERLAY_ENABLED)) {
                prefs.edit().putBoolean(KEY_OVERLAY_ENABLED, legacy.getBoolean(KEY_OVERLAY_ENABLED, false)).apply()
            }
        }
        prefs
    }

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        val overlayPref = settingsPrefs.getBoolean(KEY_OVERLAY_ENABLED, false)
        val canDraw = hasOverlayPermission()
        val isOverlayActive = overlayPref && canDraw

        _uiState.value = _uiState.value.copy(
            isBiometricEnabled = authRepository.isBiometricEnabled(),
            isOverlayEnabled = isOverlayActive,
            autoLockMinutes = authRepository.getAutoLockMinutes()
        )
    }

    fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    fun setOverlayEnabled(enabled: Boolean) {
        settingsPrefs.edit().putBoolean(KEY_OVERLAY_ENABLED, enabled).apply()
        _uiState.value = _uiState.value.copy(isOverlayEnabled = enabled)

        if (enabled) {
            OverlayService.start(context)
        } else {
            OverlayService.stop(context)
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        authRepository.setBiometricEnabled(enabled)
        _uiState.value = _uiState.value.copy(isBiometricEnabled = enabled)
    }

    fun setAutoLockMinutes(minutes: Int) {
        authRepository.setAutoLockMinutes(minutes)
        _uiState.value = _uiState.value.copy(autoLockMinutes = minutes)
    }

    fun showChangePinDialog() {
        _uiState.value = _uiState.value.copy(
            showChangePinDialog = true,
            changePinOldPin = "",
            changePinNewPin = "",
            changePinConfirmPin = "",
            changePinError = null,
            changePinSuccess = false
        )
    }

    fun hideChangePinDialog() {
        _uiState.value = _uiState.value.copy(showChangePinDialog = false)
    }

    fun onChangePinOldPinChanged(pin: String) {
        if (pin.all { it.isDigit() } && pin.length <= 6) {
            _uiState.value = _uiState.value.copy(changePinOldPin = pin, changePinError = null)
        }
    }

    fun onChangePinNewPinChanged(pin: String) {
        if (pin.all { it.isDigit() } && pin.length <= 6) {
            _uiState.value = _uiState.value.copy(changePinNewPin = pin, changePinError = null)
        }
    }

    fun onChangePinConfirmPinChanged(pin: String) {
        if (pin.all { it.isDigit() } && pin.length <= 6) {
            _uiState.value = _uiState.value.copy(changePinConfirmPin = pin, changePinError = null)
        }
    }

    fun changePin() {
        val state = _uiState.value
        if (state.changePinNewPin != state.changePinConfirmPin) {
            _uiState.value = state.copy(changePinError = "pin_mismatch")
            return
        }
        if (state.changePinNewPin.length < 4 || state.changePinNewPin.length > 6 || !state.changePinNewPin.all { it.isDigit() }) {
            _uiState.value = state.copy(changePinError = "pin_too_short")
            return
        }
        if (state.changePinOldPin.length < 4 || !state.changePinOldPin.all { it.isDigit() }) {
            _uiState.value = state.copy(changePinError = "wrong_old_pin")
            return
        }

        val success = authRepository.changePin(state.changePinOldPin, state.changePinNewPin)
        if (success) {
            _uiState.value = state.copy(changePinSuccess = true, showChangePinDialog = false)
        } else {
            _uiState.value = state.copy(changePinError = "wrong_old_pin")
        }
    }

    fun showDeleteDataDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDataDialog = true)
    }

    fun hideDeleteDataDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDataDialog = false)
    }

    suspend fun deleteAllData() {
        setOverlayEnabled(false)
        try {
            dataRepository.deleteAllEntries()
        } catch (e: Exception) {
            // Ignore
        }
        AppDatabase.destroyInstance()
        context.deleteDatabase(AppDatabase.DATABASE_NAME)
        authRepository.deleteAllData()
        cryptoManager.clearDbPassphrase(context)
        keystoreManager.deleteAllKeys()
        _uiState.value = _uiState.value.copy(
            showDeleteDataDialog = false,
            deleteDataSuccess = true
        )
    }
}
