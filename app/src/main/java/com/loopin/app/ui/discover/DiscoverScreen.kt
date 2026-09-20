package com.loopin.app.ui.discover

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.R
import com.loopin.app.ui.components.LoopInBottomBar
import com.loopin.app.ui.components.LoopInTab
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.LoopInTheme
import com.loopin.app.ui.theme.NeutralGray
import com.loopin.app.ui.theme.PureWhite

data class DiscoverService(
    val name: String,
    val category: String = "Popular",
    val startingPrice: String = ""
)

private data class PopularItem(
    val id: String,
    val name: String,
    val plansCount: Int,
    val imageRes: Int,
    val startingPrice: String
)

private data class AppServiceItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val plansCount: Int,
    val iconRes: Int,
    val startingPrice: String
)

@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSubscriptionClick: (Long) -> Unit = {},
    onServiceSelected: (DiscoverService) -> Unit = {},
    onTabSelected: (LoopInTab) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val popularItems = remember {
        listOf(
            PopularItem("netflix", "Netflix", 4, R.drawable.card_netflix, "₹ 199 / mo"),
            PopularItem("spotify", "Spotify", 3, R.drawable.card_spotify, "₹ 119 / mo"),
            PopularItem("gemini", "Gemini", 3, R.drawable.card_gemini, "₹ 1,950 / mo")
        )
    }

    val appServices = remember {
        listOf(
            AppServiceItem(
                id = "hotstar",
                name = "Jio Hotstar",
                subtitle = "Micro shows on Tadka, endless sports, entertain...",
                plansCount = 3,
                iconRes = R.drawable.icon_hotstar,
                startingPrice = "₹ 499 / yr"
            ),
            AppServiceItem(
                id = "claude",
                name = "Claude AI",
                subtitle = "Chat with AI: Your smart writing assistant, AI gen...",
                plansCount = 3,
                iconRes = R.drawable.icon_claude,
                startingPrice = "₹ 1,999 / mo"
            ),
            AppServiceItem(
                id = "figma",
                name = "Figma",
                subtitle = "The collaborative canvas for design, code and AI.",
                plansCount = 4,
                iconRes = R.drawable.icon_figma,
                startingPrice = "₹ 1,200 / mo"
            ),
            AppServiceItem(
                id = "linkedin",
                name = "Linkedin",
                subtitle = "Find Jobs, Insights and News.",
                plansCount = 4,
                iconRes = R.drawable.icon_linkedin,
                startingPrice = "₹ 1,499 / mo"
            )
        )
    }

    val filteredPopular = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            popularItems
        } else {
            popularItems.filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }
        }
    }

    val filteredApps = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            appServices
        } else {
            appServices.filter {
                it.name.contains(searchQuery.trim(), ignoreCase = true) ||
                    it.subtitle.contains(searchQuery.trim(), ignoreCase = true)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            LoopInBottomBar(
                selectedTab = LoopInTab.DISCOVER,
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
                .verticalScroll(scrollState)
                .background(PureWhite)
        ) {
            // Top App Bar
            ExploreHeader(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Search Bar
            ExploreSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            // Most Popular Section
            if (filteredPopular.isNotEmpty()) {
                Text(
                    text = "Most Popular",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DeepMidnight,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    filteredPopular.forEach { item ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    val subId = when (item.id) {
                                        "netflix" -> 2L
                                        "spotify" -> 3L
                                        "gemini" -> 2L
                                        else -> 2L
                                    }
                                    onSubscriptionClick(subId)
                                    onServiceSelected(
                                        DiscoverService(
                                            name = item.name,
                                            category = "Popular",
                                            startingPrice = item.startingPrice
                                        )
                                    )
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = item.imageRes),
                                contentDescription = item.name,
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(135f / 126f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.name,
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = DeepMidnight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${item.plansCount} Plans",
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = NeutralGray
                            )
                        }
                    }
                }
            }

            // Apps & Services Section
            if (filteredApps.isNotEmpty()) {
                Text(
                    text = "Apps & Services",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DeepMidnight,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)
                )

                filteredApps.forEachIndexed { index, app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val subId = when (app.id) {
                                    "hotstar" -> 1L
                                    "claude" -> 4L
                                    else -> 2L
                                }
                                onSubscriptionClick(subId)
                                onServiceSelected(
                                    DiscoverService(
                                        name = app.name,
                                        category = "Apps & Services",
                                        startingPrice = app.startingPrice
                                    )
                                )
                            }
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // App Squircle Icon
                        Image(
                            painter = painterResource(id = app.iconRes),
                            contentDescription = app.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        // App Details
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = app.name,
                                    fontFamily = AppFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = DeepMidnight
                                )

                                PlanIndicatorBadge(plansCount = app.plansCount)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = app.subtitle,
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                color = NeutralGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (index < filteredApps.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = Color(0xFFF3F4F6),
                            thickness = 1.dp
                        )
                    }
                }
            }

            // Empty state if nothing matches search
            if (filteredPopular.isEmpty() && filteredApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No results found",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DeepMidnight
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No subscriptions matched \"$searchQuery\"",
                            fontFamily = AppFontFamily,
                            fontSize = 14.sp,
                            color = NeutralGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ExploreHeader(
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
        // Back Button: 48dp accessible touch target
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
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos,
                    contentDescription = "Back",
                    tint = DeepMidnight,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(start = 4.dp)
                )
            }
        }

        // Center Title & Subtitle
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Explore",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = DeepMidnight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Browse popular subscriptions",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = NeutralGray
            )
        }

        // Notification Bell Button: 48dp accessible touch target
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
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = DeepMidnight,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun ExploreSearchBar(
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
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(22.dp)
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
                            text = "Search Netflix, Spotify, Gemini and more",
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
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear search",
                        tint = NeutralGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanIndicatorBadge(
    plansCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        modifier = modifier
    ) {
        repeat(plansCount) {
            Canvas(modifier = Modifier.size(7.dp)) {
                val path = Path().apply {
                    moveTo(size.width / 2f, 0f)
                    lineTo(size.width, size.height / 2f)
                    lineTo(size.width / 2f, size.height)
                    lineTo(0f, size.height / 2f)
                    close()
                }
                drawPath(path, color = Color(0xFFE95810))
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "$plansCount Plans",
            fontFamily = AppFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = NeutralGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DiscoverScreenPreview() {
    LoopInTheme {
        DiscoverScreen()
    }
}
