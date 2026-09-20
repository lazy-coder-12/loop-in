package com.loopin.app.ui.discover

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopin.app.data.catalog.CatalogPlan
import com.loopin.app.data.catalog.CatalogService
import com.loopin.app.data.catalog.ServiceCatalogProvider
import com.loopin.app.data.repository.SubscriptionRepository
import com.loopin.app.domain.model.Subscription
import com.loopin.app.ui.components.LoopInBottomBar
import com.loopin.app.ui.components.LoopInTab
import com.loopin.app.ui.components.ReportIssueOverlay
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.LoopInTheme
import com.loopin.app.ui.theme.PureWhite

@Composable
fun ServicePlansScreen(
    serviceId: String,
    repository: SubscriptionRepository? = null,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onPlanClick: (serviceId: String, planId: String) -> Unit = { _, _ -> },
    onTabSelected: (LoopInTab) -> Unit = {}
) {
    val service = remember(serviceId) {
        ServiceCatalogProvider.getServiceById(serviceId) ?: ServiceCatalogProvider.getServiceById("netflix")!!
    }

    val subscriptions by (repository?.getSubscriptionsFlow()
        ?.collectAsState(initial = emptyList())
        ?: remember { mutableStateOf<List<Subscription>>(emptyList()) })

    ServicePlansContent(
        service = service,
        userSubscriptions = subscriptions,
        modifier = modifier,
        onBackClick = onBackClick,
        onPlanClick = { planId -> onPlanClick(service.id, planId) },
        onTabSelected = onTabSelected
    )
}

