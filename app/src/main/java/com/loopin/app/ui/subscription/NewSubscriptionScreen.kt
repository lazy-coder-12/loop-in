package com.loopin.app.ui.subscription

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.loopin.app.ui.theme.FluentIcons
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.data.repository.SubscriptionRepository
import com.loopin.app.domain.model.BillingCycle
import com.loopin.app.domain.model.Subscription
import com.loopin.app.domain.model.SubscriptionSource
import com.loopin.app.domain.model.SubscriptionStatus
import com.loopin.app.ui.components.LoopInBottomBar
import com.loopin.app.ui.components.LoopInTab
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.ElectricBlue
import com.loopin.app.ui.theme.LoopInTheme
import com.loopin.app.ui.theme.NeutralGray
import com.loopin.app.ui.theme.PureWhite
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun NewSubscriptionScreen(
    modifier: Modifier = Modifier,
    repository: SubscriptionRepository? = null,
    onBackClick: () -> Unit = {},
    onCancelClick: () -> Unit = onBackClick,
    onSaveSuccess: () -> Unit = {},
    onTabSelected: (LoopInTab) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var subscriptionName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Entertainment") }
    var isCategoryDropdownOpen by remember { mutableStateOf(false) }

    var selectedBillingCycle by remember { mutableStateOf("Monthly") }
    val billingCycles = listOf("Weekly", "Monthly", "Quarterly", "Yearly")

    var selectedPlan by remember { mutableStateOf("Standard Plan") }
    var isPlanDropdownOpen by remember { mutableStateOf(false) }

    val categories = listOf(
        "Entertainment",
        "Music",
        "Productivity",
        "Cloud Storage",
        "Food & Lifestyle",
        "Fitness",
        "Other"
    )

    val plans = listOf(
        "Basic Plan",
        "Standard Plan",
        "Premium Plan"
    )

    // Dynamic Plan Name and Price based on selected plan
    val (planDetailName, planPrice, amountMinor) = when (selectedPlan) {
        "Basic Plan" -> Triple("Basic 720p", "₹ 199", 19900L)
        "Standard Plan" -> Triple("Standard 1080p", "₹ 499", 49900L)
        "Premium Plan" -> Triple("Premium 4K HDR", "₹ 649", 64900L)
        else -> Triple("Standard 1080p", "₹ 499", 49900L)
    }

    val scrollState = rememberScrollState()

    fun handleSave() {
        val finalName = if (subscriptionName.isNotBlank()) subscriptionName.trim() else "Netflix"
        val cycleEnum = when (selectedBillingCycle) {
            "Weekly" -> BillingCycle.WEEKLY
            "Quarterly" -> BillingCycle.QUARTERLY
            "Yearly" -> BillingCycle.YEARLY
            else -> BillingCycle.MONTHLY
        }

        if (repository != null) {
            coroutineScope.launch {
                val newSub = Subscription(
                    name = finalName,
                    amountMinor = amountMinor,
                    billingCycle = cycleEnum,
                    nextDueDate = LocalDate.now().plusMonths(1),
                    status = SubscriptionStatus.ACTIVE,
                    source = SubscriptionSource.MANUAL,
                    category = selectedCategory,
                    createdAt = System.currentTimeMillis()
                )
                repository.insertSubscription(newSub)
                onSaveSuccess()
            }
        } else {
            onSaveSuccess()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            LoopInBottomBar(
                selectedTab = LoopInTab.NEW,
                onTabSelected = onTabSelected
            )
        },
        containerColor = PureWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .background(PureWhite)
        ) {
            // Header: Back Button, Title + Subtitle, Red Close Button
            NewSubscriptionHeader(
                onBackClick = onBackClick,
                onCloseClick = onCancelClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Subscription Name
                Text(
                    text = "Subscription Name",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = DeepMidnight
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = subscriptionName,
                    onValueChange = { subscriptionName = it },
                    placeholder = {
                        Text(
                            text = "e.g Netflix",
                            fontFamily = AppFontFamily,
                            fontSize = 14.sp,
                            color = NeutralGray
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        unfocusedContainerColor = PureWhite,
                        focusedContainerColor = PureWhite
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Category Dropdown
                Text(
                    text = "Category",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = DeepMidnight
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                RoundedCornerShape(12.dp)
                            )
                            .background(PureWhite)
                            .clickable { isCategoryDropdownOpen = true }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = selectedCategory,
                            fontFamily = AppFontFamily,
                            fontSize = 14.sp,
                            color = DeepMidnight
                        )
                        Icon(
                            painter = painterResource(FluentIcons.ChevronDown),
                            contentDescription = "Dropdown",
                            tint = DeepMidnight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = isCategoryDropdownOpen,
                        onDismissRequest = { isCategoryDropdownOpen = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = category,
                                        fontFamily = AppFontFamily,
                                        fontSize = 14.sp
                                    )
                                },
                                onClick = {
                                    selectedCategory = category
                                    isCategoryDropdownOpen = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Billing Cycle
                Text(
                    text = "Billing Cycle",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = DeepMidnight
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    billingCycles.forEach { cycle ->
                        val isSelected = cycle == selectedBillingCycle
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ElectricBlue else PureWhite)
                                .then(
                                    if (!isSelected) {
                                        Modifier.border(
                                            BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                            RoundedCornerShape(12.dp)
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable { selectedBillingCycle = cycle },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cycle,
                                fontFamily = AppFontFamily,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 13.sp,
                                color = if (isSelected) PureWhite else Color(0xFF6B7280)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Subscription Plan Dropdown
                Text(
                    text = "Subscription Plan",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = DeepMidnight
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                RoundedCornerShape(12.dp)
                            )
                            .background(PureWhite)
                            .clickable { isPlanDropdownOpen = true }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = selectedPlan,
                            fontFamily = AppFontFamily,
                            fontSize = 14.sp,
                            color = DeepMidnight
                        )
                        Icon(
                            painter = painterResource(FluentIcons.ChevronDown),
                            contentDescription = "Dropdown",
                            tint = DeepMidnight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = isPlanDropdownOpen,
                        onDismissRequest = { isPlanDropdownOpen = false }
                    ) {
                        plans.forEach { plan ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = plan,
                                        fontFamily = AppFontFamily,
                                        fontSize = 14.sp
                                    )
                                },
                                onClick = {
                                    selectedPlan = plan
                                    isPlanDropdownOpen = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Plan Details Section
                Text(
                    text = "Plan Details",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DeepMidnight
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Plan Name",
                        fontFamily = AppFontFamily,
                        fontSize = 14.sp,
                        color = NeutralGray
                    )
                    Text(
                        text = planDetailName,
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = DeepMidnight
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Price",
                        fontFamily = AppFontFamily,
                        fontSize = 14.sp,
                        color = NeutralGray
                    )
                    Text(
                        text = planPrice,
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = DeepMidnight
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                Spacer(modifier = Modifier.height(24.dp))

                // "Add Subscription" Action Button
                Button(
                    onClick = { handleSave() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricBlue,
                        contentColor = PureWhite
                    )
                ) {
                    Text(
                        text = "Add Subscription",
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // "Cancel" Action Button
                OutlinedButton(
                    onClick = onCancelClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = PureWhite,
                        contentColor = DeepMidnight
                    )
                ) {
                    Text(
                        text = "Cancel",
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = DeepMidnight
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun NewSubscriptionHeader(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button: 48dp accessible touch target
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable(
                    role = Role.Button,
                    onClickLabel = "Navigate back"
                ) { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF2F4F7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(FluentIcons.Back),
                    contentDescription = "Back",
                    tint = DeepMidnight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Centered Title & Subtitle
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "New Subscription",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = DeepMidnight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Add details to start tracking",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = NeutralGray
            )
        }

        // Close Button: 48dp accessible touch target
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable(
                    role = Role.Button,
                    onClickLabel = "Close form"
                ) { onCloseClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFEE2E2)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(FluentIcons.Dismiss),
                        contentDescription = "Close",
                        tint = PureWhite,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewSubscriptionScreenPreview() {
    LoopInTheme {
        NewSubscriptionScreen()
    }
}
