package com.loopin.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.ElectricBlue
import com.loopin.app.ui.theme.FluentIcons
import com.loopin.app.ui.theme.LoopInTheme
import com.loopin.app.ui.theme.NeutralGray
import com.loopin.app.ui.theme.PureWhite
import kotlinx.coroutines.launch

@Composable
fun ReportIssueOverlay(
    serviceName: String = "this service",
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    var isSubmitted by remember { mutableStateOf(false) }

    var plansMismatch by remember { mutableStateOf(false) }
    var pricingMismatch by remember { mutableStateOf(false) }
    var otherChecked by remember { mutableStateOf(false) }
    var otherReasonText by remember { mutableStateOf("") }

    // Opening animation
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

    fun handleDismiss() {
        coroutineScope.launch {
            launch {
                alpha.animateTo(0f, animationSpec = tween(160))
            }
            scale.animateTo(0.75f, animationSpec = tween(160, easing = FastOutSlowInEasing))
            onDismiss()
        }
    }

    val isSubmitEnabled = (plansMismatch || pricingMismatch || (otherChecked && otherReasonText.isNotBlank()))

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f * alpha.value))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClickLabel = "Dismiss report dialog"
                ) { handleDismiss() }
        )

        // Overlay Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .scale(scale.value)
                .clip(RoundedCornerShape(24.dp))
                .background(PureWhite)
                .border(BorderStroke(1.dp, Color(0xFFF0F2F5)), RoundedCornerShape(24.dp))
                .padding(22.dp)
        ) {
            if (!isSubmitted) {
                // Form Content
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Report an issue",
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = DeepMidnight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Help us keep $serviceName plans accurate",
                                fontFamily = AppFontFamily,
                                fontSize = 13.sp,
                                color = NeutralGray
                            )
                        }

                        // Close Button (Pink squircle with red circle & white 'X')
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable(
                                    role = Role.Button,
                                    onClickLabel = "Close dialog"
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

                    Spacer(modifier = Modifier.height(18.dp))

                    // Option 1: Plans mismatch
                    ReportCheckboxRow(
                        text = "Plans mismatch",
                        checked = plansMismatch,
                        onCheckedChange = { plansMismatch = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option 2: Pricing mismatch
                    ReportCheckboxRow(
                        text = "Pricing mismatch",
                        checked = pricingMismatch,
                        onCheckedChange = { pricingMismatch = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option 3: Other
                    ReportCheckboxRow(
                        text = "Other",
                        checked = otherChecked,
                        onCheckedChange = { otherChecked = it }
                    )

                    // Text Input for Other
                    AnimatedVisibility(
                        visible = otherChecked,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = otherReasonText,
                                onValueChange = { otherReasonText = it },
                                placeholder = {
                                    Text(
                                        text = "Please specify the issue...",
                                        fontFamily = AppFontFamily,
                                        fontSize = 14.sp,
                                        color = NeutralGray
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricBlue,
                                    unfocusedBorderColor = Color(0xFFE5E7EB),
                                    unfocusedContainerColor = PureWhite,
                                    focusedContainerColor = PureWhite
                                ),
                                minLines = 2,
                                maxLines = 4
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    Button(
                        onClick = { isSubmitted = true },
                        enabled = isSubmitEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            disabledContainerColor = Color(0xFFE5E7EB),
                            contentColor = PureWhite,
                            disabledContentColor = Color(0xFF9CA3AF)
                        )
                    ) {
                        Text(
                            text = "Submit",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                // Confirmation / Success Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Green check circle
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(FluentIcons.Checkmark),
                            contentDescription = "Success",
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Issue Registered",
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = DeepMidnight
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Thank you for letting us know! We have registered your report and our catalog team will review the details for $serviceName.",
                        fontFamily = AppFontFamily,
                        fontSize = 14.sp,
                        color = NeutralGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { handleDismiss() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            contentColor = PureWhite
                        )
                    ) {
                        Text(
                            text = "Done",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportCheckboxRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = ElectricBlue,
                uncheckedColor = Color(0xFF9CA3AF),
                checkmarkColor = PureWhite
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontFamily = AppFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = DeepMidnight
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportIssueOverlayPreview() {
    LoopInTheme {
        ReportIssueOverlay(
            serviceName = "Netflix",
            onDismiss = {}
        )
    }
}
