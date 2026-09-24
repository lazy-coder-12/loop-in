package com.loopin.app.detection.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.detection.DetectionManager
import com.loopin.app.detection.samples.SampleDebitAlert
import com.loopin.app.detection.samples.SampleDebitAlerts
import com.loopin.app.ui.components.LoopInPrimaryButton
import com.loopin.app.ui.theme.AccentGreen
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.ElectricBlue
import com.loopin.app.ui.theme.FluentIcons
import com.loopin.app.ui.theme.NeutralGray
import com.loopin.app.ui.theme.PureWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoDetectSheet(
    detectionManager: DetectionManager,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isEnabled by detectionManager.isListenerEnabled.collectAsState()
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PureWhite,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(scrollState)
        ) {
            // Drag pill & Close Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // RBI Compliance Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF4EC))
                        .border(1.dp, ElectricBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(FluentIcons.Sparkle),
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RBI E-Mandate Framework",
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = ElectricBlue
                    )
                }

                // Close Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(FluentIcons.Dismiss),
                        contentDescription = "Close",
                        tint = DeepMidnight,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Title
            Text(
                text = "Auto-Detect Subscriptions",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = DeepMidnight
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Since October 2021, the RBI has mandated that banks send pre-debit alerts 24 hours before any recurring auto-debit. Loop’n captures these alerts locally and builds your subscription records.",
                fontFamily = AppFontFamily,
                fontSize = 14.sp,
                color = NeutralGray,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Privacy Pillars Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF9FAFB))
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                PrivacyItem(
                    icon = FluentIcons.ShieldCheckmark,
                    title = "100% On-Device Processing",
                    subtitle = "Notifications are parsed strictly in local memory. No notification data is ever sent off your phone."
                )
                Spacer(modifier = Modifier.height(12.dp))
                PrivacyItem(
                    icon = FluentIcons.Dismiss,
                    title = "Zero Invasive Permissions",
                    subtitle = "No SMS reading permissions, no bank statement uploads, and no NetBanking logins required."
                )
                Spacer(modifier = Modifier.height(12.dp))
                PrivacyItem(
                    icon = FluentIcons.CheckmarkCircle,
                    title = "Automatic & Transparent",
                    subtitle = "Review and confirm each detected subscription with matched catalog plans and renewal dates."
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Status Card
            if (!isEnabled) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFFBEB))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD97706))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Notification Access Required",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Enable Notification Listener access in Android Settings so Loop'in can detect bank pre-debit notifications.",
                        fontFamily = AppFontFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF78350F),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    LoopInPrimaryButton(
                        onClick = { detectionManager.openNotificationListenerSettings() },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Enable in System Settings",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = PureWhite
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFECFDF5))
                        .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AccentGreen)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Detector Active & Listening",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF065F46)
                        )
                        Text(
                            text = "Loop'in is actively monitoring local pre-debit alerts.",
                            fontFamily = AppFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF047857)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFE5E7EB))
            Spacer(modifier = Modifier.height(16.dp))

            // Test Simulation Section
            Text(
                text = "Simulate Bank Pre-Debit Alerts",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = DeepMidnight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Test the detection engine instantly with realistic templates from Indian banks without waiting for live SMS or push notifications:",
                fontFamily = AppFontFamily,
                fontSize = 13.sp,
                color = NeutralGray,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sample alerts grid/cards
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SampleDebitAlerts.samples.forEach { sample ->
                    SampleAlertCard(
                        sample = sample,
                        onSimulate = {
                            detectionManager.simulateAlert(sample)
                            onDismiss()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PrivacyItem(
    icon: Int,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEFF6FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = ElectricBlue,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = DeepMidnight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontFamily = AppFontFamily,
                fontSize = 12.sp,
                color = NeutralGray,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun SampleAlertCard(
    sample: SampleDebitAlert,
    onSimulate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            .background(PureWhite)
            .clickable { onSimulate() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = sample.issuer.displayName,
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = ElectricBlue
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "•",
                    color = Color(0xFFD1D5DB)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = sample.expectedService,
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = DeepMidnight
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = sample.body,
                fontFamily = AppFontFamily,
                fontSize = 11.sp,
                color = NeutralGray,
                maxLines = 1,
                lineHeight = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF3F4F6))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Simulate",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = DeepMidnight
            )
        }
    }
}
