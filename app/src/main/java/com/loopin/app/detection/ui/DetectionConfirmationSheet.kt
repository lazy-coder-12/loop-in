package com.loopin.app.detection.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.data.catalog.ServiceCatalogProvider
import com.loopin.app.detection.model.ParsedDebitAlert
import com.loopin.app.ui.components.LoopInPrimaryButton
import com.loopin.app.ui.components.MonogramTile
import com.loopin.app.ui.theme.AccentGreen
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.ElectricBlue
import com.loopin.app.ui.theme.FluentIcons
import com.loopin.app.ui.theme.NeutralGray
import com.loopin.app.ui.theme.PureWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionConfirmationSheet(
    alert: ParsedDebitAlert,
    onConfirm: () -> Unit,
    onEdit: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showRawDetails by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val catalogService = remember(alert.matchedCatalogId) {
        alert.matchedCatalogId?.let { ServiceCatalogProvider.getServiceById(it) }
    }

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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag handle pill
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pre-Debit Alert Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFECFDF5))
                    .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(AccentGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "RBI Pre-Debit Alert Detected",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color(0xFF065F46)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Service Icon / Monogram
            Box(contentAlignment = Alignment.Center) {
                if (catalogService != null && catalogService.iconRes != 0) {
                    Image(
                        painter = painterResource(catalogService.iconRes),
                        contentDescription = alert.matchedServiceName,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                } else {
                    MonogramTile(
                        name = alert.matchedServiceName,
                        size = 72.dp,
                        cornerRadius = 20.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Service Name
            Text(
                text = alert.matchedServiceName,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = DeepMidnight,
                textAlign = TextAlign.Center
            )

            // Matched Plan Pill if available
            if (alert.matchedPlanName != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        painter = painterResource(FluentIcons.Checkmark),
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Catalog Plan: ${alert.matchedPlanName}",
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = ElectricBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Subscription Spec Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF9FAFB))
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                // Amount
                SpecRow(
                    label = "Detected Amount",
                    value = alert.formattedAmount,
                    highlight = true
                )
                HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 10.dp))

                // Next Due Date
                SpecRow(
                    label = "Scheduled Debit Date",
                    value = alert.formattedDate
                )
                HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 10.dp))

                // Bank / Card Source
                val cardOrAcct = alert.accountOrCardMask ?: "E-Mandate"
                SpecRow(
                    label = "Bank & Source",
                    value = "${alert.bankIssuer.displayName} ($cardOrAcct)"
                )

                if (alert.mandateRef != null) {
                    HorizontalDivider(color = Color(0xFFF3F4F6), modifier = Modifier.padding(vertical = 10.dp))
                    SpecRow(
                        label = "Mandate Ref (UMRN)",
                        value = alert.mandateRef
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Transparency Accordion: View raw bank notification
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { showRawDetails = !showRawDetails }
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (showRawDetails) "Hide original bank message" else "View original bank message",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = ElectricBlue
                )
                Icon(
                    painter = painterResource(if (showRawDetails) FluentIcons.ChevronDown else FluentIcons.ChevronRight),
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(16.dp)
                )
            }

            if (showRawDetails) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(12.dp)
                ) {
                    Text(
                        text = alert.rawText.ifBlank { alert.rawTitle },
                        fontFamily = AppFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF4B5563),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Action: Confirm & Add Subscription
            LoopInPrimaryButton(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Confirm & Add Subscription",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = PureWhite
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Action: Edit Details
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB))
            ) {
                Icon(
                    painter = painterResource(FluentIcons.Edit),
                    contentDescription = null,
                    tint = DeepMidnight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit Details",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = DeepMidnight
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Dismiss
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Dismiss",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = NeutralGray
                )
            }
        }
    }
}

@Composable
private fun SpecRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = AppFontFamily,
            fontSize = 13.sp,
            color = NeutralGray
        )
        Text(
            text = value,
            fontFamily = AppFontFamily,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = if (highlight) 17.sp else 14.sp,
            color = DeepMidnight
        )
    }
}
