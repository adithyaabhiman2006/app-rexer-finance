package com.example.util

import java.text.NumberFormat
import java.util.Locale

data class CurrencyItem(
    val symbol: String,
    val code: String,
    val name: String,
    val flag: String = ""
)

object CurrencyHelper {
    val supportedCurrencies = listOf(
        CurrencyItem(symbol = "$", code = "USD", name = "US Dollar", flag = "🇺🇸"),
        CurrencyItem(symbol = "LKR", code = "LKR", name = "Sri Lankan Rupee", flag = "🇱🇰"),
        CurrencyItem(symbol = "₹", code = "INR", name = "Indian Rupee", flag = "🇮🇳"),
        CurrencyItem(symbol = "€", code = "EUR", name = "Euro", flag = "🇪🇺"),
        CurrencyItem(symbol = "£", code = "GBP", name = "British Pound", flag = "🇬🇧"),
        CurrencyItem(symbol = "¥", code = "JPY", name = "Japanese Yen", flag = "🇯🇵"),
        CurrencyItem(symbol = "A$", code = "AUD", name = "Australian Dollar", flag = "🇦🇺"),
        CurrencyItem(symbol = "C$", code = "CAD", name = "Canadian Dollar", flag = "🇨🇦"),
        CurrencyItem(symbol = "AED", code = "AED", name = "UAE Dirham", flag = "🇦🇪"),
        CurrencyItem(symbol = "SGD$", code = "SGD", name = "Singapore Dollar", flag = "🇸🇬"),
        CurrencyItem(symbol = "CHF", code = "CHF", name = "Swiss Franc", flag = "🇨🇭"),
        CurrencyItem(symbol = "Rs", code = "PKR", name = "Pakistani Rupee", flag = "🇵🇰")
    )

    fun getQuickExpensePresets(currencySymbol: String): List<Int> {
        val sym = currencySymbol.trim().uppercase()
        return when {
            sym == "$" || sym == "USD" || sym == "€" || sym == "EUR" || sym == "£" || sym == "GBP" || sym == "CHF" -> {
                listOf(5, 10, 20, 50, 100, 250)
            }
            sym == "A$" || sym == "AUD" || sym == "C$" || sym == "CAD" || sym == "SGD$" || sym == "SGD" || sym == "AED" -> {
                listOf(10, 25, 50, 100, 200, 500)
            }
            sym == "LKR" || sym == "JPY" || sym == "¥" || sym == "KRW" -> {
                listOf(500, 1000, 2500, 5000, 10000, 25000)
            }
            else -> {
                // ₹ (INR), Rs, or others
                listOf(100, 250, 500, 1000, 2000, 5000)
            }
        }
    }

    fun getDailyCapPresets(currencySymbol: String): List<Int> {
        val sym = currencySymbol.trim().uppercase()
        return when {
            sym == "$" || sym == "USD" || sym == "€" || sym == "EUR" || sym == "£" || sym == "GBP" || sym == "CHF" -> {
                listOf(25, 50, 100, 150, 250, 500)
            }
            sym == "A$" || sym == "AUD" || sym == "C$" || sym == "CAD" || sym == "SGD$" || sym == "SGD" || sym == "AED" -> {
                listOf(50, 100, 200, 350, 500, 1000)
            }
            sym == "LKR" || sym == "JPY" || sym == "¥" || sym == "KRW" -> {
                listOf(2500, 5000, 10000, 15000, 25000, 50000)
            }
            else -> {
                listOf(1000, 2000, 3000, 5000, 8000, 15000)
            }
        }
    }

    fun getGoalDepositPresets(currencySymbol: String): List<Int> {
        val sym = currencySymbol.trim().uppercase()
        return when {
            sym == "$" || sym == "USD" || sym == "€" || sym == "EUR" || sym == "£" || sym == "GBP" || sym == "CHF" -> {
                listOf(10, 25, 50, 100, 250, 500)
            }
            sym == "A$" || sym == "AUD" || sym == "C$" || sym == "CAD" || sym == "SGD$" || sym == "SGD" || sym == "AED" -> {
                listOf(25, 50, 100, 250, 500, 1000)
            }
            sym == "LKR" || sym == "JPY" || sym == "¥" -> {
                listOf(1000, 2500, 5000, 10000, 25000, 50000)
            }
            else -> {
                listOf(500, 1000, 2000, 5000, 10000, 20000)
            }
        }
    }

    fun formatAmount(amount: Double, currencySymbol: String): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
        val formattedNumber = if (amount % 1.0 == 0.0) {
            formatter.format(amount.toLong())
        } else {
            String.format(Locale.getDefault(), "%,.2f", amount)
        }
        val sym = currencySymbol.trim()
        return if (sym.length > 2) {
            "$sym $formattedNumber"
        } else {
            "$sym$formattedNumber"
        }
    }
}
