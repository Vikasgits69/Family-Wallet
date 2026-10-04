package com.example.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Card Number Visual Transformation.
 * Formats raw digits with spaces every 4 digits (e.g., "1234 5678 9012 3456")
 * without ever altering raw state or causing cursor transposition bugs.
 */
class CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
        val out = buildString {
            for (i in trimmed.indices) {
                append(trimmed[i])
                if (i % 4 == 3 && i != trimmed.lastIndex) {
                    append(" ")
                }
            }
        }

        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val spaces = (offset - 1) / 4
                return (offset + spaces).coerceAtMost(out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val spaces = offset / 5
                return (offset - spaces).coerceAtMost(trimmed.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetTranslator)
    }
}

/**
 * Expiry Date Visual Transformation.
 * Displays "MM/YY" format while user types raw digits (e.g., "1226" -> "12/26")
 * preserving exact entry order without cursor jumps.
 */
class ExpiryDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 4) text.text.substring(0..3) else text.text
        val out = buildString {
            for (i in trimmed.indices) {
                append(trimmed[i])
                if (i == 1) {
                    append("/")
                }
            }
        }

        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return (offset + 1).coerceAtMost(out.length)
                return out.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return (offset - 1).coerceAtMost(trimmed.length)
                return trimmed.length
            }
        }

        return TransformedText(AnnotatedString(out), offsetTranslator)
    }
}
