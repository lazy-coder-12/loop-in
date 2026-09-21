package com.loopin.app.ui.subscription

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.loopin.app.ui.theme.FluentIcons
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.R
import com.loopin.app.data.repository.SampleDataProvider
import com.loopin.app.data.repository.SubscriptionRepository
import com.loopin.app.domain.model.BillingCycle
import com.loopin.app.domain.model.Subscription
import com.loopin.app.domain.model.SubscriptionStatus
import com.loopin.app.ui.components.LoopInBottomBar
import com.loopin.app.ui.components.LoopInTab
import com.loopin.app.ui.components.MonogramTile
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.ElectricBlue
import com.loopin.app.ui.theme.LoopInTheme
import com.loopin.app.ui.theme.NeutralGray
import com.loopin.app.ui.theme.PureWhite
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SubscriptionsListScreen(
    repository: SubscriptionRepository,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSubscriptionClick: (Long) -> Unit = {},
    onAddClick: () -> Unit = {},
    onTabSelected: (LoopInTab) -> Unit = {}
) {
    val subscriptions by repository.getSubscriptionsFlow().collectAsState(initial = emptyList())

    SubscriptionsListContent(
        subscriptions = subscriptions,
        modifier = modifier,
        onBackClick = onBackClick,
        onNotificationsClick = onNotificationsClick,
        onSubscriptionClick = onSubscriptionClick,
        onAddClick = onAddClick,
        onTabSelected = onTabSelected
    )
}

