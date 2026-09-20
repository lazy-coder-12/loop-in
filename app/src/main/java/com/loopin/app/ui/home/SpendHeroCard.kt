package com.loopin.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.ui.theme.AccentGreen
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.PureWhite

@Composable
fun SpendHeroCard(
    formattedMonthlySpend: String,
    activeSubscriptionsCount: Int,
    formattedAnnualSpend: String,
    modifier: Modifier = Modifier,
    onViewSubscriptionsClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Upper label: [Icon] MONTHLY SPEND
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Payments,
                contentDescription = null,
                tint = PureWhite.copy(alpha = 0.9f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MONTHLY SPEND",
                color = PureWhite.copy(alpha = 0.9f),
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Large Amount: ₹ 2,912.07
        Text(
            text = formattedMonthlySpend,
            color = PureWhite,
            fontFamily = AppFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 44.sp,
            lineHeight = 48.sp,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Subtitle: "6 active subscriptions • ₹ 34,944.84 per year"
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$activeSubscriptionsCount active subscriptions",
                color = PureWhite.copy(alpha = 0.85f),
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Green separation dot
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(AccentGreen)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formattedAnnualSpend,
                color = PureWhite.copy(alpha = 0.85f),
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // White Pill Button: "View Subscriptions"
        Button(
            onClick = onViewSubscriptionsClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = PureWhite,
                contentColor = DeepMidnight
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "View Subscriptions",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = DeepMidnight
            )
        }
    }
}
