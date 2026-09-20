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
    data object ServicePlans : NavRoutes("service_plans/{serviceId}") {
        const val ARG_SERVICE_ID = "serviceId"
        fun createRoute(serviceId: String): String = "service_plans/$serviceId"
    }
    data object PlanDetail : NavRoutes("plan_detail/{serviceId}/{planId}") {
        const val ARG_SERVICE_ID = "serviceId"
        const val ARG_PLAN_ID = "planId"
        fun createRoute(serviceId: String, planId: String): String = "plan_detail/$serviceId/$planId"
    }
}
