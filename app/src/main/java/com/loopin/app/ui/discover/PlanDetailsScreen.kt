package com.loopin.app.ui.discover

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.loopin.app.domain.model.SubscriptionSource
import com.loopin.app.domain.model.SubscriptionStatus
import com.loopin.app.ui.components.LoopInBottomBar
import com.loopin.app.ui.components.LoopInTab
import com.loopin.app.ui.components.ReportIssueOverlay
import com.loopin.app.ui.theme.AppFontFamily
import com.loopin.app.ui.theme.DangerRed
import com.loopin.app.ui.theme.DeepMidnight
import com.loopin.app.ui.theme.ElectricBlue
import com.loopin.app.ui.theme.LoopInTheme
import com.loopin.app.ui.theme.PureWhite
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun PlanDetailsScreen(
    serviceId: String,
    planId: String,
    repository: SubscriptionRepository? = null,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onTabSelected: (LoopInTab) -> Unit = {}
) {
    val service = remember(serviceId) {
        ServiceCatalogProvider.getServiceById(serviceId) ?: ServiceCatalogProvider.getServiceById("netflix")!!
    }

    val plan = remember(service, planId) {
        service.plans.find { it.id.equals(planId, ignoreCase = true) } ?: service.plans.first()
    }

    val subscriptions by (repository?.getSubscriptionsFlow()
        ?.collectAsState(initial = emptyList())
        ?: remember { mutableStateOf<List<Subscription>>(emptyList()) })

    PlanDetailsContent(
        service = service,
        plan = plan,
        userSubscriptions = subscriptions,
        repository = repository,
        modifier = modifier,
        onBackClick = onBackClick,
        onTabSelected = onTabSelected
    )
}

@Composable
fun PlanDetailsContent(
    service: CatalogService,
    plan: CatalogPlan,
    userSubscriptions: List<Subscription>,
    repository: SubscriptionRepository? = null,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onTabSelected: (LoopInTab) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var showReportOverlay by remember { mutableStateOf(false) }

    // Active subscription detection for this service
    val activeSubForService = remember(userSubscriptions, service) {
        userSubscriptions.firstOrNull { sub ->
            sub.name.contains(service.name, ignoreCase = true) ||
                service.name.contains(sub.name, ignoreCase = true)
        }
    }

    // Determine if this specific plan is currently active
    val isPlanActive: Boolean = remember(activeSubForService, plan, service.plans) {
        if (activeSubForService == null) {
            false
        } else {
            val matchedByName = service.plans.firstOrNull { p ->
                activeSubForService.name.contains(p.name, ignoreCase = true)
            }
            if (matchedByName != null) {
                matchedByName.id == plan.id
            } else {
                val matchedByPrice = service.plans.firstOrNull { p ->
                    activeSubForService.amountMinor == p.priceMinor
                }
                if (matchedByPrice != null) {
                    matchedByPrice.id == plan.id
                } else {
                    val fallbackId = service.plans.find { it.id == "standard" }?.id ?: service.plans.firstOrNull()?.id
                    fallbackId == plan.id
                }
            }
        }
    }

    fun handleAddSubscription() {
        if (repository != null) {
            coroutineScope.launch {
                val newSub = Subscription(
                    name = "${service.name} ${plan.name}",
                    amountMinor = plan.priceMinor,
                    billingCycle = plan.billingCycle,
                    nextDueDate = LocalDate.now().plusMonths(1),
                    status = SubscriptionStatus.ACTIVE,
                    source = SubscriptionSource.MANUAL,
                    category = service.category,
                    createdAt = System.currentTimeMillis()
                )
                repository.insertSubscription(newSub)
                snackbarHostState.showSnackbar("Added ${service.name} (${plan.name}) to your subscriptions!")
            }
        }
    }

    fun handleRemoveSubscription() {
        if (repository != null && activeSubForService != null) {
            coroutineScope.launch {
                repository.deleteSubscription(activeSubForService.id)
                snackbarHostState.showSnackbar("Removed ${activeSubForService.name} from your subscriptions.")
            }
        }
    }

    fun handleVisitWebsite() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(service.websiteUrl))
            context.startActivity(intent)
        } catch (_: Exception) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Unable to open browser")
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
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = PureWhite
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .statusBarsPadding()
                    .background(PureWhite)
            ) {
                // Top App Bar with back button, Details, and anchored 3-dots popup
                PlanDetailsHeader(
                    onBackClick = onBackClick,
                    onReportIssueClick = { showReportOverlay = true }
                )

                // Scrollable content area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Plan Header Row: Service Icon + Name & Resolution + Active Pill (if active)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Soft grey squircle behind service icon
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF3F4F6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = service.iconRes),
                                contentDescription = service.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = service.name,
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = DeepMidnight
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${plan.name} ${plan.resolution}",
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280)
                            )
                        }

                        // Green "Active" Pill if tracked
                        if (isPlanActive) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF16A34A))
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Active",
                                    fontFamily = AppFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = PureWhite
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // "Plan Details" & "🌐 Visit Website"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Plan Details",
                            fontFamily = AppFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DeepMidnight
                        )

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(
                                    role = Role.Button,
                                    onClickLabel = "Visit official pricing website"
                                ) { handleVisitWebsite() }
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(FluentIcons.Globe),
                                contentDescription = null,
                                tint = Color(0xFF0070FF),
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Visit Website",
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF0070FF)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Plan Features Box with checkmarks
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(PureWhite)
                            .border(BorderStroke(1.dp, Color(0xFFE5E7EB)), RoundedCornerShape(20.dp))
                            .padding(horizontal = 20.dp, vertical = 22.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            plan.features.forEach { feature ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        painter = painterResource(FluentIcons.Checkmark),
                                        contentDescription = null,
                                        tint = DeepMidnight,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .padding(top = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = feature,
                                        fontFamily = AppFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = DeepMidnight,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Bottom Action Button pinned above BottomBar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 14.dp)
                ) {
                    if (!isPlanActive) {
                        // "Add Subscription" button (Solid Electric Blue)
                        Button(
                            onClick = { handleAddSubscription() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricBlue,
                                contentColor = PureWhite
                            )
                        ) {
                            Text(
                                text = "Add Subscription",
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        // "Remove Subscription" button (Light pink container with red text)
                        Button(
                            onClick = { handleRemoveSubscription() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFEE2E2),
                                contentColor = DangerRed
                            )
                        ) {
                            Text(
                                text = "Remove Subscription",
                                fontFamily = AppFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Report Issue Overlay Modal
        if (showReportOverlay) {
            ReportIssueOverlay(
                serviceName = service.name,
                onDismiss = { showReportOverlay = false }
            )
        }
    }
}

/**
 * Top App Bar with back button, center Details/Subtitle, and anchored 3-dots more options button.
 * The DropdownMenu is nested directly inside the 3-dots Box so that Compose anchors it
 * right below the button on the top right edge.
 */
@Composable
private fun PlanDetailsHeader(
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
                    painter = painterResource(FluentIcons.Back),
                    contentDescription = "Back",
                    tint = DeepMidnight,
                    modifier = Modifier.size(20.dp)
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
                    painter = painterResource(FluentIcons.MoreVertical),
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
                            painter = painterResource(FluentIcons.Flag),
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

@Preview(showBackground = true)
@Composable
fun PlanDetailsScreenPreview() {
    LoopInTheme {
        PlanDetailsContent(
            service = ServiceCatalogProvider.getServiceById("netflix")!!,
            plan = ServiceCatalogProvider.getServiceById("netflix")!!.plans.first(),
            userSubscriptions = emptyList()
        )
    }
}
