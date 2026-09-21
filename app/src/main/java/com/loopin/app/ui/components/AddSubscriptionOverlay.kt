package com.loopin.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.ElectricBlue
import com.loopin.app.ui.theme.FluentIcons
import com.loopin.app.ui.theme.LoopInTheme
import com.loopin.app.ui.theme.PureWhite
import kotlinx.coroutines.launch

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role

@Composable
fun AddSubscriptionOverlay(
    onDismiss: () -> Unit,
    onManualEntry: () -> Unit,
    onAutoDetect: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    // Opening animation: scaling animation with a little bouncy effect at the end
    LaunchedEffect(Unit) {
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200)
            )
        }
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    // Closing animation on Manual Entry click:
    // Scaling down animation with bouncy effect at the start of the animation
    fun handleManualEntryClick() {
        coroutineScope.launch {
            // Anticipatory bounce up at the start
            scale.animateTo(
                targetValue = 1.06f,
                animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing)
            )
            // Then swift scale down with spring bounce and fade out
            launch {
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 180)
                )
            }
            scale.animateTo(
                targetValue = 0.65f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            onManualEntry()
        }
    }

    // Dismiss animation on 'X' or scrim click
    fun handleDismiss() {
        coroutineScope.launch {
            launch {
                alpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 160)
                )
            }
            scale.animateTo(
                targetValue = 0.75f,
                animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing)
            )
            onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Dimmed Scrim Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f * alpha.value))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClickLabel = "Dismiss overlay"
                ) {
                    handleDismiss()
                }
        )

        // Overlay Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .scale(scale.value)
                .clip(RoundedCornerShape(24.dp))
                .background(PureWhite)
                .border(
                    BorderStroke(1.dp, Color(0xFFF0F2F5)),
                    RoundedCornerShape(24.dp)
                )
                .padding(22.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Row: "Add New Subscription" + Red Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Add New Subscription",
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = DeepMidnight
                    )

                    // Close Button: 48dp accessible touch container around the pink squircle
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(
                                role = Role.Button,
                                onClickLabel = "Close overlay"
                            ) { handleDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(FluentIcons.Dismiss),
                                    contentDescription = "Close",
                                    tint = PureWhite,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Option 1: Auto Detect Subscriptions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            RoundedCornerShape(18.dp)
                        )
                        .clickable(
                            role = Role.Button,
                            onClickLabel = "Auto detect subscriptions from notifications"
                        ) {
                            onAutoDetect()
                            handleDismiss()
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Microchip Icon in light blue squircle
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(FluentIcons.Sparkle),
                            contentDescription = "Auto Detect",
                            tint = ElectricBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto Detect Subscriptions",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = DeepMidnight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "The auto-detection-engine will automatically detect renewal alerts. No manual entry required!",
                            fontFamily = AppFontFamily,
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280),
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        painter = painterResource(FluentIcons.ChevronRight),
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Option 2: Manual Entry
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            RoundedCornerShape(18.dp)
                        )
                        .clickable(
                            role = Role.Button,
                            onClickLabel = "Open manual entry form"
                        ) { handleManualEntryClick() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notepad / Document Edit Icon in light blue squircle
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(FluentIcons.DocumentEdit),
                            contentDescription = "Manual Entry",
                            tint = ElectricBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Manual Entry",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = DeepMidnight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "A form to help you manually enter the details to help you keep track of the subscriptions.",
                            fontFamily = AppFontFamily,
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280),
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        painter = painterResource(FluentIcons.ChevronRight),
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddSubscriptionOverlayPreview() {
    LoopInTheme {
        AddSubscriptionOverlay(
            onDismiss = {},
            onManualEntry = {}
        )
    }
}
