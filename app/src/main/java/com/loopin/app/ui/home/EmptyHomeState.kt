package com.loopin.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.ui.components.LoopInPrimaryButton
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.ElectricBlue
import com.loopin.app.ui.theme.FluentIcons
import com.loopin.app.ui.theme.NeutralGray
import com.loopin.app.ui.theme.PureWhite

@Composable
fun EmptyHomeState(
    modifier: Modifier = Modifier,
    onAddSubscriptionClick: () -> Unit = {},
    onSeedSampleDataClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F1FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(FluentIcons.AlertFilled),
                contentDescription = null,
                tint = ElectricBlue,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Title
        Text(
            text = "Loop'in is watching for\npre-debit notices.",
            color = DeepMidnight,
            fontFamily = AppFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = "Your bank notification alerts will appear here automatically. You can also add your subscriptions manually.",
            color = NeutralGray,
            fontFamily = AppFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Add subscription button (Linear gradient #3D3C3B to #16110F with #FFFFFF text)
        LoopInPrimaryButton(
            onClick = onAddSubscriptionClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                painter = painterResource(FluentIcons.Add),
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "  Add Subscription",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = PureWhite
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick seed sample data button
        OutlinedButton(
            onClick = onSeedSampleDataClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                painter = painterResource(FluentIcons.Sparkle),
                contentDescription = null,
                tint = ElectricBlue,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "  Load Sample Subscriptions",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ElectricBlue
            )
        }
    }
}
