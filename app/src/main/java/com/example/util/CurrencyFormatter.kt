package com.example.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val indianLocale = Locale("en", "IN")

    fun formatRupees(amount: Double, includeDecimals: Boolean = true): String {
        return try {
            val formatter = NumberFormat.getCurrencyInstance(indianLocale)
            val symbol = "₹"
            if (includeDecimals) {
                "$symbol" + String.format(Locale.ENGLISH, "%,.2f", amount)
            } else {
                "$symbol" + String.format(Locale.ENGLISH, "%,.0f", amount)
            }
        } catch (_: Exception) {
            if (includeDecimals) {
                "₹" + String.format(Locale.ENGLISH, "%,.2f", amount)
            } else {
                "₹" + String.format(Locale.ENGLISH, "%,.0f", amount)
            }
        }
    }

    fun formatRupeesCompact(amount: Double): String {
        return when {
            amount >= 10_000_000 -> "₹" + String.format(Locale.ENGLISH, "%.2f Cr", amount / 10_000_000.0)
            amount >= 100_000 -> "₹" + String.format(Locale.ENGLISH, "%.2f L", amount / 100_000.0)
            amount >= 1_000 -> "₹" + String.format(Locale.ENGLISH, "%.1f K", amount / 1_000.0)
            else -> "₹" + String.format(Locale.ENGLISH, "%,.0f", amount)
        }
    }
}
