package com.loopin.app.detection.model

import com.loopin.app.domain.logic.IndianCurrencyFormatter
import com.loopin.app.domain.model.BillingCycle
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Encapsulates a parsed RBI pre-debit alert extracted on-device
 * from bank and card network notifications.
 */
data class ParsedDebitAlert(
    val id: String = UUID.randomUUID().toString(),
    val rawMerchant: String,
    val matchedServiceName: String,
    val matchedCatalogId: String? = null,
    val matchedPlanName: String? = null,
    val amountMinor: Long,
    val debitDate: LocalDate,
    val billingCycle: BillingCycle = BillingCycle.MONTHLY,
    val category: String,
    val bankIssuer: BankIssuer = BankIssuer.OTHER,
    val accountOrCardMask: String? = null,
    val mandateRef: String? = null,
    val confidenceScore: Float = 1.0f,
    val rawTitle: String = "",
    val rawText: String = "",
    val sourcePackage: String = "",
    val detectedAt: Long = System.currentTimeMillis()
) {
    val formattedAmount: String
        get() = IndianCurrencyFormatter.format(amountMinor)

    val formattedDate: String
        get() = debitDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
}