@Composable
fun ServicePlansContent(
    service: CatalogService,
    userSubscriptions: List<Subscription>,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onPlanClick: (planId: String) -> Unit = {},
    onTabSelected: (LoopInTab) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showReportOverlay by remember { mutableStateOf(false) }

    // Exactly one active subscription can exist for this service
    val activeSubForService = remember(userSubscriptions, service) {
        userSubscriptions.firstOrNull { sub ->
            sub.name.contains(service.name, ignoreCase = true) ||
                service.name.contains(sub.name, ignoreCase = true)
        }
    }

    // Determine the exact single active plan ID (at most ONE card will ever have the status pill)
    val activePlanId: String? = remember(activeSubForService, service.plans) {
        if (activeSubForService == null) {
            null
        } else {
            // 1. Try matching plan name within subscription name (e.g. "Netflix Standard")
            val matchedByName = service.plans.firstOrNull { plan ->
                activeSubForService.name.contains(plan.name, ignoreCase = true)
            }
            if (matchedByName != null) {
                matchedByName.id
            } else {
                // 2. Try matching by exact price
                val matchedByPrice = service.plans.firstOrNull { plan ->
                    activeSubForService.amountMinor == plan.priceMinor
                }
                if (matchedByPrice != null) {
                    matchedByPrice.id
                } else {
                    // 3. Fallback: if user subscribed to "Netflix", default to "standard" (as per mockup) or first plan
                    service.plans.find { it.id == "standard" }?.id ?: service.plans.firstOrNull()?.id
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                LoopInBottomBar(
                    selectedTab = LoopInTab.DISCOVER,
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
                // Top App Bar with Details, subtitle, back button and anchored 3-dots popup
                ServicePlansHeader(
                    onBackClick = onBackClick,
                    onReportIssueClick = { showReportOverlay = true }
                )

                // Service Hero (Icon, Name, Plans Count, Orange Diamonds)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Soft grey squircle behind Netflix icon
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(Color(0xFFF3F4F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = service.iconRes),
                            contentDescription = service.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(86.dp)
                                .clip(RoundedCornerShape(18.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = service.name,
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = DeepMidnight
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${service.plans.size} Plans",
                        fontFamily = AppFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4 Orange Rotated Diamonds
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(service.plans.size) {
                            Canvas(modifier = Modifier.size(9.dp)) {
                                val path = Path().apply {
                                    moveTo(size.width / 2f, 0f)
                                    lineTo(size.width, size.height / 2f)
                                    lineTo(size.width / 2f, size.height)
                                    lineTo(0f, size.height / 2f)
                                    close()
                                }
                                drawPath(path, color = Color(0xFFE95810))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // "Plans" Header
                Text(
                    text = "Plans",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = DeepMidnight,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // List of Plan Cards
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    service.plans.forEach { plan ->
                        val isThisPlanActive = (plan.id == activePlanId)
                        PlanRowCard(
                            plan = plan,
                            isActive = isThisPlanActive,
                            onClick = { onPlanClick(plan.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }

        // Report Issue Overlay Modal with Spring Animation
        if (showReportOverlay) {
            ReportIssueOverlay(
                serviceName = service.name,
                onDismiss = { showReportOverlay = false }
            )
        }
    }
}

@Composable
private fun PlanRowCard(
    plan: CatalogPlan,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PureWhite)
            .border(BorderStroke(1.dp, Color(0xFFE5E7EB)), RoundedCornerShape(16.dp))
            .clickable(
                role = Role.Button,
                onClickLabel = "View ${plan.name} details"
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dual Overlapping Devices Screen Icon (Exact matching mockup)
        OverlappingScreensIcon(
            tint = DeepMidnight,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Plan Name & Resolution
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = plan.name,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = DeepMidnight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = plan.resolution,
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
        }

        // Active Sub Pill (green) - Only shown on the single active card
        if (isActive) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF16A34A))
                    .padding(horizontal = 13.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Active Sub",
                    fontFamily = AppFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = PureWhite
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        // Trailing Chevron
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Top App Bar with back button, center Details/Subtitle, and anchored 3-dots more options button.
 * The DropdownMenu is nested directly inside the 3-dots Box so that Compose anchors it
 * right below the button on the top right edge.
 */
@Composable
private fun ServicePlansHeader(
    onBackClick: () -> Unit,
    onReportIssueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button: 48dp accessible touch target with squircle background
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
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos,
                    contentDescription = "Back",
                    tint = DeepMidnight,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(start = 2.dp)
                )
            }
        }

        // Center Title & Subtitle
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Details",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = DeepMidnight
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Subscription plans and info",
                fontFamily = AppFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
        }

        // More Options Button (3 vertical dots): anchored Box
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PureWhite)
                    .border(BorderStroke(1.dp, Color(0xFFE5E7EB)), RoundedCornerShape(16.dp))
                    .clickable(
                        role = Role.Button,
                        onClickLabel = "More options"
                    ) { isMenuExpanded = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "More options",
                    tint = DeepMidnight,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Anchored DropdownMenu right below the 3-dots button
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                offset = DpOffset(x = 0.dp, y = 4.dp),
                modifier = Modifier
                    .background(PureWhite, RoundedCornerShape(14.dp))
                    .border(BorderStroke(1.dp, Color(0xFFE5E7EB)), RoundedCornerShape(14.dp))
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Report an issue",
                            fontFamily = AppFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = DeepMidnight
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = null,
                            tint = DeepMidnight,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = {
                        isMenuExpanded = false
                        onReportIssueClick()
                    }
                )
            }
        }
    }
}

/**
 * Custom Canvas drawing two overlapping rounded screens with crisp lines matching the UI mockup.
 */
@Composable
fun OverlappingScreensIcon(
    modifier: Modifier = Modifier,
    tint: Color = DeepMidnight
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val strokeWidth = 2.dp.toPx()
        val cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        val rectWidth = size.width * 0.60f
        val rectHeight = size.height * 0.68f

        // Back screen (shifted up and right)
        val backLeft = size.width * 0.28f
        val backTop = size.height * 0.08f
        drawRoundRect(
            color = tint,
            topLeft = Offset(backLeft, backTop),
            size = Size(rectWidth, rectHeight),
            cornerRadius = cornerRadius,
            style = Stroke(width = strokeWidth)
        )

        // Front screen (shifted down and left)
        val frontLeft = size.width * 0.08f
        val frontTop = size.height * 0.22f

        // Fill background first to mask out overlapping lines from the back screen
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(frontLeft, frontTop),
            size = Size(rectWidth, rectHeight),
            cornerRadius = cornerRadius,
            style = Fill
        )

        // Draw outline of front screen
        drawRoundRect(
            color = tint,
            topLeft = Offset(frontLeft, frontTop),
            size = Size(rectWidth, rectHeight),
            cornerRadius = cornerRadius,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ServicePlansScreenPreview() {
    LoopInTheme {
        ServicePlansContent(
            service = ServiceCatalogProvider.getServiceById("netflix")!!,
            userSubscriptions = emptyList()
        )
    }
}
