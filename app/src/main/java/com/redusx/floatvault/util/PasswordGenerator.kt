package com.redusx.floatvault.util

import java.security.SecureRandom

object PasswordGenerator {

    private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
    private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val DIGITS = "0123456789"
    private const val SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>?"

    private val random = SecureRandom()

    fun generate(
        length: Int = 16,
        includeUppercase: Boolean = true,
        includeDigits: Boolean = true,
        includeSymbols: Boolean = true
    ): String {
        val charPool = StringBuilder(LOWERCASE).apply {
            if (includeUppercase) append(UPPERCASE)
            if (includeDigits) append(DIGITS)
            if (includeSymbols) append(SYMBOLS)
        }.toString()

        if (charPool.isEmpty()) return ""

        val password = StringBuilder(length)
        // Ensure at least one of each selected category
        if (includeUppercase) password.append(UPPERCASE[random.nextInt(UPPERCASE.length)])
        if (includeDigits) password.append(DIGITS[random.nextInt(DIGITS.length)])
        if (includeSymbols) password.append(SYMBOLS[random.nextInt(SYMBOLS.length)])

        while (password.length < length) {
            password.append(charPool[random.nextInt(charPool.length)])
        }

        // Shuffle the characters
        return password.toString().toList().shuffled(random).joinToString("")
    }

    enum class Strength {
        WEAK,
        MEDIUM,
        STRONG
    }

    fun calculateStrength(password: String): Strength {
        if (password.length < 8) return Strength.WEAK
        var score = 0
        if (password.length >= 12) score++
        if (password.length >= 16) score++
        if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++

        return when {
            score >= 4 -> Strength.STRONG
            score >= 2 -> Strength.MEDIUM
            else -> Strength.WEAK
        }
    }
}
