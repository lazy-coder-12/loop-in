package com.loopin.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.domain.logic.IndianCurrencyFormatter
import com.loopin.app.domain.model.Subscription
import com.loopin.app.domain.model.SubscriptionStatus
import com.loopin.app.ui.components.BadgeStatusType
import com.loopin.app.ui.components.MonogramTile
import com.loopin.app.ui.components.StatusBadge
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.ElectricBlue
import com.loopin.app.ui.theme.NeutralGray
import java.time.format.DateTimeFormatter

@Composable
fun RecentSubscriptionsSection(
    subscriptions: List<Subscription>,
    modifier: Modifier = Modifier,
    onViewAllClick: () -> Unit = {},
    onSubscriptionClick: (Long) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        // Section Header: "Recent Subscription" & "View All"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Subscription",
                color = DeepMidnight,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = "View All",
                color = ElectricBlue,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier
                    .clickable { onViewAllClick() }
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Subscription Cards
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            subscriptions.forEach { subscription ->
                SubscriptionItemCard(
                    subscription = subscription,
                    onClick = { onSubscriptionClick(subscription.id) }
                )
            }
        }
    }
}

@Composable
fun SubscriptionItemCard(
    subscription: Subscription,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM")
    val formattedDueDate = "Due ${subscription.nextDueDate.format(dateFormatter)}"
    val formattedAmount = formatCurrency(subscription.amountMinor)

    val cadenceDisplay = when (subscription.billingCycle) {
        com.loopin.app.domain.model.BillingCycle.WEEKLY -> "Weekly"
        com.loopin.app.domain.model.BillingCycle.MONTHLY -> "Monthly"
        com.loopin.app.domain.model.BillingCycle.QUARTERLY -> "Quarterly"
        com.loopin.app.domain.model.BillingCycle.HALF_YEARLY -> "Half-Yearly"
        com.loopin.app.domain.model.BillingCycle.YEARLY -> "Yearly"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF7F8FA))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Monogram Tile (48x48dp, 14dp radius)
        MonogramTile(
            name = subscription.name,
            size = 48.dp,
            cornerRadius = 14.dp
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Middle: Name and Subtitle
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = subscription.name,
                color = DeepMidnight,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$cadenceDisplay • ${subscription.category}",
                    color = NeutralGray,
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp
                )
                if (subscription.status == SubscriptionStatus.SUSPECTED) {
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusBadge(status = BadgeStatusType.REVIEW)
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right: Amount and Due Date
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = formattedAmount,
                color = DeepMidnight,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = formattedDueDate,
                color = NeutralGray,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp
            )
        }
    }
}

private fun formatCurrency(amountMinor: Long): String {
    val raw = IndianCurrencyFormatter.format(amountMinor)
    return if (raw.startsWith("₹")) {
        "₹ " + raw.substring(1)
    } else {
        raw
    }
}
