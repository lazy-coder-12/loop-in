package com.loopin.app.domain.model

import java.time.LocalDate

/**
 * Pure domain model representing a recurring subscription.
 * Money is strictly stored as Long in paise (amountMinor).
 */
data class Subscription(
    val id: Long = 0L,
    val name: String,
    val amountMinor: Long,
    val billingCycle: BillingCycle,
    val nextDueDate: LocalDate,
    val status: SubscriptionStatus,
    val source: SubscriptionSource,
    val category: String,
    val isTrial: Boolean = false,
    val trialEndDate: LocalDate? = null,
    val amountAfterTrialMinor: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
