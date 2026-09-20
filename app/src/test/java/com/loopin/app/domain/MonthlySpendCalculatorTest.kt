package com.loopin.app.domain

import com.loopin.app.domain.logic.MonthlySpendCalculator
import com.loopin.app.domain.model.BillingCycle
import com.loopin.app.domain.model.Subscription
import com.loopin.app.domain.model.SubscriptionSource
import com.loopin.app.domain.model.SubscriptionStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class MonthlySpendCalculatorTest {

    private val baseDate = LocalDate.of(2026, 9, 20)

    private fun createSub(
        amountMinor: Long,
        cycle: BillingCycle,
        status: SubscriptionStatus = SubscriptionStatus.ACTIVE
    ): Subscription {
        return Subscription(
            id = 1L,
            name = "Test",
            amountMinor = amountMinor,
            billingCycle = cycle,
            nextDueDate = baseDate,
            status = status,
            source = SubscriptionSource.AUTO,
            category = "Test"
        )
    }

    @Test
    fun calculateMonthlyEquivalent_weekly_normalizesCorrectly() {
        // ₹120 weekly -> (12000 * 52) / 12 = 52000 paise = ₹520/month
        val sub = createSub(12000L, BillingCycle.WEEKLY)
        assertEquals(52000L, MonthlySpendCalculator.calculateMonthlyEquivalent(sub))
    }

    @Test
    fun calculateMonthlyEquivalent_monthly_returnsExactAmount() {
        val sub = createSub(64900L, BillingCycle.MONTHLY)
        assertEquals(64900L, MonthlySpendCalculator.calculateMonthlyEquivalent(sub))
    }

    @Test
    fun calculateMonthlyEquivalent_quarterly_dividesByThree() {
        val sub = createSub(90000L, BillingCycle.QUARTERLY)
        assertEquals(30000L, MonthlySpendCalculator.calculateMonthlyEquivalent(sub))
    }

    @Test
    fun calculateMonthlyEquivalent_halfYearly_dividesBySix() {
        val sub = createSub(60000L, BillingCycle.HALF_YEARLY)
        assertEquals(10000L, MonthlySpendCalculator.calculateMonthlyEquivalent(sub))
    }

    @Test
    fun calculateMonthlyEquivalent_yearly_dividesByTwelve() {
        val sub = createSub(1200000L, BillingCycle.YEARLY)
        assertEquals(100000L, MonthlySpendCalculator.calculateMonthlyEquivalent(sub))
    }

    @Test
    fun calculateSummary_countsOnlyActiveSubscriptions() {
        val subscriptions = listOf(
            createSub(64900L, BillingCycle.MONTHLY, SubscriptionStatus.ACTIVE),    // ₹649/mo
            createSub(149900L, BillingCycle.YEARLY, SubscriptionStatus.ACTIVE),    // ₹1499/12 = 12491 paise
            createSub(199900L, BillingCycle.MONTHLY, SubscriptionStatus.SUSPECTED),// Excluded!
            createSub(1200000L, BillingCycle.YEARLY, SubscriptionStatus.PAUSED),   // Excluded!
            createSub(50000L, BillingCycle.MONTHLY, SubscriptionStatus.ENDED)      // Excluded!
        )

        val summary = MonthlySpendCalculator.calculateSummary(subscriptions)

        assertEquals(2, summary.activeSubscriptionCount)
        val expectedMonthly = 64900L + (149900L / 12L) // 64900 + 12491 = 77391L
        assertEquals(expectedMonthly, summary.monthlySpendMinor)
        assertEquals(expectedMonthly * 12L, summary.annualSpendMinor)
    }
}
