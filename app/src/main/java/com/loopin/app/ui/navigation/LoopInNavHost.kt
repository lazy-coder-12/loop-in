package com.loopin.app.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.loopin.app.di.AppContainer
import com.loopin.app.ui.components.AddSubscriptionOverlay
import com.loopin.app.ui.components.LoopInTab
import com.loopin.app.ui.discover.DiscoverScreen
import com.loopin.app.ui.discover.PlanDetailsScreen
import com.loopin.app.ui.discover.ServicePlansScreen
import com.loopin.app.ui.home.HomeScreen
import com.loopin.app.ui.home.HomeViewModel
import com.loopin.app.ui.notifications.NotificationsScreen
import com.loopin.app.ui.settings.ProfileScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.loopin.app.detection.model.ParsedDebitAlert
import com.loopin.app.detection.ui.AutoDetectSheet
import com.loopin.app.detection.ui.DetectionConfirmationSheet
import kotlinx.coroutines.launch
import com.loopin.app.ui.subscription.NewSubscriptionScreen
import com.loopin.app.ui.subscription.SubscriptionDetailScreen
import com.loopin.app.ui.subscription.SubscriptionsListScreen

private fun getTabIndex(route: String?): Int {
    return when (route) {
        NavRoutes.Home.route -> 0
        NavRoutes.Discover.route -> 1
        NavRoutes.NewSubscription.route -> 2
        NavRoutes.MySubs.route -> 3
        NavRoutes.Profile.route -> 4
        else -> -1
    }
}

