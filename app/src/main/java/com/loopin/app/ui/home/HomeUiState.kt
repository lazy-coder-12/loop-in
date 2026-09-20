package com.loopin.app.ui.home

import com.loopin.app.domain.model.RenewalAlert
import com.loopin.app.domain.model.Subscription

/**
 * UI State for the Home screen.
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "Anurag Verma",
    val greeting: String = "Good Afternoon!",
    val monthlySpendMinor: Long = 0L,
    val formattedMonthlySpend: String = "₹ 0",
    val activeSubscriptionsCount: Int = 0,
    val annualProjectedMinor: Long = 0L,
    val formattedAnnualSpend: String = "₹ 0 per year",
    val upcomingAlerts: List<RenewalAlert> = emptyList(),
    val isAlertsExpanded: Boolean = false,
    val recentSubscriptions: List<Subscription> = emptyList(),
    val totalSubscriptionsCount: Int = 0
) {
    val isEmpty: Boolean
        get() = !isLoading && totalSubscriptionsCount == 0
}