@Composable
fun SubscriptionsListContent(
    subscriptions: List<Subscription>,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSubscriptionClick: (Long) -> Unit = {},
    onAddClick: () -> Unit = {},
    onTabSelected: (LoopInTab) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filterTabs = listOf("All", "Upcoming", "Expired", "Free Trial")

    // If Room is empty, fallback to rich sample items so the screen is immediately populated as in the design
    val displaySubscriptions = remember(subscriptions) {
        if (subscriptions.isNotEmpty()) {
            subscriptions
        } else {
            SampleDataProvider.getSampleSubscriptions()
        }
    }

    val filteredSubscriptions = remember(displaySubscriptions, selectedFilter, searchQuery) {
        displaySubscriptions.filter { sub ->
            val matchesFilter = when (selectedFilter) {
                "Upcoming" -> sub.status == SubscriptionStatus.ACTIVE
                "Expired" -> sub.status == SubscriptionStatus.PAUSED
                "Free Trial" -> sub.isTrial
                else -> true
            }

            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                sub.name.contains(searchQuery.trim(), ignoreCase = true) ||
                    sub.category.contains(searchQuery.trim(), ignoreCase = true)
            }

            matchesFilter && matchesSearch
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            LoopInBottomBar(
                selectedTab = LoopInTab.MY_SUBS,
                onTabSelected = onTabSelected
            )
        },
        containerColor = PureWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .statusBarsPadding()
                .background(PureWhite)
        ) {
            // Top App Bar
            MySubsHeader(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar: "Search apps, tools and more"
            MySubsSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips: All, Upcoming, Expired, Free Trial
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterTabs) { tab ->
                    val isSelected = tab == selectedFilter
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ElectricBlue else PureWhite)
                            .then(
                                if (!isSelected) {
                                    Modifier.border(
                                        BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                        RoundedCornerShape(12.dp)
                                    )
                                } else {
                                    Modifier
                                }
                            )
                            .selectable(
                                selected = isSelected,
                                onClick = { selectedFilter = tab },
                                role = Role.Tab
                            )
                            .padding(horizontal = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontFamily = AppFontFamily,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 14.sp,
                            color = if (isSelected) PureWhite else Color(0xFF6B7280)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subscriptions List
            if (filteredSubscriptions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No subscriptions found",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DeepMidnight
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) {
                                "No matches for \"$searchQuery\""
                            } else {
                                "No subscriptions in '$selectedFilter'"
                            },
                            fontFamily = AppFontFamily,
                            fontSize = 14.sp,
                            color = NeutralGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(filteredSubscriptions, key = { it.id }) { subscription ->
                        SubscriptionRowItem(
                            subscription = subscription,
                            onClick = { onSubscriptionClick(subscription.id) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = Color(0xFFF3F4F6),
                            thickness = 1.dp
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionRowItem(
    subscription: Subscription,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brandIconRes = getServiceIconRes(subscription.name)
    val formattedPrice = "₹ ${subscription.amountMinor / 100}"
    val cycleLabel = when (subscription.billingCycle) {
        BillingCycle.WEEKLY -> "Weekly"
        BillingCycle.MONTHLY -> "Monthly"
        BillingCycle.QUARTERLY -> "Quarterly"
        BillingCycle.HALF_YEARLY -> "Half-Yearly"
        BillingCycle.YEARLY -> "Yearly"
    }

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH)
    }
    val dueText = "Due ${subscription.nextDueDate.format(dateFormatter)}"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Icon: 56dp squircle
        if (brandIconRes != null) {
            Image(
                painter = painterResource(id = brandIconRes),
                contentDescription = subscription.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        } else {
            MonogramTile(
                name = subscription.name,
                size = 56.dp,
                cornerRadius = 16.dp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            // Row 1: Title and Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subscription.name,
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepMidnight
                )
                Text(
                    text = formattedPrice,
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepMidnight
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Row 2: "Monthly • Entertainment" and "Due 24 Sept"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$cycleLabel • ${subscription.category}",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = NeutralGray
                )
                Text(
                    text = dueText,
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = NeutralGray
                )
            }
        }
    }
}

private fun getServiceIconRes(name: String): Int? {
    val lower = name.lowercase()
    return when {
        lower.contains("hotstar") -> R.drawable.icon_hotstar
        lower.contains("claude") -> R.drawable.icon_claude
        lower.contains("figma") -> R.drawable.icon_figma
        lower.contains("linkedin") -> R.drawable.icon_linkedin
        lower.contains("netflix") -> R.drawable.card_netflix
        lower.contains("spotify") -> R.drawable.card_spotify
        lower.contains("gemini") -> R.drawable.card_gemini
        else -> null
    }
}

@Composable
private fun MySubsHeader(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button: 48dp accessible touch area
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable(
                    role = Role.Button,
                    onClickLabel = "Navigate back"
                ) { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF2F4F7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(FluentIcons.Back),
                    contentDescription = "Back",
                    tint = DeepMidnight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Center Title & Subtitle
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My Subs",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = DeepMidnight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Collection of your subscriptions",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = NeutralGray
            )
        }

        // Notification Bell Button: 48dp accessible touch area
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable(
                    role = Role.Button,
                    onClickLabel = "Open notifications"
                ) { onNotificationsClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PureWhite)
                    .border(
                        BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(FluentIcons.Alert),
                    contentDescription = "Notifications",
                    tint = DeepMidnight,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun MySubsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF9FAFB))
            .border(
                BorderStroke(1.dp, Color(0xFFEFEFEF)),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(FluentIcons.Search),
                contentDescription = "Search",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(
                    fontFamily = AppFontFamily,
                    fontSize = 14.sp,
                    color = DeepMidnight
                ),
                singleLine = true,
                cursorBrush = SolidColor(DeepMidnight),
                interactionSource = interactionSource,
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "Search apps, tools and more",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                    innerTextField()
                }
            )

            if (query.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = "Clear search text"
                        ) { onQueryChange("") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(FluentIcons.Dismiss),
                        contentDescription = "Clear search",
                        tint = NeutralGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SubscriptionsListScreenPreview() {
    val sampleSubs = SampleDataProvider.getSampleSubscriptions()
    LoopInTheme {
        SubscriptionsListContent(
            subscriptions = sampleSubs
        )
    }
}