@Composable
fun LoopInNavHost(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val coroutineScope = rememberCoroutineScope()
    val detectionManager = appContainer.detectionManager
    val activeAlert by detectionManager.activeAlertForConfirmation.collectAsState()
    var showAddSubscriptionOverlay by remember { mutableStateOf(false) }
    var showAutoDetectSheet by remember { mutableStateOf(false) }
    var editingDetectedAlert by remember { mutableStateOf<ParsedDebitAlert?>(null) }

    fun navigateToNewSubscriptionDirectly() {
        navController.navigate(NavRoutes.NewSubscription.route) {
            launchSingleTop = true
        }
    }

    fun navigateToTab(tab: LoopInTab) {
        if (tab == LoopInTab.NEW) {
            showAddSubscriptionOverlay = true
            return
        }

        val targetRoute = when (tab) {
            LoopInTab.HOME -> NavRoutes.Home.route
            LoopInTab.DISCOVER -> NavRoutes.Discover.route
            LoopInTab.NEW -> NavRoutes.NewSubscription.route
            LoopInTab.MY_SUBS -> NavRoutes.MySubs.route
            LoopInTab.PROFILE -> NavRoutes.Profile.route
        }

        val currentRoute = navController.currentDestination?.route

        // Don't re-navigate if already on this route
        if (currentRoute == targetRoute) {
            return
        }

        // If user is inside nested ServicePlans or PlanDetail and taps Discover in bottom bar,
        // pop back up to Discover.
        if (tab == LoopInTab.DISCOVER && (currentRoute?.startsWith("service_plans") == true || currentRoute?.startsWith("plan_detail") == true)) {
            navController.popBackStack(NavRoutes.Discover.route, inclusive = false)
            return
        }

        navController.navigate(targetRoute) {
            launchSingleTop = true
        }
    }

    fun handleBackClick() {
        val popped = navController.popBackStack()
        if (!popped) {
            navController.navigate(NavRoutes.Home.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Home.route,
            modifier = Modifier.fillMaxSize(),
            // Directional slide-in for forward navigation
            enterTransition = {
                val initialIndex = getTabIndex(initialState.destination.route)
                val targetIndex = getTabIndex(targetState.destination.route)
                if (initialIndex != -1 && targetIndex != -1 && targetIndex < initialIndex) {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                } else {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                }
            },
            exitTransition = {
                val initialIndex = getTabIndex(initialState.destination.route)
                val targetIndex = getTabIndex(targetState.destination.route)
                if (initialIndex != -1 && targetIndex != -1 && targetIndex < initialIndex) {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                } else {
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                }
            },
            // Move out transition revealing the previous screen beneath
            popEnterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)) +
                    scaleIn(
                        initialScale = 0.95f,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                )
            }
        ) {
            composable(NavRoutes.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.provideFactory(appContainer.subscriptionRepository)
                )

                HomeScreen(
                    viewModel = homeViewModel,
                    onViewSubscriptionsClick = { navigateToTab(LoopInTab.MY_SUBS) },
                    onViewAllClick = { navigateToTab(LoopInTab.MY_SUBS) },
                    onSubscriptionClick = { id ->
                        navController.navigate(NavRoutes.SubscriptionDetail.createRoute(id))
                    },
                    onSearchClick = { navigateToTab(LoopInTab.DISCOVER) },
                    onNotificationsClick = {
                        navController.navigate(NavRoutes.Notifications.route)
                    },
                    onAddSubscriptionClick = { showAddSubscriptionOverlay = true },
                    onTabSelected = ::navigateToTab
                )
            }

            composable(NavRoutes.Discover.route) {
                DiscoverScreen(
                    onBackClick = { handleBackClick() },
                    onNotificationsClick = {
                        navController.navigate(NavRoutes.Notifications.route)
                    },
                    onServiceClick = { serviceId ->
                        navController.navigate(NavRoutes.ServicePlans.createRoute(serviceId))
                    },
                    onTabSelected = ::navigateToTab
                )
            }

            composable(NavRoutes.NewSubscription.route) {
                val detected = editingDetectedAlert
                NewSubscriptionScreen(
                    repository = appContainer.subscriptionRepository,
                    initialName = detected?.matchedServiceName ?: "",
                    initialCategory = detected?.category ?: "Entertainment",
                    initialAmountMinor = detected?.amountMinor,
                    initialDueDate = detected?.debitDate,
                    onBackClick = {
                        editingDetectedAlert = null
                        handleBackClick()
                    },
                    onCancelClick = {
                        editingDetectedAlert = null
                        handleBackClick()
                    },
                    onSaveSuccess = {
                        editingDetectedAlert = null
                        navigateToTab(LoopInTab.MY_SUBS)
                    },
                    onTabSelected = ::navigateToTab
                )
            }

            composable(NavRoutes.MySubs.route) {
                SubscriptionsListScreen(
                    repository = appContainer.subscriptionRepository,
                    onBackClick = { handleBackClick() },
                    onNotificationsClick = {
                        navController.navigate(NavRoutes.Notifications.route)
                    },
                    onSubscriptionClick = { id ->
                        navController.navigate(NavRoutes.SubscriptionDetail.createRoute(id))
                    },
                    onAddClick = {
                        showAddSubscriptionOverlay = true
                    },
                    onTabSelected = ::navigateToTab
                )
            }

            composable(NavRoutes.Profile.route) {
                ProfileScreen(
                    repository = appContainer.subscriptionRepository,
                    onBackClick = { handleBackClick() },
                    onNotificationsClick = {
                        navController.navigate(NavRoutes.Notifications.route)
                    },
                    onTabSelected = ::navigateToTab
                )
            }

            // Deeper screen: Notifications moves in from bottom to top; pops down revealing screen beneath
            composable(
                route = NavRoutes.Notifications.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                    )
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)) +
                        scaleIn(
                            initialScale = 0.95f,
                            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                        )
                },
                popExitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
                    )
                }
            ) {
                NotificationsScreen(
                    repository = appContainer.subscriptionRepository,
                    onBackClick = { handleBackClick() },
                    onSubscriptionClick = { id ->
                        navController.navigate(NavRoutes.SubscriptionDetail.createRoute(id))
                    }
                )
            }

            // Deeper screen: Subscription Detail moves in from bottom to top; pops down revealing screen beneath
            composable(
                route = NavRoutes.SubscriptionDetail.route,
                arguments = listOf(
                    navArgument(NavRoutes.SubscriptionDetail.ARG_SUBSCRIPTION_ID) {
                        type = NavType.LongType
                    }
                ),
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                    )
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)) +
                        scaleIn(
                            initialScale = 0.95f,
                            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                        )
                },
                popExitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
                    )
                }
            ) { backStackEntry ->
                val subscriptionId = backStackEntry.arguments?.getLong(
                    NavRoutes.SubscriptionDetail.ARG_SUBSCRIPTION_ID
                ) ?: 0L

                SubscriptionDetailScreen(
                    subscriptionId = subscriptionId,
                    repository = appContainer.subscriptionRepository,
                    onBackClick = { handleBackClick() }
                )
            }

            // Deeper screen: Service Plans (e.g. Netflix) moves in from bottom to top; pops down revealing screen beneath
            composable(
                route = NavRoutes.ServicePlans.route,
                arguments = listOf(
                    navArgument(NavRoutes.ServicePlans.ARG_SERVICE_ID) {
                        type = NavType.StringType
                    }
                ),
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                },
                popExitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
                    )
                }
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString(
                    NavRoutes.ServicePlans.ARG_SERVICE_ID
                ) ?: "netflix"

                ServicePlansScreen(
                    serviceId = serviceId,
                    repository = appContainer.subscriptionRepository,
                    onBackClick = { handleBackClick() },
                    onPlanClick = { sId, planId ->
                        navController.navigate(NavRoutes.PlanDetail.createRoute(sId, planId))
                    },
                    onTabSelected = ::navigateToTab
                )
            }

            // Deeper screen: Plan Details slides in horizontally from right; pops out sliding to the right
            composable(
                route = NavRoutes.PlanDetail.route,
                arguments = listOf(
                    navArgument(NavRoutes.PlanDetail.ARG_SERVICE_ID) {
                        type = NavType.StringType
                    },
                    navArgument(NavRoutes.PlanDetail.ARG_PLAN_ID) {
                        type = NavType.StringType
                    }
                ),
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                }
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString(
                    NavRoutes.PlanDetail.ARG_SERVICE_ID
                ) ?: "netflix"
                val planId = backStackEntry.arguments?.getString(
                    NavRoutes.PlanDetail.ARG_PLAN_ID
                ) ?: "mobile"

                PlanDetailsScreen(
                    serviceId = serviceId,
                    planId = planId,
                    repository = appContainer.subscriptionRepository,
                    onBackClick = { handleBackClick() },
                    onTabSelected = ::navigateToTab
                )
            }
        }

        // Bouncy Scale Animated Add Subscription Overlay
        if (showAddSubscriptionOverlay) {
            AddSubscriptionOverlay(
                onDismiss = {
                    showAddSubscriptionOverlay = false
                },
                onManualEntry = {
                    showAddSubscriptionOverlay = false
                    editingDetectedAlert = null
                    navigateToNewSubscriptionDirectly()
                },
                onAutoDetect = {
                    showAddSubscriptionOverlay = false
                    showAutoDetectSheet = true
                }
            )
        }

        // RBI Pre-Debit Detection & Simulation Sheet
        if (showAutoDetectSheet) {
            AutoDetectSheet(
                detectionManager = detectionManager,
                onDismiss = {
                    showAutoDetectSheet = false
                }
            )
        }

        // Detection Confirmation Sheet (Triggered live or via simulation)
        activeAlert?.let { alert ->
            DetectionConfirmationSheet(
                alert = alert,
                onConfirm = {
                    coroutineScope.launch {
                        detectionManager.confirmAlertAndBuildSubscription(alert)
                    }
                },
                onEdit = {
                    editingDetectedAlert = alert
                    detectionManager.dismissActiveAlert()
                    navigateToNewSubscriptionDirectly()
                },
                onDismiss = {
                    detectionManager.dismissActiveAlert()
                }
            )
        }
    }
}
