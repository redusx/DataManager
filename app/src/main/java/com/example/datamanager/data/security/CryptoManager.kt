package com.example.datamanager.data.security

import android.content.Context
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor(
    private val keystoreManager: KeystoreManager
) {

    companion object {
        private const val DB_PREFS_NAME = "datamanager_db_security"
        private const val KEY_ENCRYPTED_DB_PASSPHRASE = "encrypted_db_passphrase"
        private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
        private const val PBKDF2_ITERATIONS = 100_000
        private const val SALT_SIZE = 32
        private const val KEY_LENGTH = 256
    }

    /**
     * Get or create a persistent database passphrase.
     * Generates a 256-bit random passphrase on first creation, encrypts it
     * using the Android Keystore master key (including the IV), and saves it.
     * Subsequent calls decrypt the saved payload so the passphrase is
     * completely stable and identical across app launches.
     */
    fun getOrCreateDbPassphrase(context: Context): ByteArray {
        val prefs = context.getSharedPreferences(DB_PREFS_NAME, Context.MODE_PRIVATE)
        val encryptedPassphraseB64 = prefs.getString(KEY_ENCRYPTED_DB_PASSPHRASE, null)

        if (encryptedPassphraseB64 != null) {
            try {
                val encryptedBytes = Base64.decode(encryptedPassphraseB64, Base64.NO_WRAP)
                return keystoreManager.decrypt(encryptedBytes)
            } catch (e: Exception) {
                // Keystore was reset or corrupted, generate a new one below
            }
        }

        // Generate 32 cryptographically secure random bytes (256-bit key)
        val rawPassphrase = ByteArray(32).also {
            SecureRandom().nextBytes(it)
        }
        val encrypted = keystoreManager.encrypt(rawPassphrase)
        val b64 = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        prefs.edit().putString(KEY_ENCRYPTED_DB_PASSPHRASE, b64).commit()
        return rawPassphrase
    }

    /**
     * Clear stored db passphrase (used during complete data wipe).
     */
    fun clearDbPassphrase(context: Context) {
        context.getSharedPreferences(DB_PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit()
    }

    /**
     * Hash a PIN with a random salt using PBKDF2.
     * Returns Base64-encoded "salt:hash" string.
     */
    fun hashPin(pin: String): String {
        val salt = ByteArray(SALT_SIZE).also {
            SecureRandom().nextBytes(it)
        }
        val hash = pbkdf2Hash(pin, salt)
        val saltB64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        val hashB64 = Base64.encodeToString(hash, Base64.NO_WRAP)
        return "$saltB64:$hashB64"
    }

    /**
     * Verify a PIN against a stored "salt:hash" string.
     */
    fun verifyPin(pin: String, storedHash: String): Boolean {
        val parts = storedHash.split(":")
        if (parts.size != 2) return false
        val salt = Base64.decode(parts[0], Base64.NO_WRAP)
        val expectedHash = Base64.decode(parts[1], Base64.NO_WRAP)
        val actualHash = pbkdf2Hash(pin, salt)
        return expectedHash.contentEquals(actualHash)
    }

    /**
     * Encrypt the PIN hash with the Keystore master key for storage.
     */
    fun encryptForStorage(data: String): String {
        val encrypted = keystoreManager.encrypt(data.toByteArray())
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    /**
     * Decrypt stored data with the Keystore master key.
     */
    fun decryptFromStorage(encryptedData: String): String {
        val data = Base64.decode(encryptedData, Base64.NO_WRAP)
        return String(keystoreManager.decrypt(data))
    }

    private fun pbkdf2Hash(pin: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
        return factory.generateSecret(spec).encoded
    }
}
