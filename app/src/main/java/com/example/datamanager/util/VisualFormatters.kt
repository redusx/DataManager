package com.example.datamanager.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Formats a raw numeric string as 16-digit card number chunks: 4242 4242 4242 4242
 */
class CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i != 15) {
                out += " "
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val clamped = offset.coerceAtMost(16)
                val spaces = (clamped - 1) / 4
                return (clamped + spaces).coerceAtMost(out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                val clamped = offset.coerceAtMost(out.length)
                val spaces = clamped / 5
                return (clamped - spaces).coerceAtMost(trimmed.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

/**
 * Formats a 4-digit date string into MM / YY
 */
class ExpiryDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 4) text.text.substring(0..3) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 && trimmed.length > 2) {
                out += " / "
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return (offset + 3).coerceAtMost(out.length)
                return out.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return 2
                return (offset - 3).coerceAtMost(trimmed.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

/**
 * Formats an IBAN string into 4-character chunks: TR33 0006 1005 ...
 */
class IbanVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val clean = text.text.replace(" ", "").uppercase()
        val trimmed = if (clean.length >= 26) clean.substring(0..25) else clean
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i != trimmed.lastIndex) {
                out += " "
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val clamped = offset.coerceAtMost(26)
                val spaces = (clamped - 1) / 4
                return (clamped + spaces).coerceAtMost(out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                val clamped = offset.coerceAtMost(out.length)
                val spaces = clamped / 5
                return (clamped - spaces).coerceAtMost(trimmed.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

/**
 * Formats a Turkish Phone Number: 0 (5XX) XXX XX XX
 */
class PhoneNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        val trimmed = if (digits.length >= 11) digits.substring(0..10) else digits
        var out = ""
        for (i in trimmed.indices) {
            when (i) {
                0 -> out += "${trimmed[i]} ("
                3 -> out += "${trimmed[i]}) "
                6 -> out += "${trimmed[i]} "
                8 -> out += "${trimmed[i]} "
                else -> out += trimmed[i]
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset == 1) return 3
                if (offset in 2..4) return offset + 2
                if (offset in 5..7) return offset + 4
                if (offset in 8..9) return offset + 5
                return (offset + 6).coerceAtMost(out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return offset.coerceAtMost(trimmed.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}
