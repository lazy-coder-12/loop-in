package com.loopin.app.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.loopin.app.ui.theme.FluentIcons
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.data.repository.SampleDataProvider
import com.loopin.app.data.repository.SubscriptionRepository
import com.loopin.app.domain.logic.AlertSelector
import com.loopin.app.domain.model.RenewalAlert
import com.loopin.app.ui.components.MonogramTile
import com.loopin.app.ui.theme.AccentGreen
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.LoopInTheme
import com.loopin.app.ui.theme.NeutralGray
import com.loopin.app.ui.theme.PureWhite

@Composable
fun NotificationsScreen(
    repository: SubscriptionRepository,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSubscriptionClick: (Long) -> Unit = {}
) {
    val subscriptions by repository.getSubscriptionsFlow().collectAsState(initial = emptyList())
    val alerts = remember(subscriptions) {
        AlertSelector.selectAlerts(subscriptions)
    }

    NotificationsContent(
        alerts = alerts,
        modifier = modifier,
        onBackClick = onBackClick,
        onSubscriptionClick = onSubscriptionClick
    )
}

@Composable
fun NotificationsContent(
    alerts: List<RenewalAlert>,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSubscriptionClick: (Long) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = PureWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Top Bar with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF7F8FA))
                ) {
                    Icon(
                        painter = painterResource(FluentIcons.Back),
                        contentDescription = "Back",
                        tint = DeepMidnight,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Upcoming Renewals",
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = DeepMidnight
                    )
                    Text(
                        text = "Next 7 days renewal alerts",
                        fontFamily = AppFontFamily,
                        fontSize = 13.sp,
                        color = NeutralGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (alerts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(FluentIcons.CheckmarkCircle),
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "All clear!",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DeepMidnight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No renewals due in the next 7 days",
                            fontFamily = AppFontFamily,
                            fontSize = 14.sp,
                            color = NeutralGray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(alerts, key = { it.subscription.id }) { alert ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF7F8FA))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MonogramTile(
                                name = alert.subscription.name,
                                size = 44.dp,
                                cornerRadius = 14.dp
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = alert.subscription.name,
                                    fontFamily = AppFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = DeepMidnight
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = alert.displayText,
                                    fontFamily = AppFontFamily,
                                    fontSize = 13.sp,
                                    color = NeutralGray
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = alert.formattedUrgency,
                                    fontFamily = AppFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = AccentGreen
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    val sample = SampleDataProvider.getSampleSubscriptions()
    val alerts = AlertSelector.selectAlerts(sample)
    LoopInTheme {
        NotificationsContent(alerts = alerts)
    }
}
