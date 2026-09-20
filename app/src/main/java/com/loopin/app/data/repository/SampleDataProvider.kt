package com.loopin.app.data.repository

import com.loopin.app.domain.model.BillingCycle
import com.loopin.app.domain.model.Subscription
import com.loopin.app.domain.model.SubscriptionSource
import com.loopin.app.domain.model.SubscriptionStatus
import java.time.LocalDate

/**
 * Realistic Indian sample subscription data relative to current date.
 * Strictly calibrated to produce the exact UI stats seen in the design:
 * - 6 active subscriptions
 * - ₹ 2,912.07 monthly spend
 * - ₹ 34,944.84 annual spend
 * - 3 upcoming renewals
 * - Top 3 recent: Disney + Hotstar, Netflix, Spotify
 */
object SampleDataProvider {

    fun getSampleSubscriptions(referenceDate: LocalDate = LocalDate.now()): List<Subscription> {
        val nowMillis = System.currentTimeMillis()

        return listOf(
            // 1. Disney + Hotstar (Newest created -> Top 1 recent)
            Subscription(
                id = 1L,
                name = "Disney + Hotstar",
                amountMinor = 49900L, // ₹499
                billingCycle = BillingCycle.MONTHLY,
                nextDueDate = referenceDate.plusDays(4),
                status = SubscriptionStatus.ACTIVE,
                source = SubscriptionSource.AUTO,
                category = "Entertainment",
                createdAt = nowMillis - 10000L
            ),
            // 2. Netflix (2nd newest created -> Top 2 recent)
            Subscription(
                id = 2L,
                name = "Netflix",
                amountMinor = 64900L, // ₹649
                billingCycle = BillingCycle.MONTHLY,
                nextDueDate = referenceDate.plusDays(1),
                status = SubscriptionStatus.ACTIVE,
                source = SubscriptionSource.AUTO,
                category = "Entertainment",
                createdAt = nowMillis - 20000L
            ),
            // 3. Spotify (3rd newest created -> Top 3 recent)
            Subscription(
                id = 3L,
                name = "Spotify",
                amountMinor = 13900L, // ₹139
                billingCycle = BillingCycle.MONTHLY,
                nextDueDate = referenceDate.plusDays(8), // renews after 7 days
                status = SubscriptionStatus.ACTIVE,
                source = SubscriptionSource.AUTO,
                category = "Entertainment",
                createdAt = nowMillis - 30000L
            ),
            // 4. Claude Pro (Active monthly -> ₹1,499)
            Subscription(
                id = 4L,
                name = "Claude Pro",
                amountMinor = 149900L, // ₹1,499
                billingCycle = BillingCycle.MONTHLY,
                nextDueDate = referenceDate.plusDays(15),
                status = SubscriptionStatus.ACTIVE,
                source = SubscriptionSource.AUTO,
                category = "Productivity",
                createdAt = nowMillis - 40000L
            ),
            // 5. Amazon Prime (Active yearly -> ₹1,512.84 / 12 = ₹126.07/mo)
            Subscription(
                id = 5L,
                name = "Amazon Prime",
                amountMinor = 151284L,
                billingCycle = BillingCycle.YEARLY,
                nextDueDate = referenceDate.plusDays(6), // renews in 6 days (3rd alert)
                status = SubscriptionStatus.ACTIVE,
                source = SubscriptionSource.MANUAL,
                category = "Lifestyle",
                createdAt = nowMillis - 50000L
            ),
            // 6. Notion (Active trial -> ₹0/mo, ends in 2 days -> 2nd alert)
            Subscription(
                id = 6L,
                name = "Notion",
                amountMinor = 0L,
                billingCycle = BillingCycle.MONTHLY,
                nextDueDate = referenceDate.plusDays(2),
                status = SubscriptionStatus.ACTIVE,
                source = SubscriptionSource.MANUAL,
                category = "Productivity",
                isTrial = true,
                trialEndDate = referenceDate.plusDays(2),
                amountAfterTrialMinor = 80000L,
                createdAt = nowMillis - 60000L
            ),
            // 7. Cult.fit Gym (Paused membership -> excluded from spend & alerts)
            Subscription(
                id = 7L,
                name = "Cult.fit Gym",
                amountMinor = 1200000L,
                billingCycle = BillingCycle.YEARLY,
                nextDueDate = referenceDate.plusDays(90),
                status = SubscriptionStatus.PAUSED,
                source = SubscriptionSource.MANUAL,
                category = "Fitness",
                createdAt = nowMillis - 70000L
            )
        )
    }
}
