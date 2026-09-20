package com.loopin.app.domain.model

enum class BillingCycle {
    WEEKLY,
    MONTHLY,
    QUARTERLY,
    HALF_YEARLY,
    YEARLY
}

enum class SubscriptionStatus {
    SUSPECTED,
    ACTIVE,
    PAUSED,
    ENDED
}

enum class SubscriptionSource {
    AUTO,
    MANUAL
}
