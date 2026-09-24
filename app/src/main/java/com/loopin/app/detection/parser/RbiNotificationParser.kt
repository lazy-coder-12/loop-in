package com.loopin.app.detection.parser

import com.loopin.app.detection.matcher.MerchantMatcher
import com.loopin.app.detection.model.BankIssuer
import com.loopin.app.detection.model.ParsedDebitAlert
import com.loopin.app.domain.model.BillingCycle
import java.time.LocalDate
import java.time.Month
import java.util.Locale

/**
 * Pure Kotlin parser that identifies RBI-mandated pre-debit notifications
 * (e-mandates, recurring Standing Instructions, UPI AutoPay) and extracts
 * structured subscription data without any network calls or SMS permissions.
 */
object RbiNotificationParser {

    private val otpRegex = Regex("(?i)\\b(?:otp|one time password|verification code|secret code)\\b")
    private val atmWithdrawalRegex = Regex("(?i)\\b(?:withdrawn at atm|atm cash|atm withdrawal)\\b")

    private val preDebitTriggers = listOf(
        "pre-debit",
        "pre debit",
        "predebit",
        "standing instruction",
        "si alert",
        "si debit",
        "e-mandate",
        "emandate",
        "autopay",
        "auto-pay",
        "autodebit",
        "auto debit",
        "auto-debit",
        "scheduled to be debited",
        "scheduled for debit",
        "is scheduled on",
        "will be debited",
        "will be processed",
        "recurring payment",
        "recurring debit",
        "recurring transaction"
    )

    private val amountPrefixRegex = Regex("(?i)(?:rs\\.?|inr|₹)\\s*([0-9]{1,3}(?:,[0-9]{3})*(?:\\.[0-9]{1,2})?|[0-9]+(?:\\.[0-9]{1,2})?)")
    private val amountSuffixRegex = Regex("(?i)([0-9]{1,3}(?:,[0-9]{3})*(?:\\.[0-9]{1,2})?|[0-9]+(?:\\.[0-9]{1,2})?)\\s*(?:rs\\.?|inr|₹)")

    private val merchantTowardsRegex = Regex("(?i)towards\\s+([A-Za-z0-9\\s+&.-]+?)(?:\\s+(?:on|dated|for|using|via|from|ref|mandate|card|a/c|with|to|to\\s+cancel)|$)")
    private val merchantForRegex = Regex("(?i)for\\s+(?:your\\s+mandate\\s+with\\s+|your\\s+recurring\\s+payment\\s+with\\s+|your\\s+)?([A-Za-z0-9\\s+&.-]+?)(?:\\s+(?:on|dated|is\\s+scheduled|will\\s+be|using|via|from|ref|mandate|card|a/c|with|to|to\\s+cancel)|$)")
    private val merchantColonRegex = Regex("(?i)merchant\\s*[:\\-]\\s*([A-Za-z0-9\\s+&.-]+?)(?:\\s+(?:on|dated|ref|using|from)|$)")

    private val cardAccountRegex = Regex("(?i)(?:card|a/c|account|xx|\\*\\*)\\s*(?:ending\\s*(?:in\\s*)?)?([0-9]{4}|\\*{2,4}[0-9]{4}|xx[0-9]{4})")
    private val mandateRefRegex = Regex("(?i)\\b(?:umrn|mandate\\s+ref|mandate\\s+id|ref\\s*(?:no\\.?|id)?)\\s*[:\\-]?\\s*([A-Za-z0-9]+)")

    private val fullDateRegex = Regex("(?i)\\b(\\d{1,2})[-/.](\\d{1,2}|[A-Za-z]{3,9})[-/.](\\d{2,4})\\b")
    private val dateWithMonthNameRegex = Regex("(?i)\\b(\\d{1,2})\\s+([A-Za-z]{3,9})(?:\\s+(\\d{4}))?\\b")

