package com.loopin.app.domain.logic

import com.loopin.app.domain.model.AlertType
import com.loopin.app.domain.model.RenewalAlert
import com.loopin.app.domain.model.Subscription
import com.loopin.app.domain.model.SubscriptionStatus
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Pure domain logic to select and format upcoming alerts within the next 7 days.
 * Includes:
 * - ACTIVE subscriptions renewing within next 7 days [today..today+7]
 * - Subscriptions with trials ending within next 7 days [today..today+7]
 * - Sorted ascending by targetDate.
 */
object AlertSelector {

    fun selectAlerts(
        subscriptions: List<Subscription>,
        today: LocalDate = LocalDate.now()
    ): List<RenewalAlert> {
        val maxDate = today.plusDays(7)
        val alerts = mutableListOf<RenewalAlert>()

        for (sub in subscriptions) {
            // 1. Check trial expiry first if subscription is in trial
            if (sub.isTrial && sub.trialEndDate != null) {
                val trialEnd = sub.trialEndDate
                if (!trialEnd.isBefore(today) && !trialEnd.isAfter(maxDate)) {
                    val days = ChronoUnit.DAYS.between(today, trialEnd)
                    val urgency = formatUrgency(days)
                    val urgencyLower = urgency.lowercase()
                    val displayText = if (sub.amountAfterTrialMinor != null && sub.amountAfterTrialMinor > 0L) {
                        val amountAfter = IndianCurrencyFormatter.format(sub.amountAfterTrialMinor)
                        "${sub.name} trial ends $urgencyLower · $amountAfter after"
                    } else {
                        "${sub.name} trial ends $urgencyLower"
                    }

                    alerts.add(
                        RenewalAlert(
                            subscription = sub,
                            type = AlertType.TRIAL_EXPIRY,
                            targetDate = trialEnd,
                            daysRemaining = days,
                            formattedUrgency = urgency,
                            displayText = displayText
                        )
                    )
                }
            } else if (sub.status == SubscriptionStatus.ACTIVE) {
                // 2. Check active subscription renewal for non-trial subscriptions
                val dueDate = sub.nextDueDate
                if (!dueDate.isBefore(today) && !dueDate.isAfter(maxDate)) {
                    val days = ChronoUnit.DAYS.between(today, dueDate)
                    val urgency = formatUrgency(days)
                    val formattedAmount = IndianCurrencyFormatter.format(sub.amountMinor)
                    val urgencyLower = urgency.lowercase()
                    val displayText = "${sub.name} renews $urgencyLower · $formattedAmount"

                    alerts.add(
                        RenewalAlert(
                            subscription = sub,
                            type = AlertType.RENEWAL,
                            targetDate = dueDate,
                            daysRemaining = days,
                            formattedUrgency = urgency,
                            displayText = displayText
                        )
                    )
                }
            }
        }

        return alerts.sortedWith(compareBy({ it.targetDate }, { it.subscription.name }))
    }

    fun formatUrgency(days: Long): String {
        return when (days) {
            0L -> "Today"
            1L -> "Tomorrow"
            else -> "In $days days"
        }
    }
}
