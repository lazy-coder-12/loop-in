package com.loopin.app.ui.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.annotation.DrawableRes
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.data.repository.SampleDataProvider
import com.loopin.app.data.repository.SubscriptionRepository
import com.loopin.app.domain.logic.IndianCurrencyFormatter
import com.loopin.app.domain.model.BillingCycle
import com.loopin.app.domain.model.Subscription
import com.loopin.app.domain.model.SubscriptionSource
import com.loopin.app.domain.model.SubscriptionStatus
import com.loopin.app.ui.components.BadgeStatusType
import com.loopin.app.ui.components.MonogramTile
import com.loopin.app.ui.components.StatusBadge
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.BorderLight
import com.loopin.app.ui.theme.DangerRed
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.ElectricBlue
import com.loopin.app.ui.theme.FluentIcons
import com.loopin.app.ui.theme.LoopInTheme
import com.loopin.app.ui.theme.NeutralGray
import com.loopin.app.ui.theme.PureWhite
import java.time.format.DateTimeFormatter

@Composable
fun SubscriptionDetailScreen(
    subscriptionId: Long,
    repository: SubscriptionRepository,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val subscription by repository.getSubscriptionById(subscriptionId).collectAsState(initial = null)

    SubscriptionDetailContent(
        subscription = subscription,
        modifier = modifier,
        onBackClick = onBackClick
    )
}

@Composable
fun SubscriptionDetailContent(
    subscription: Subscription?,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = PureWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Top Navigation Bar with Back Button
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

                Text(
                    text = "Subscription Details",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DeepMidnight
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (subscription == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading subscription...",
                        fontFamily = AppFontFamily,
                        color = NeutralGray
                    )
                }
            } else {
                val cadenceText = when (subscription.billingCycle) {
                    BillingCycle.WEEKLY -> "Weekly"
                    BillingCycle.MONTHLY -> "Monthly"
                    BillingCycle.QUARTERLY -> "Quarterly"
                    BillingCycle.HALF_YEARLY -> "Half-Yearly"
                    BillingCycle.YEARLY -> "Yearly"
                }

                // Subscription Hero Header Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFF7F8FA))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MonogramTile(
                        name = subscription.name,
                        size = 64.dp,
                        cornerRadius = 20.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = subscription.name,
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = DeepMidnight
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$cadenceText • ${subscription.category}",
                        fontFamily = AppFontFamily,
                        fontSize = 14.sp,
                        color = NeutralGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val badgeType = when (subscription.status) {
                        SubscriptionStatus.ACTIVE -> BadgeStatusType.ACTIVE
                        SubscriptionStatus.SUSPECTED -> BadgeStatusType.REVIEW
                        SubscriptionStatus.PAUSED -> BadgeStatusType.PAUSED
                        SubscriptionStatus.ENDED -> BadgeStatusType.ENDED
                    }
                    StatusBadge(status = badgeType)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Breakdown Items
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
                        .padding(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

                    DetailRow(
                        iconRes = FluentIcons.Payment,
                        label = "Amount",
                        value = "₹ " + IndianCurrencyFormatter.format(subscription.amountMinor).removePrefix("₹")
                    )
                    HorizontalDivider(color = BorderLight, thickness = 1.dp)

                    DetailRow(
                        iconRes = FluentIcons.Calendar,
                        label = "Next Due Date",
                        value = subscription.nextDueDate.format(dateFormatter)
                    )
                    HorizontalDivider(color = BorderLight, thickness = 1.dp)

                    DetailRow(
                        iconRes = FluentIcons.Tag,
                        label = "Billing Cycle",
                        value = cadenceText
                    )
                    HorizontalDivider(color = BorderLight, thickness = 1.dp)

                    val sourceText = when (subscription.source) {
                        SubscriptionSource.AUTO -> "RBI Pre-Debit Signal"
                        SubscriptionSource.MANUAL -> "Manual Entry"
                    }

                    DetailRow(
                        iconRes = FluentIcons.ShieldCheckmark,
                        label = "Detection Signal",
                        value = sourceText
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Signal Info Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF0F6FF))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            painter = painterResource(FluentIcons.Info),
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Detected from bank e-mandate notifications mandated by RBI. No SMS reading or account linking required.",
                            fontFamily = AppFontFamily,
                            fontSize = 13.sp,
                            color = DeepMidnight,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Action Buttons
                OutlinedButton(
                    onClick = { /* Placeholder action */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepMidnight)
                ) {
                    Icon(
                        painter = painterResource(FluentIcons.Pause),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pause Tracking",
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { /* Placeholder action */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed)
                ) {
                    Icon(
                        painter = painterResource(FluentIcons.Delete),
                        contentDescription = null,
                        tint = DangerRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Remove Subscription",
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = DangerRed
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(
    @DrawableRes iconRes: Int,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = NeutralGray,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                fontFamily = AppFontFamily,
                fontSize = 14.sp,
                color = NeutralGray
            )
        }
        Text(
            text = value,
            fontFamily = AppFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = DeepMidnight
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SubscriptionDetailScreenPreview() {
    val sample = SampleDataProvider.getSampleSubscriptions().first()
    LoopInTheme {
        SubscriptionDetailContent(
            subscription = sample
        )
    }
}
