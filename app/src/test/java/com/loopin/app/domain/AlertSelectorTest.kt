package com.loopin.app.domain

import com.loopin.app.domain.logic.AlertSelector
import com.loopin.app.domain.model.AlertType
import com.loopin.app.domain.model.BillingCycle
import com.loopin.app.domain.model.Subscription
import com.loopin.app.domain.model.SubscriptionSource
import com.loopin.app.domain.model.SubscriptionStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class AlertSelectorTest {

    private val today = LocalDate.of(2026, 9, 20)

    @Test
    fun selectAlerts_renewsToday_labeledToday() {
        val sub = Subscription(
            id = 1L,
            name = "Spotify",
            amountMinor = 13900L,
            billingCycle = BillingCycle.MONTHLY,
            nextDueDate = today,
            status = SubscriptionStatus.ACTIVE,
            source = SubscriptionSource.AUTO,
            category = "Entertainment"
        )

        val alerts = AlertSelector.selectAlerts(listOf(sub), today)
        assertEquals(1, alerts.size)
        assertEquals("Today", alerts[0].formattedUrgency)
        assertEquals("Spotify renews today · ₹139", alerts[0].displayText)
    }

    @Test
    fun selectAlerts_renewsTomorrow_labeledTomorrow() {
        val sub = Subscription(
            id = 1L,
            name = "Netflix",
            amountMinor = 64900L,
            billingCycle = BillingCycle.MONTHLY,
            nextDueDate = today.plusDays(1),
            status = SubscriptionStatus.ACTIVE,
            source = SubscriptionSource.AUTO,
            category = "Entertainment"
        )

        val alerts = AlertSelector.selectAlerts(listOf(sub), today)
        assertEquals(1, alerts.size)
        assertEquals("Tomorrow", alerts[0].formattedUrgency)
        assertEquals("Netflix renews tomorrow · ₹649", alerts[0].displayText)
    }

    @Test
    fun selectAlerts_trialEnding_labeledWithAmountAfter() {
        val sub = Subscription(
            id = 2L,
            name = "Notion",
            amountMinor = 0L,
            billingCycle = BillingCycle.MONTHLY,
            nextDueDate = today.plusDays(2),
            status = SubscriptionStatus.ACTIVE,
            source = SubscriptionSource.MANUAL,
            category = "Productivity",
            isTrial = true,
            trialEndDate = today.plusDays(2),
            amountAfterTrialMinor = 80000L
        )

        val alerts = AlertSelector.selectAlerts(listOf(sub), today)
        assertEquals(1, alerts.size)
        assertEquals(AlertType.TRIAL_EXPIRY, alerts[0].type)
        assertEquals("In 2 days", alerts[0].formattedUrgency)
        assertEquals("Notion trial ends in 2 days · ₹800 after", alerts[0].displayText)
    }

    @Test
    fun selectAlerts_beyondSevenDays_excluded() {
        val sub = Subscription(
            id = 3L,
            name = "Amazon Prime",
            amountMinor = 149900L,
            billingCycle = BillingCycle.YEARLY,
            nextDueDate = today.plusDays(8), // > 7 days
            status = SubscriptionStatus.ACTIVE,
            source = SubscriptionSource.MANUAL,
            category = "Lifestyle"
        )

        val alerts = AlertSelector.selectAlerts(listOf(sub), today)
        assertTrue(alerts.isEmpty())
    }

    @Test
    fun selectAlerts_sortedAscendingByDate() {
        val sub1 = Subscription(
            id = 1L,
            name = "Disney+",
            amountMinor = 49900L,
            billingCycle = BillingCycle.MONTHLY,
            nextDueDate = today.plusDays(4),
            status = SubscriptionStatus.ACTIVE,
            source = SubscriptionSource.AUTO,
            category = "Entertainment"
        )
        val sub2 = Subscription(
            id = 2L,
            name = "Netflix",
            amountMinor = 64900L,
            billingCycle = BillingCycle.MONTHLY,
            nextDueDate = today.plusDays(1),
            status = SubscriptionStatus.ACTIVE,
            source = SubscriptionSource.AUTO,
            category = "Entertainment"
        )

        val alerts = AlertSelector.selectAlerts(listOf(sub1, sub2), today)
        assertEquals(2, alerts.size)
        assertEquals("Netflix", alerts[0].subscription.name)
        assertEquals("Disney+", alerts[1].subscription.name)
    }
}
