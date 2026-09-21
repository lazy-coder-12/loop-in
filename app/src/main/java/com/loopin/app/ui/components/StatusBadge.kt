package com.loopin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.ui.theme.FluentIcons
import com.loopin.app.ui.theme.LoopInTheme

enum class BadgeStatusType {
    REVIEW,
    ACTIVE,
    PAUSED,
    ENDED
}

/**
 * Accessible status badge that ALWAYS combines an icon and a text label (never color alone).
 * High-contrast container and content pairs strictly meet WCAG AA standards (> 4.5:1).
 */
@Composable
fun StatusBadge(
    status: BadgeStatusType,
    modifier: Modifier = Modifier,
    customLabel: String? = null
) {
    val colors = LoopInTheme.colors
    val shapes = LoopInTheme.shapes

    val (containerColor, contentColor, iconRes, defaultLabel) = when (status) {
        BadgeStatusType.REVIEW -> Quad(
            colors.warningContainer,
            colors.warningContent,
            FluentIcons.Search,
            "Review"
        )
        BadgeStatusType.ACTIVE -> Quad(
            colors.successContainer,
            colors.successContent,
            FluentIcons.Checkmark,
            "Active"
        )
        BadgeStatusType.PAUSED -> Quad(
            colors.infoContainer,
            colors.infoContent,
            FluentIcons.Pause,
            "Paused"
        )
        BadgeStatusType.ENDED -> Quad(
            colors.surfaceVariant,
            colors.failed,
            FluentIcons.Dismiss,
            "Ended"
        )
    }

    val labelText = customLabel ?: defaultLabel

    Row(
        modifier = modifier
            .clip(shapes.pill)
            .background(containerColor)
            .border(
                width = 1.dp,
                color = contentColor.copy(alpha = 0.2f),
                shape = shapes.pill
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .semantics {
                contentDescription = "Status: $labelText"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(11.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = labelText,
            color = contentColor,
            style = LoopInTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 13.sp
            )
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Preview
@Composable
fun StatusBadgePreview() {
    LoopInTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            StatusBadge(status = BadgeStatusType.REVIEW)
            Spacer(modifier = Modifier.width(8.dp))
            StatusBadge(status = BadgeStatusType.ACTIVE)
            Spacer(modifier = Modifier.width(8.dp))
            StatusBadge(status = BadgeStatusType.PAUSED)
            Spacer(modifier = Modifier.width(8.dp))
            StatusBadge(status = BadgeStatusType.ENDED)
        }
    }
}
