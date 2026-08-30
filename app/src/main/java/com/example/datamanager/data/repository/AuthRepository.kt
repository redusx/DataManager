package com.example.datamanager.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.datamanager.data.security.CryptoManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptoManager
) {

    companion object {
        private const val PREFS_NAME = "datamanager_auth_prefs"
        private const val KEY_PIN_HASH = "pin_hash_encrypted"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
        private const val KEY_LOCKOUT_UNTIL = "lockout_until"
        private const val KEY_AUTO_LOCK_MINUTES = "auto_lock_minutes"
        private const val MAX_ATTEMPTS_BEFORE_LOCKOUT = 5
        private const val LOCKOUT_DURATION_MS = 30_000L // 30 seconds
    }

    private val prefs: SharedPreferences by lazy {
        createEncryptedPrefs()
    }

    private fun createEncryptedPrefs(): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            try {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
                EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e2: Exception) {
                // Fallback to private prefs if hardware keystore fails
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            }
        }
    }

    fun isPinSetup(): Boolean {
        return try {
            prefs.getString(KEY_PIN_HASH, null) != null
        } catch (e: Exception) {
            false
        }
    }

    fun setupPin(pin: String): Boolean {
        // Enforce numeric-only PIN (4 to 6 digits)
        if (pin.length !in 4..6 || !pin.all { it.isDigit() }) return false
        return try {
            val pinHash = cryptoManager.hashPin(pin)
            val encrypted = cryptoManager.encryptForStorage(pinHash)
            prefs.edit().putString(KEY_PIN_HASH, encrypted).apply()
            resetFailedAttempts()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun verifyPin(pin: String): Boolean {
        // Enforce numeric-only PIN (4 to 6 digits)
        if (pin.length !in 4..6 || !pin.all { it.isDigit() }) return false
        if (isLockedOut()) return false

        return try {
            val encryptedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
            val storedHash = cryptoManager.decryptFromStorage(encryptedHash)
            val isValid = cryptoManager.verifyPin(pin, storedHash)

            if (isValid) {
                resetFailedAttempts()
            } else {
                incrementFailedAttempts()
            }
            isValid
        } catch (e: Exception) {
            false
        }
    }

    fun changePin(oldPin: String, newPin: String): Boolean {
        if (newPin.length !in 4..6 || !newPin.all { it.isDigit() }) return false
        if (!verifyPin(oldPin)) return false
        return setupPin(newPin)
    }

    fun isBiometricEnabled(): Boolean {
        return try {
            prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        } catch (e: Exception) {
            false
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        try {
            prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun getAutoLockMinutes(): Int {
        return try {
            prefs.getInt(KEY_AUTO_LOCK_MINUTES, 1)
        } catch (e: Exception) {
            1
        }
    }

    fun setAutoLockMinutes(minutes: Int) {
        try {
            prefs.edit().putInt(KEY_AUTO_LOCK_MINUTES, minutes).apply()
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun getFailedAttempts(): Int {
        return try {
            prefs.getInt(KEY_FAILED_ATTEMPTS, 0)
        } catch (e: Exception) {
            0
        }
    }

    fun isLockedOut(): Boolean {
        return try {
            val lockoutUntil = prefs.getLong(KEY_LOCKOUT_UNTIL, 0)
            if (lockoutUntil > System.currentTimeMillis()) return true
            if (lockoutUntil > 0) {
                resetFailedAttempts()
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun getLockoutRemainingMs(): Long {
        return try {
            val lockoutUntil = prefs.getLong(KEY_LOCKOUT_UNTIL, 0)
            (lockoutUntil - System.currentTimeMillis()).coerceAtLeast(0)
        } catch (e: Exception) {
            0L
        }
    }

    private fun incrementFailedAttempts() {
        try {
            val attempts = getFailedAttempts() + 1
            prefs.edit().putInt(KEY_FAILED_ATTEMPTS, attempts).apply()

            if (attempts >= MAX_ATTEMPTS_BEFORE_LOCKOUT) {
                val lockoutUntil = System.currentTimeMillis() + LOCKOUT_DURATION_MS
                prefs.edit().putLong(KEY_LOCKOUT_UNTIL, lockoutUntil).apply()
            }
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun resetFailedAttempts() {
        try {
            prefs.edit()
                .putInt(KEY_FAILED_ATTEMPTS, 0)
                .putLong(KEY_LOCKOUT_UNTIL, 0)
                .apply()
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun deleteAllData() {
        try {
            prefs.edit().clear().apply()
        } catch (e: Exception) {
            // Ignore
        }
    }
}
