package com.redusx.floatvault.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redusx.floatvault.data.database.AppDatabase
import com.redusx.floatvault.data.repository.AuthRepository
import com.redusx.floatvault.data.repository.DataRepository
import com.redusx.floatvault.data.security.CryptoManager
import com.redusx.floatvault.data.security.KeystoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOADING,
    val pin: String = "",
    val confirmPin: String = "",
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val failedAttempts: Int = 0,
    val isLockedOut: Boolean = false,
    val lockoutRemainingMs: Long = 0,
    val isBiometricAvailable: Boolean = false,
    val isPinSetup: Boolean = false,
    val isResettingPinWithBiometric: Boolean = false,
    val showResetVaultDialog: Boolean = false,
    val resetSuccessMessage: String? = null,
    val isUnlocking: Boolean = false,
    val showBiometricPromptDialog: Boolean = false
)

enum class AuthMode {
    LOADING,
    SETUP_PIN,
    CONFIRM_PIN,
    ENTER_PIN,
    AUTHENTICATED
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val authRepository: AuthRepository,
    private val dataRepository: DataRepository,
    private val keystoreManager: KeystoreManager,
    private val cryptoManager: CryptoManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        val isPinSetup = authRepository.isPinSetup()
        val isBiometricEnabled = authRepository.isBiometricEnabled()
        _uiState.value = _uiState.value.copy(
            mode = if (isPinSetup) AuthMode.ENTER_PIN else AuthMode.SETUP_PIN,
            isPinSetup = isPinSetup,
            isBiometricAvailable = isBiometricEnabled,
            failedAttempts = authRepository.getFailedAttempts(),
            isLockedOut = authRepository.isLockedOut(),
            lockoutRemainingMs = authRepository.getLockoutRemainingMs()
        )
    }

    fun onDigitEntered(digit: String) {
        val currentState = _uiState.value
        if (currentState.isLockedOut) return

        when (currentState.mode) {
            AuthMode.SETUP_PIN -> {
                val newPin = currentState.pin + digit
                _uiState.value = currentState.copy(pin = newPin, isError = false, errorMessage = null)
                if (newPin.length >= 6) {
                    _uiState.value = _uiState.value.copy(mode = AuthMode.CONFIRM_PIN, confirmPin = "")
                }
            }
            AuthMode.CONFIRM_PIN -> {
                val newConfirm = currentState.confirmPin + digit
                _uiState.value = currentState.copy(confirmPin = newConfirm, isError = false, errorMessage = null)
                if (newConfirm.length >= 6) {
                    if (newConfirm == currentState.pin) {
                        val success = authRepository.setupPin(newConfirm)
                        if (success) {
                            val canPromptBiometric = !currentState.isResettingPinWithBiometric &&
                                com.redusx.floatvault.util.BiometricHelper.canAuthenticate(context)

                            if (canPromptBiometric) {
                                _uiState.value = _uiState.value.copy(
                                    showBiometricPromptDialog = true,
                                    isResettingPinWithBiometric = false
                                )
                            } else {
                                _uiState.value = _uiState.value.copy(isUnlocking = true)
                                viewModelScope.launch {
                                    delay(220)
                                    _uiState.value = _uiState.value.copy(
                                        mode = AuthMode.AUTHENTICATED,
                                        isResettingPinWithBiometric = false
                                    )
                                }
                            }
                        } else {
                            _uiState.value = _uiState.value.copy(
                                mode = AuthMode.SETUP_PIN,
                                pin = "",
                                confirmPin = "",
                                isError = true,
                                errorMessage = "setup_failed"
                            )
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            mode = AuthMode.SETUP_PIN,
                            pin = "",
                            confirmPin = "",
                            isError = true,
                            errorMessage = "pin_mismatch"
                        )
                    }
                }
            }
            AuthMode.ENTER_PIN -> {
                val newPin = currentState.pin + digit
                _uiState.value = currentState.copy(pin = newPin, isError = false, errorMessage = null)
                if (newPin.length >= 6) {
                    val isValid = authRepository.verifyPin(newPin)
                    if (isValid) {
                        _uiState.value = _uiState.value.copy(isUnlocking = true)
                        viewModelScope.launch {
                            delay(240) // Allow 6th dot animation & success visual feedback to complete
                            _uiState.value = _uiState.value.copy(mode = AuthMode.AUTHENTICATED)
                        }
                    } else {
                        viewModelScope.launch {
                            delay(80)
                            _uiState.value = _uiState.value.copy(
                                pin = "",
                                isError = true,
                                errorMessage = "wrong_pin",
                                failedAttempts = authRepository.getFailedAttempts(),
                                isLockedOut = authRepository.isLockedOut(),
                                lockoutRemainingMs = authRepository.getLockoutRemainingMs()
                            )
                        }
                    }
                }
            }
            else -> {}
        }
    }

    fun onDeleteDigit() {
        val currentState = _uiState.value
        when (currentState.mode) {
            AuthMode.SETUP_PIN -> {
                if (currentState.pin.isNotEmpty()) {
                    _uiState.value = currentState.copy(pin = currentState.pin.dropLast(1))
                }
            }
            AuthMode.CONFIRM_PIN -> {
                if (currentState.confirmPin.isNotEmpty()) {
                    _uiState.value = currentState.copy(confirmPin = currentState.confirmPin.dropLast(1))
                }
            }
            AuthMode.ENTER_PIN -> {
                if (currentState.pin.isNotEmpty()) {
                    _uiState.value = currentState.copy(pin = currentState.pin.dropLast(1))
                }
            }
            else -> {}
        }
    }

    fun onBiometricSuccess() {
        _uiState.value = _uiState.value.copy(isUnlocking = true)
        viewModelScope.launch {
            delay(200)
            _uiState.value = _uiState.value.copy(mode = AuthMode.AUTHENTICATED)
        }
    }

    fun onBiometricResetVerified() {
        // Biometric verified: Transition to SETUP_PIN to enter a new PIN without wiping data!
        _uiState.value = _uiState.value.copy(
            mode = AuthMode.SETUP_PIN,
            pin = "",
            confirmPin = "",
            isError = false,
            errorMessage = null,
            isResettingPinWithBiometric = true,
            showResetVaultDialog = false,
            resetSuccessMessage = "Parmak iziniz doğrulandı. Lütfen yeni 6 haneli PIN kodunuzu belirleyin."
        )
    }

    fun showResetVaultDialog() {
        _uiState.value = _uiState.value.copy(showResetVaultDialog = true)
    }

    fun hideResetVaultDialog() {
        _uiState.value = _uiState.value.copy(showResetVaultDialog = false)
    }

    fun clearResetSuccessMessage() {
        _uiState.value = _uiState.value.copy(resetSuccessMessage = null)
    }

    fun wipeAndResetVault() {
        viewModelScope.launch {
            try {
                dataRepository.deleteAllEntries()
            } catch (e: Exception) {
                // Ignore
            }
            AppDatabase.destroyInstance()
            context.deleteDatabase(AppDatabase.DATABASE_NAME)
            context.deleteDatabase(AppDatabase.LEGACY_DATABASE_NAME)
            authRepository.deleteAllData()
            cryptoManager.clearDbPassphrase(context)
            keystoreManager.deleteAllKeys()

            _uiState.value = _uiState.value.copy(
                mode = AuthMode.SETUP_PIN,
                pin = "",
                confirmPin = "",
                isPinSetup = false,
                isError = false,
                errorMessage = null,
                failedAttempts = 0,
                isLockedOut = false,
                showResetVaultDialog = false,
                isResettingPinWithBiometric = false,
                showBiometricPromptDialog = false,
                resetSuccessMessage = "Kasa sıfırlandı. Lütfen yeni bir PIN oluşturun."
            )
        }
    }

    fun onEnableBiometricConfirmed() {
        authRepository.setBiometricEnabled(true)
        _uiState.value = _uiState.value.copy(
            showBiometricPromptDialog = false,
            isBiometricAvailable = true,
            isUnlocking = true
        )
        viewModelScope.launch {
            delay(220)
            _uiState.value = _uiState.value.copy(mode = AuthMode.AUTHENTICATED)
        }
    }

    fun onDismissBiometricPrompt() {
        authRepository.setBiometricEnabled(false)
        _uiState.value = _uiState.value.copy(
            showBiometricPromptDialog = false,
            isBiometricAvailable = false,
            isUnlocking = true
        )
        viewModelScope.launch {
            delay(220)
            _uiState.value = _uiState.value.copy(mode = AuthMode.AUTHENTICATED)
        }
    }

    fun isBiometricEnabled(): Boolean = authRepository.isBiometricEnabled()
}
