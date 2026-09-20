package com.loopin.app.domain.model

import java.time.LocalDate

enum class AlertType {
    RENEWAL,
    TRIAL_EXPIRY
}

/**
 * Domain representation of an urgent upcoming event (renewal or trial expiry).
 */
data class RenewalAlert(
    val subscription: Subscription,
    val type: AlertType,
    val targetDate: LocalDate,
    val daysRemaining: Long,
    val formattedUrgency: String, // "Today", "Tomorrow", "In N days"
    val displayText: String
)
