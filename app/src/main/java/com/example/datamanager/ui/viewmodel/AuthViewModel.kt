package com.example.datamanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.datamanager.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val isPinSetup: Boolean = false
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
    private val authRepository: AuthRepository
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
                            _uiState.value = _uiState.value.copy(mode = AuthMode.AUTHENTICATED)
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
                        _uiState.value = _uiState.value.copy(mode = AuthMode.AUTHENTICATED)
                    } else {
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
        _uiState.value = _uiState.value.copy(mode = AuthMode.AUTHENTICATED)
    }

    fun isBiometricEnabled(): Boolean = authRepository.isBiometricEnabled()
}
