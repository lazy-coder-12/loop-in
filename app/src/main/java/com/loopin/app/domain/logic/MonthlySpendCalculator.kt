package com.loopin.app.domain.logic

import com.loopin.app.domain.model.BillingCycle
import com.loopin.app.domain.model.Subscription
import com.loopin.app.domain.model.SubscriptionStatus

/**
 * Domain spend breakdown summary.
 */
data class MonthlySpendSummary(
    val monthlySpendMinor: Long,
    val annualSpendMinor: Long,
    val activeSubscriptionCount: Int
)

/**
 * Pure Kotlin logic for recurring subscription spend normalization.
 * Formulas:
 * - Weekly: amount * 52 / 12
 * - Monthly: amount
 * - Quarterly: amount / 3
 * - Half-Yearly: amount / 6
 * - Yearly: amount / 12
 *
 * Rules:
 * - ONLY subscriptions with status == ACTIVE are counted toward total spend.
 * - All calculations use integer Long arithmetic on paise.
 */
object MonthlySpendCalculator {

    fun calculateMonthlyEquivalent(subscription: Subscription): Long {
        val amount = subscription.amountMinor
        return when (subscription.billingCycle) {
            BillingCycle.WEEKLY -> (amount * 52L) / 12L
            BillingCycle.MONTHLY -> amount
            BillingCycle.QUARTERLY -> amount / 3L
            BillingCycle.HALF_YEARLY -> amount / 6L
            BillingCycle.YEARLY -> amount / 12L
        }
    }

    fun calculateSummary(subscriptions: List<Subscription>): MonthlySpendSummary {
        val activeSubs = subscriptions.filter { it.status == SubscriptionStatus.ACTIVE }

        val totalMonthlyMinor = activeSubs.sumOf { calculateMonthlyEquivalent(it) }
        val annualSpendMinor = totalMonthlyMinor * 12L

        return MonthlySpendSummary(
            monthlySpendMinor = totalMonthlyMinor,
            annualSpendMinor = annualSpendMinor,
            activeSubscriptionCount = activeSubs.size
        )
    }
}
