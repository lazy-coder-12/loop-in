package com.loopin.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.loopin.app.R
import com.loopin.app.data.repository.SampleDataProvider
import com.loopin.app.domain.logic.AlertSelector
import com.loopin.app.domain.logic.RecentSubscriptionsSelector
import com.loopin.app.ui.components.LoopInBottomBar
import com.loopin.app.ui.components.LoopInTab
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.ElectricBlue
import com.loopin.app.ui.theme.LoopInTheme
import com.loopin.app.ui.theme.PureWhite

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onViewSubscriptionsClick: () -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onSubscriptionClick: (Long) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onAddSubscriptionClick: () -> Unit = {},
    onTabSelected: (LoopInTab) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        modifier = modifier,
        onToggleAlertsExpanded = viewModel::toggleAlertsExpanded,
        onViewSubscriptionsClick = onViewSubscriptionsClick,
        onViewAllClick = onViewAllClick,
        onSubscriptionClick = onSubscriptionClick,
        onSearchClick = onSearchClick,
        onNotificationsClick = onNotificationsClick,
        onAddSubscriptionClick = onAddSubscriptionClick,
        onSeedSampleData = viewModel::seedSampleData,
        onTabSelected = onTabSelected
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onToggleAlertsExpanded: () -> Unit = {},
    onViewSubscriptionsClick: () -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onSubscriptionClick: (Long) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onAddSubscriptionClick: () -> Unit = {},
    onSeedSampleData: () -> Unit = {},
    onTabSelected: (LoopInTab) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            LoopInBottomBar(
                selectedTab = LoopInTab.HOME,
                onTabSelected = onTabSelected
            )
        },
        containerColor = PureWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(scrollState)
                .background(PureWhite)
        ) {
            // Top Section Box: Striped background image with FIXED height (520dp)
            // It covers the Header, Spend Card, gap, and behind the top of ComingUpCard.
            // Because the image has a fixed height and top alignment, it NEVER resizes or zooms!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ElectricBlue)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_hero_orange),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier.matchParentSize()
                )

                // Hero Content: Header, Spend Card, and Coming Up card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    HeaderSection(
                        userName = uiState.userName,
                        greeting = uiState.greeting,
                        onSearchClick = onSearchClick,
                        onNotificationsClick = onNotificationsClick
                    )

                    SpendHeroCard(
                        formattedMonthlySpend = uiState.formattedMonthlySpend,
                        activeSubscriptionsCount = uiState.activeSubscriptionsCount,
                        formattedAnnualSpend = uiState.formattedAnnualSpend,
                        onViewSubscriptionsClick = onViewSubscriptionsClick
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Coming Up renewals card: Striped background follows through continuously
                    // behind its 24dp top rounded corners!
                    ComingUpCard(
                        alerts = uiState.upcomingAlerts,
                        isExpanded = uiState.isAlertsExpanded,
                        onToggleExpanded = onToggleAlertsExpanded
                    )
                }
            }

            // Bottom Section: Pure White surface with rounded top corners (24dp)
            // The DeepMidnight background underlay ensures the top corners blend seamlessly
            // into the dark navy ComingUpCard resting directly above it.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 360.dp)
                    .background(DeepMidnight)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(PureWhite)
            ) {
                if (uiState.isEmpty) {
                    EmptyHomeState(
                        onAddSubscriptionClick = onAddSubscriptionClick,
                        onSeedSampleDataClick = onSeedSampleData
                    )
                } else {
                    RecentSubscriptionsSection(
                        subscriptions = uiState.recentSubscriptions,
                        onViewAllClick = onViewAllClick,
                        onSubscriptionClick = onSubscriptionClick
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 860)
@Composable
fun HomeScreenPopulatedPreview() {
    val sampleSubs = SampleDataProvider.getSampleSubscriptions()
    val alerts = AlertSelector.selectAlerts(sampleSubs)
    val recent = RecentSubscriptionsSelector.selectRecent(sampleSubs, limit = 3)

    LoopInTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                isLoading = false,
                userName = "Anurag Verma",
                greeting = "Good Afternoon!",
                monthlySpendMinor = 291207L,
                formattedMonthlySpend = "₹ 2,912.07",
                activeSubscriptionsCount = 6,
                annualProjectedMinor = 3494484L,
                formattedAnnualSpend = "₹ 34, 944.84 per year",
                upcomingAlerts = alerts,
                recentSubscriptions = recent,
                totalSubscriptionsCount = sampleSubs.size
            )
        )
    }
}

@Preview(showBackground = true, heightDp = 860)
@Composable
fun HomeScreenEmptyPreview() {
    LoopInTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                isLoading = false,
                userName = "Anurag Verma",
                greeting = "Good Afternoon!",
                monthlySpendMinor = 0L,
                formattedMonthlySpend = "₹ 0",
                activeSubscriptionsCount = 0,
                annualProjectedMinor = 0L,
                formattedAnnualSpend = "₹ 0 per year",
                upcomingAlerts = emptyList(),
                recentSubscriptions = emptyList(),
                totalSubscriptionsCount = 0
            )
        )
    }
}
