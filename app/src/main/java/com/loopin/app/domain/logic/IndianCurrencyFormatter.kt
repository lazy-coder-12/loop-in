package com.loopin.app.domain.logic

/**
 * Breakdown of an Indian Rupee amount for stylized rendering (dimmed symbol & decimals).
 */
data class FormattedRupees(
    val symbol: String = "₹",
    val wholePart: String,
    val decimalPart: String = "",
    val isNegative: Boolean = false
) {
    val fullFormatted: String
        get() = "${if (isNegative) "-" else ""}$symbol$wholePart$decimalPart"
}

/**
 * Pure Kotlin Indian Currency Formatter.
 * Handles:
 * - Lakh / Crore grouping (e.g. ₹1,24,999, ₹10,50,000, ₹4,281).
 * - Paise stored as Long (never floating point).
 * - Omits decimals when paise is zero (428100L -> ₹4,281).
 * - Shows exactly two decimal digits when paise > 0 (428150L -> ₹4,281.50).
 * - Independent of locale behavior.
 */
object IndianCurrencyFormatter {

    fun format(amountMinor: Long): String {
        return split(amountMinor).fullFormatted
    }

    fun split(amountMinor: Long): FormattedRupees {
        val isNegative = amountMinor < 0
        val absPaiseTotal = if (isNegative) -amountMinor else amountMinor
        val rupees = absPaiseTotal / 100L
        val paise = absPaiseTotal % 100L

        val wholePart = formatIndianGrouping(rupees)
        val decimalPart = if (paise > 0L) {
            val paiseString = if (paise < 10) "0$paise" else paise.toString()
            ".$paiseString"
        } else {
            ""
        }

        return FormattedRupees(
            symbol = "₹",
            wholePart = wholePart,
            decimalPart = decimalPart,
            isNegative = isNegative
        )
    }

    /**
     * Formats a whole rupee amount according to the Indian numbering system:
     * Rightmost 3 digits grouped, followed by pairs of 2 digits.
     * e.g. 10000000 -> 1,00,00,000
     *      124999   -> 1,24,999
     *      4281     -> 4,281
     *      999      -> 999
     */
    fun formatIndianGrouping(rupees: Long): String {
        if (rupees == 0L) return "0"

        val str = rupees.toString()
        val len = str.length

        if (len <= 3) {
            return str
        }

        val last3 = str.substring(len - 3)
        var remaining = str.substring(0, len - 3)

        val result = StringBuilder()
        while (remaining.length > 2) {
            val chunk = remaining.substring(remaining.length - 2)
            if (result.isEmpty()) {
                result.insert(0, chunk)
            } else {
                result.insert(0, "$chunk,")
            }
            remaining = remaining.substring(0, remaining.length - 2)
        }

        if (remaining.isNotEmpty()) {
            if (result.isNotEmpty()) {
                result.insert(0, "$remaining,")
            } else {
                result.insert(0, remaining)
            }
        }

        return "$result,$last3"
    }
}
