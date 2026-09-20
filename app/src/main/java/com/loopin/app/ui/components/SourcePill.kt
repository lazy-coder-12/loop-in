package com.loopin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.ui.theme.LoopInTheme

enum class SubscriptionSourceType {
    AUTO,
    MANUAL
}

/**
 * Small pill badge indicating whether a subscription was detected automatically (via pre-debit alerts)
 * or manually added by the user.
 */
@Composable
fun SourcePill(
    source: SubscriptionSourceType,
    modifier: Modifier = Modifier
) {
    val colors = LoopInTheme.colors
    val shapes = LoopInTheme.shapes

    val label = if (source == SubscriptionSourceType.AUTO) "Auto" else "Manual"
    val dotColor = if (source == SubscriptionSourceType.AUTO) colors.primary else colors.textSecondary

    Row(
        modifier = modifier
            .clip(shapes.pill)
            .background(colors.surfaceVariant)
            .border(
                width = 1.dp,
                color = colors.border,
                shape = shapes.pill
            )
            .padding(horizontal = 7.dp, vertical = 2.dp)
            .semantics {
                contentDescription = "Source: $label"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = colors.textSecondary,
            style = LoopInTheme.typography.labelMedium.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 12.sp
            )
        )
    }
}

@Preview
@Composable
fun SourcePillPreview() {
    LoopInTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            SourcePill(source = SubscriptionSourceType.AUTO)
            Spacer(modifier = Modifier.width(8.dp))
            SourcePill(source = SubscriptionSourceType.MANUAL)
        }
    }
}
