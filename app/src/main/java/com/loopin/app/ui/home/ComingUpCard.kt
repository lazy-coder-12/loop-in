package com.loopin.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.domain.model.RenewalAlert
import com.loopin.app.ui.components.MonogramTile
import com.loopin.app.ui.theme.AccentGreen
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.FluentIcons
import com.loopin.app.ui.theme.PureWhite

@Composable
fun ComingUpCard(
    alerts: List<RenewalAlert>,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Fast, responsive chevron rotation animation
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "arrowRotation"
    )

    // Full-width card with top rounded corners (24dp)
    // Clipped directly so the continuous striped background follows through behind the corners
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(DeepMidnight)
            .clickable { onToggleExpanded() }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(FluentIcons.Calendar),
                        contentDescription = "Renewals Calendar",
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    if (alerts.isNotEmpty()) {
                        val count = alerts.size
                        val annotatedText = buildAnnotatedString {
                            append("You’ve ")
                            withStyle(style = SpanStyle(color = AccentGreen, fontWeight = FontWeight.SemiBold)) {
                                append("$count upcoming renewal${if (count > 1) "s" else ""}")
                            }
                        }
                        Text(
                            text = annotatedText,
                            color = PureWhite,
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    } else {
                        Text(
                            text = "No renewals due in next 7 days",
                            color = PureWhite.copy(alpha = 0.85f),
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp
                        )
                    }
                }

                Icon(
                    painter = painterResource(FluentIcons.ChevronDown),
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = PureWhite,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotationAngle)
                )
            }

            // Snappy, responsive expansion and collapse
            AnimatedVisibility(
                visible = isExpanded && alerts.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 4.dp)
                ) {
                    alerts.take(5).forEachIndexed { index, alert ->
                        if (index > 0) {
                            HorizontalDivider(
                                color = PureWhite.copy(alpha = 0.1f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                        AlertItemRow(alert = alert)
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertItemRow(
    alert: RenewalAlert,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MonogramTile(
            name = alert.subscription.name,
            size = 38.dp,
            cornerRadius = 12.dp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = alert.subscription.name,
                color = PureWhite,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = alert.displayText,
                color = PureWhite.copy(alpha = 0.75f),
                fontFamily = AppFontFamily,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = alert.formattedUrgency,
            color = AccentGreen,
            fontFamily = AppFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}
