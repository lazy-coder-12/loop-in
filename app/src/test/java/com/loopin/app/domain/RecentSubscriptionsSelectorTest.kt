package com.loopin.app.domain

import com.loopin.app.domain.logic.RecentSubscriptionsSelector
import com.loopin.app.domain.model.BillingCycle
import com.loopin.app.domain.model.Subscription
import com.loopin.app.domain.model.SubscriptionSource
import com.loopin.app.domain.model.SubscriptionStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class RecentSubscriptionsSelectorTest {

    @Test
    fun selectRecent_returnsUpToThreeNewestByCreatedAt() {
        val now = System.currentTimeMillis()
        val subs = listOf(
            Subscription(
                id = 1L,
                name = "Oldest",
                amountMinor = 10000L,
                billingCycle = BillingCycle.MONTHLY,
                nextDueDate = LocalDate.now(),
                status = SubscriptionStatus.ACTIVE,
                source = SubscriptionSource.AUTO,
                category = "Cat",
                createdAt = now - 500000L
            ),
            Subscription(
                id = 2L,
                name = "Second Newest",
                amountMinor = 20000L,
                billingCycle = BillingCycle.MONTHLY,
                nextDueDate = LocalDate.now(),
                status = SubscriptionStatus.ACTIVE,
                source = SubscriptionSource.AUTO,
                category = "Cat",
                createdAt = now - 100000L
            ),
            Subscription(
                id = 3L,
                name = "Newest",
                amountMinor = 30000L,
                billingCycle = BillingCycle.MONTHLY,
                nextDueDate = LocalDate.now(),
                status = SubscriptionStatus.SUSPECTED, // includes SUSPECTED
                source = SubscriptionSource.AUTO,
                category = "Cat",
                createdAt = now
            ),
            Subscription(
                id = 4L,
                name = "Third Newest",
                amountMinor = 40000L,
                billingCycle = BillingCycle.MONTHLY,
                nextDueDate = LocalDate.now(),
                status = SubscriptionStatus.ACTIVE,
                source = SubscriptionSource.AUTO,
                category = "Cat",
                createdAt = now - 200000L
            )
        )

        val recent = RecentSubscriptionsSelector.selectRecent(subs, limit = 3)

        assertEquals(3, recent.size)
        assertEquals("Newest", recent[0].name)
        assertEquals("Second Newest", recent[1].name)
        assertEquals("Third Newest", recent[2].name)
    }
}
