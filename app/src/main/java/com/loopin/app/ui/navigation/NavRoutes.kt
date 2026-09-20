package com.loopin.app.ui.navigation

sealed class NavRoutes(val route: String) {
    data object Home : NavRoutes("home")
    data object Discover : NavRoutes("discover")
    data object NewSubscription : NavRoutes("new_subscription")
    data object MySubs : NavRoutes("my_subs")
    data object Profile : NavRoutes("profile")
    data object Notifications : NavRoutes("notifications")
    data object SubscriptionDetail : NavRoutes("subscription_detail/{subscriptionId}") {
        const val ARG_SUBSCRIPTION_ID = "subscriptionId"
        fun createRoute(subscriptionId: Long): String = "subscription_detail/$subscriptionId"
    }
}
