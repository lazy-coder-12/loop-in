package com.loopin.app.domain.logic

import com.loopin.app.domain.model.Subscription

/**
 * Pure domain logic to select the most recently added subscriptions.
 * Requirements:
 * - Newest 3 subscriptions by createdAt descending.
 * - Includes SUSPECTED subscriptions.
 */
object RecentSubscriptionsSelector {

    fun selectRecent(subscriptions: List<Subscription>, limit: Int = 3): List<Subscription> {
        return subscriptions
            .sortedByDescending { it.createdAt }
            .take(limit)
    }
}