    /**
     * Determines whether the notification represents an RBI pre-debit / e-mandate alert.
     */
    fun isPreDebitNotification(title: String, text: String): Boolean {
        val combined = "$title $text".lowercase(Locale.ROOT)

        // 1. Immediately filter out OTPs or ATM cash withdrawals
        if (otpRegex.containsMatchIn(combined)) return false
        if (atmWithdrawalRegex.containsMatchIn(combined)) return false

        // 2. Check for pre-debit / recurring auto-debit triggers
        return preDebitTriggers.any { combined.contains(it) }
    }

    /**
     * Parses an incoming notification and returns a structured [ParsedDebitAlert], or null if not applicable.
     */
    fun parseNotification(
        packageName: String,
        title: String,
        text: String,
        postTime: Long = System.currentTimeMillis(),
        referenceDate: LocalDate = LocalDate.now()
    ): ParsedDebitAlert? {
        val combined = "$title $text"
        if (!isPreDebitNotification(title, text)) {
            return null
        }

        // 1. Extract Amount
        val amountMinor = extractAmountMinor(combined) ?: return null

        // 2. Extract Merchant Candidate
        val rawMerchant = extractMerchantCandidate(combined) ?: "Recurring Subscription"
        val matchResult = MerchantMatcher.match(rawMerchant, amountMinor)

        // 3. Extract Due Date
        val debitDate = extractDebitDate(combined, referenceDate)

        // 4. Extract Card/Account Mask & Mandate Ref
        val accountMask = extractAccountOrCardMask(combined)
        val mandateRef = extractMandateRef(combined)

        // 5. Identify Issuer
        val issuer = BankIssuer.resolve(packageName, combined)

        return ParsedDebitAlert(
            rawMerchant = rawMerchant,
            matchedServiceName = matchResult.serviceName,
            matchedCatalogId = matchResult.catalogService?.id,
            matchedPlanName = matchResult.matchedPlan?.name,
            amountMinor = amountMinor,
            debitDate = debitDate,
            billingCycle = BillingCycle.MONTHLY,
            category = matchResult.category,
            bankIssuer = issuer,
            accountOrCardMask = accountMask,
            mandateRef = mandateRef,
            confidenceScore = matchResult.confidence,
            rawTitle = title,
            rawText = text,
            sourcePackage = packageName,
            detectedAt = postTime
        )
    }

    private fun extractAmountMinor(text: String): Long? {
        val prefixMatch = amountPrefixRegex.find(text)
        val candidate = prefixMatch?.groupValues?.getOrNull(1)
            ?: amountSuffixRegex.find(text)?.groupValues?.getOrNull(1)
            ?: return null

        val cleanNum = candidate.replace(",", "").trim()
        val amountDouble = cleanNum.toDoubleOrNull() ?: return null
        return (amountDouble * 100).toLong()
    }

    private fun extractMerchantCandidate(text: String): String? {
        // Try preposition patterns
        val towardsMatch = merchantTowardsRegex.find(text)
        if (towardsMatch != null) {
            val candidate = towardsMatch.groupValues[1].trim()
            if (isValidMerchantCandidate(candidate)) return candidate
        }

        val forMatch = merchantForRegex.find(text)
        if (forMatch != null) {
            val candidate = forMatch.groupValues[1].trim()
            if (isValidMerchantCandidate(candidate)) return candidate
        }

        val colonMatch = merchantColonRegex.find(text)
        if (colonMatch != null) {
            val candidate = colonMatch.groupValues[1].trim()
            if (isValidMerchantCandidate(candidate)) return candidate
        }

        // Check for direct catalog mentions in text (e.g. "Netflix", "Spotify", "Hotstar")
        val knownServices = listOf(
            "Netflix", "Spotify", "Disney+ Hotstar", "Hotstar", "Claude",
            "Figma", "LinkedIn", "YouTube", "Amazon Prime", "Apple", "Microsoft"
        )
        for (service in knownServices) {
            if (text.contains(service, ignoreCase = true)) {
                return service
            }
        }

        return null
    }

    private fun isValidMerchantCandidate(candidate: String): Boolean {
        val lower = candidate.lowercase(Locale.ROOT)
        // Discard if candidate is just "your card", "auto debit", "rs", etc.
        val invalidTokens = listOf("card", "account", "a/c", "auto", "debit", "mandate", "rs", "inr")
        if (candidate.isBlank() || candidate.length > 50) return false
        if (invalidTokens.any { lower == it }) return false
        return true
    }

    private fun extractDebitDate(text: String, referenceDate: LocalDate): LocalDate {
        val lower = text.lowercase(Locale.ROOT)
        if (lower.contains("tomorrow")) {
            return referenceDate.plusDays(1)
        }
        if (lower.contains("today")) {
            return referenceDate
        }

        // Try "24-Sep-2024" or "25/09/2024"
        val fullMatch = fullDateRegex.find(text)
        if (fullMatch != null) {
            val day = fullMatch.groupValues[1].toIntOrNull()
            val monthStr = fullMatch.groupValues[2]
            val yearRaw = fullMatch.groupValues[3].toIntOrNull()

            if (day != null && day in 1..31) {
                val month = parseMonth(monthStr)
                if (month != null) {
                    val year = when {
                        yearRaw == null -> referenceDate.year
                        yearRaw < 100 -> 2000 + yearRaw
                        else -> yearRaw
                    }
                    return safeLocalDate(year, month, day) ?: referenceDate.plusDays(1)
                }
            }
        }

        // Try "24 Sep" or "24 September 2024"
        val nameMatch = dateWithMonthNameRegex.find(text)
        if (nameMatch != null) {
            val day = nameMatch.groupValues[1].toIntOrNull()
            val monthStr = nameMatch.groupValues[2]
            val yearRaw = nameMatch.groupValues.getOrNull(3)?.toIntOrNull()

            if (day != null && day in 1..31) {
                val month = parseMonth(monthStr)
                if (month != null) {
                    val year = yearRaw ?: referenceDate.year
                    return safeLocalDate(year, month, day) ?: referenceDate.plusDays(1)
                }
            }
        }

        // Fallback: RBI requires minimum 24 hour notice, so default to tomorrow
        return referenceDate.plusDays(1)
    }

    private fun parseMonth(monthStr: String): Month? {
        val num = monthStr.toIntOrNull()
        if (num != null && num in 1..12) {
            return Month.of(num)
        }
        val lower = monthStr.lowercase(Locale.ROOT).take(3)
        return when (lower) {
            "jan" -> Month.JANUARY
            "feb" -> Month.FEBRUARY
            "mar" -> Month.MARCH
            "apr" -> Month.APRIL
            "may" -> Month.MAY
            "jun" -> Month.JUNE
            "jul" -> Month.JULY
            "aug" -> Month.AUGUST
            "sep" -> Month.SEPTEMBER
            "oct" -> Month.OCTOBER
            "nov" -> Month.NOVEMBER
            "dec" -> Month.DECEMBER
            else -> null
        }
    }

    private fun safeLocalDate(year: Int, month: Month, day: Int): LocalDate? {
        return try {
            val maxDay = month.length(java.time.Year.isLeap(year.toLong()))
            LocalDate.of(year, month, day.coerceIn(1, maxDay))
        } catch (_: Exception) {
            null
        }
    }

    private fun extractAccountOrCardMask(text: String): String? {
        val match = cardAccountRegex.find(text) ?: return null
        val digits = match.groupValues.getOrNull(1) ?: return null
        return when {
            digits.length == 4 -> "ending $digits"
            else -> digits
        }
    }

    private fun extractMandateRef(text: String): String? {
        val match = mandateRefRegex.find(text) ?: return null
        return match.groupValues.getOrNull(1)
    }
}
